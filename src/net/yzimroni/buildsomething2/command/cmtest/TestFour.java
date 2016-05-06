package net.yzimroni.buildsomething2.command.cmtest;

import java.util.HashMap;
import java.util.UUID;

import net.yzimroni.buildsomething2.BuildSomethingPlugin;
import net.yzimroni.buildsomething2.game.Gamemode;
import net.yzimroni.buildsomething2.game.games.BuildersGame;
import net.yzimroni.buildsomething2.game.games.Game;
import net.yzimroni.commandmanager.command.Command;
import net.yzimroni.commandmanager.command.CommandExecutor;
import net.yzimroni.commandmanager.command.SubCommand;
import net.yzimroni.commandmanager.command.args.ArgumentData;
import net.yzimroni.commandmanager.manager.CommandManager;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class TestFour extends CommandExecutor {
	private BuildSomethingPlugin plugin;
	private final UUID CONSOLE_UUID = UUID.randomUUID();
	private HashMap<UUID, UUID> selects;

	public TestFour(BuildSomethingPlugin p) {
		plugin = p;
		selects = new HashMap<UUID, UUID>();
		initCommands();
	}

	private UUID getUuid(CommandSender sender) {
		UUID u = null;
		if (sender instanceof ConsoleCommandSender) {
			u = CONSOLE_UUID;
		} else if (sender instanceof Player) {
			u = ((Player) sender).getUniqueId();
		}
		return u;
	}

	private Game getGame(UUID u) {
		if (selects.containsKey(u)) {
			return plugin.getGameManager().getGameById(selects.get(u));
		}
		return null;
	}

	private void initCommands() {
		Command game = new Command("game", "Build Something command", "buildsomething.command", this);
		game.setAliases("bs", "buildsomething");

		SubCommand games = new SubCommand("games", "List of running games", this);
		game.addSubCommand(games);

		SubCommand start = new SubCommand("start", "Start a game", this);
		game.addSubCommand(start);

		SubCommand stop = new SubCommand("stop", "Stop a game", this);
		game.addSubCommand(stop);

		CommandManager.get().registerCommand(plugin, game);
	}

	@Override
	public boolean executeCommand(CommandSender sender, Command command, ArgumentData args) {
		UUID u = getUuid(sender);
		if (u == null) return false;

		switch (command.getName()) {
		case "game":
			return game(sender, command, args, u);
		case "games":
			games(sender, command, args, u);
		case "start":
			start(sender, command, args, u);
		case "stop":
			stop(sender, command, args, u);
		default:
			break;
		}
		return false;
	}

	private boolean game(CommandSender sender, Command command, ArgumentData args, UUID u) {
		Game g = getGame(u);
		if (g != null) {
			sender.sendMessage("Game selected: " + plugin.getGameManager().getGames().indexOf(g));
		}
		command.printHelp(sender);
		return true;
	}

	private boolean games(CommandSender sender, Command command, ArgumentData args, UUID u) {
		if (plugin.getGameManager().getGames().isEmpty()) {
			sender.sendMessage("There is no games open right now :(");
		} else {
			sender.sendMessage("Open games:");
			for (Game game : plugin.getGameManager().getGames()) {
				int index = plugin.getGameManager().getGames().indexOf(game);
				String builders = "";
				if (game instanceof BuildersGame) {
					BuildersGame bs = (BuildersGame) game;
					builders = (!bs.getBuilders().isEmpty() ? " | " + bs.getBuilders() : "");
				}
				sender.sendMessage("" + index + " | " + game.getGameType() + " | " + game.getMode() + " | " + game.getPlayers().size() + "/" + game.getMaxPlayers() + builders);
			}
		}
		return true;
	}

	private boolean start(CommandSender sender, Command command, ArgumentData args, UUID u) {
		Game g = getGame(u);

		if (g == null) {
			sender.sendMessage("You must select a game");
			return false;
		}

		if (g.getMode() == Gamemode.LOBBY) {
			g.setForceStart(true);
			g.countdown();
			sender.sendMessage("Game started!");
		} else {
			sender.sendMessage("Cant start the game");
		}

		return true;
	}

	private boolean stop(CommandSender sender, Command command, ArgumentData args, UUID u) {
		Game g = getGame(u);

		if (g == null) {
			sender.sendMessage("You must select a game");
			return false;
		}

		if (g.getMode() == Gamemode.RUNNING) {
			g.stop(true, false);
			sender.sendMessage("Game stopped");
		} else {
			sender.sendMessage("The game not running");
		}
		return true;
	}

}
