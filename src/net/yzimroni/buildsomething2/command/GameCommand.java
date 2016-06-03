package net.yzimroni.buildsomething2.command;

import java.util.HashMap;
import java.util.UUID;

import net.yzimroni.buildsomething2.BuildSomethingPlugin;
import net.yzimroni.buildsomething2.game.Gamemode;
import net.yzimroni.buildsomething2.game.games.BuildersGame;
import net.yzimroni.buildsomething2.game.games.Game;
import net.yzimroni.buildsomething2.utils.Utils;
import net.yzimroni.commandmanager.command.Command;
import net.yzimroni.commandmanager.command.MethodExecutor;
import net.yzimroni.commandmanager.command.SubCommand;
import net.yzimroni.commandmanager.command.args.ArgumentData;
import net.yzimroni.commandmanager.command.args.arguments.BooleanArgument;
import net.yzimroni.commandmanager.command.args.arguments.FlagsArgument;
import net.yzimroni.commandmanager.command.args.arguments.IntegerArgument;
import net.yzimroni.commandmanager.command.args.arguments.PlayerArgument;
import net.yzimroni.commandmanager.manager.CommandManager;
import net.yzimroni.commandmanager.utils.MethodId;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class GameCommand {
	private BuildSomethingPlugin plugin;
	private final UUID CONSOLE_UUID = UUID.randomUUID();
	private HashMap<UUID, UUID> selects;
	
	public GameCommand(BuildSomethingPlugin p) {
		plugin = p;
		selects = new HashMap<UUID, UUID>();
	}
	
	/*
	 * Commands:
	 * bs games
	 * bs sel
	 * bs start
	 * bs stop
	 * bs word
	 * bs builder
	 * bs builder add
	 * bs builder remove
	 * bs builder reset
	 * bs info
	 * bs join
	 * bs leave
	 * bs opengames (-f)
	 * bs close
	 * bs close all
	 * bs setmaxplayers (-g)
	 */
	
	protected void createCommands() {
		Command game = new Command("game", "BuildSomething2 game command", "buildsomething2.command.game", MethodExecutor.createByMethodId(this, "main"));
		game.setAliases("bs", "buildsomething");
		game.setAutoHelpCommand(true);
		
		SubCommand games = new SubCommand("games", "Show a list of all the running games", MethodExecutor.createByMethodId(this, "games"));
		games.setAliases("list");
		game.addSubCommand(games);
		
		SubCommand select = new SubCommand("select", "Select a game", MethodExecutor.createByMethodId(this, "select"));
		select.addArgument(new IntegerArgument("gameId", true, 0, null, true));
		select.setAliases("sel", "s");
		game.addSubCommand(select);
		
		SubCommand start = new SubCommand("start", "Start a game", MethodExecutor.createByMethodId(this, "start"));
		game.addSubCommand(start);
		
		SubCommand stop = new SubCommand("stop", "Stop a game", MethodExecutor.createByMethodId(this, "stop"));
		game.addSubCommand(stop);
		
		SubCommand word = new SubCommand("word", "Get the word of a game", MethodExecutor.createByMethodId(this, "word"));
		game.addSubCommand(word);
		
		SubCommand builder = new SubCommand("builder", "Get the builder(s) of a game", MethodExecutor.createByMethodId(this, "builder"));
		game.addSubCommand(builder);
		
		SubCommand builderAdd = new SubCommand("add", "Add a builder to the game", MethodExecutor.createByMethodId(this, "builderAdd"));
		builderAdd.addArgument(new PlayerArgument("player", true));
		builder.addSubCommand(builderAdd);
		
		SubCommand builderRemove = new SubCommand("remove", "Remove a builder from the game", MethodExecutor.createByMethodId(this, "builderRemove"));
		builderRemove.addArgument(new PlayerArgument("player", true));
		builder.addSubCommand(builderRemove);
		
		SubCommand builderReset = new SubCommand("reset", "Reset the builder(s) of the game", MethodExecutor.createByMethodId(this, "builderReset"));
		builder.addSubCommand(builderReset);
		
		SubCommand info = new SubCommand("info", "Get info about a game", MethodExecutor.createByMethodId(this, "info"));
		game.addSubCommand(info);
		
		SubCommand join = new SubCommand("join", "Join a game", MethodExecutor.createByMethodId(this, "join"));
		join.addArgument(new PlayerArgument("player", true, true));
		game.addSubCommand(join);
		
		SubCommand leave = new SubCommand("leave", "leave a game", MethodExecutor.createByMethodId(this, "leave"));
		leave.addArgument(new PlayerArgument("player", true, true));
		game.addSubCommand(leave);
		
		SubCommand openGames = new SubCommand("opengames", "Open game options", MethodExecutor.createByMethodId(this, "openGames"));
		openGames.addArgument(new BooleanArgument("value", false));
		FlagsArgument flag = new FlagsArgument("flags", false);
		flag.addFlag("f");
		openGames.addArgument(flag);
		game.addSubCommand(openGames);
		
		SubCommand close = new SubCommand("close", "Close a game", MethodExecutor.createByMethodId(this, "close"));
		FlagsArgument flag1 = new FlagsArgument("flags", false);
		flag1.addFlag("all");
		close.addArgument(flag1);
		game.addSubCommand(close);
				
		SubCommand maxPlayers = new SubCommand("maxplayers", "Maximum players in a game", MethodExecutor.createByMethodId(this, "maxPlayers"));
		maxPlayers.addArgument(new IntegerArgument("value", false, 2, 100, true));
		FlagsArgument flag2 = new FlagsArgument("flags", false);
		flag2.addFlag("all");
		maxPlayers.addArgument(flag2);
		game.addSubCommand(maxPlayers);
		

		CommandManager.get().registerCommand(plugin, game);
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

	private CommandSenderData getSenderData(CommandSender sender) {
		UUID u = getUuid(sender);
		Game g = getGame(u);
		return new CommandSenderData(u, g);
	}

	private CommandSenderData getCheckSenderData(CommandSender sender) {
		CommandSenderData data = getSenderData(sender);
		if (data.getUuid() == null) {
			sender.sendMessage("Invalid sender");
			return null;
		}
		if (data.getGame() == null) {
			sender.sendMessage("Invalid game");
			return null;
		}
		return data;
	}

	@MethodId("main")
	public boolean mainCommand(CommandSender sender, Command command, ArgumentData args) {
		CommandSenderData data = getSenderData(sender);
		if (data.getGame() != null) {
			sender.sendMessage("Game selected: " + plugin.getGameManager().getGames().indexOf(data.getGame()));
			sender.sendMessage("");
		}
		command.printHelp(sender);
		return true;
	}

	@MethodId("games")
	public boolean gamesCommand(CommandSender sender, Command command, ArgumentData args) {
		if (plugin.getGameManager().getGames().isEmpty()) {
			sender.sendMessage("There is no games open right now :(");
		} else {
			sender.sendMessage("Open games:");
			for (Game g : plugin.getGameManager().getGames()) {
				int index = plugin.getGameManager().getGames().indexOf(g);
				String builders = "";
				if (g instanceof BuildersGame) {
					BuildersGame bs = (BuildersGame) g;
					builders = (!bs.getBuilders().isEmpty() ? " | " + bs.getBuilders() : "");
				}
				sender.sendMessage("" + index + " | " + g.getGameType() + " | " + g.getMap().getName() + " | " + g.getMode() + " | "
					+ g.getPlayers().size() + "/" + g.getMaxPlayers() + builders);
			}
		}
		return true;
	}

	@MethodId("select")
	public boolean selectCommand(CommandSender sender, Command command, ArgumentData args) {
		int id = args.get("gameId", Integer.class);
		Game g = null;
		try {
			g = plugin.getGameManager().getGames().get(id);
		} catch (Exception ignored) {
		}

		if (g == null) {
			sender.sendMessage("invalid game, game is null");
		} else {
			UUID u = getUuid(sender);
			selects.remove(u);
			selects.put(u, g.getId());
			sender.sendMessage("Choose game number " + id);
		}
		return true;
	}

	@MethodId("start")
	public boolean startCommand(CommandSender sender, Command command, ArgumentData args) {
		CommandSenderData data = getCheckSenderData(sender);
		if (data == null) {
			return false;
		}
		Game g = data.getGame();

		if (g.getMode() == Gamemode.LOBBY) {
			g.setForceStart(true);
			g.countdown();
			sender.sendMessage("Game started!");
		} else {
			sender.sendMessage("Cant start the game");
		}

		return true;
	}

	@MethodId("stop")
	public boolean stopCommand(CommandSender sender, Command command, ArgumentData args) {
		CommandSenderData data = getCheckSenderData(sender);
		if (data == null) {
			return false;
		}
		Game g = data.getGame();

		if (g.getMode() == Gamemode.RUNNING) {
			g.stop(true, false);
			sender.sendMessage("Game stopped");
		} else {
			sender.sendMessage("The game not running");
		}

		return true;
	}

	@MethodId("word")
	public boolean wordCommand(CommandSender sender, Command command, ArgumentData args) {
		CommandSenderData data = getCheckSenderData(sender);
		if (data == null) {
			return false;
		}
		Game g = data.getGame();

		if (g.getWord() != null) {
			String wordh = g.getWord().getWordHebrew();
			if (sender instanceof Player) {
				wordh = plugin.hebrewMessage((Player) sender, g.getWord().getWordHebrew());
			}
			sender.sendMessage("Word: " + g.getWord().getWordEnglish() + " | " + wordh + " | " + g.getWord().getId());
		} else {
			sender.sendMessage("The word is not set");
		}
		return true;
	}

	@MethodId("builder")
	public boolean builderCommand(CommandSender sender, Command command, ArgumentData args) {
		CommandSenderData data = getCheckSenderData(sender);
		if (data == null) {
			return false;
		}
		Game g = data.getGame();

		if (!(g instanceof BuildersGame)) {
			sender.sendMessage("This is not builders game!");
			return false;
		}

		BuildersGame bs = (BuildersGame) g;
		sender.sendMessage("Builders (" + bs.getBuildersCount() + "): " + bs.getBuilders().toString());
		return true;
	}

	@MethodId("builderAdd")
	public boolean builderAddCommand(CommandSender sender, Command command, ArgumentData args) {
		CommandSenderData data = getCheckSenderData(sender);
		if (data == null) {
			return false;
		}
		Game g = data.getGame();

		if (!(g instanceof BuildersGame)) {
			sender.sendMessage("This is not builders game!");
			return false;
		}

		BuildersGame bs = (BuildersGame) g;

		if (bs.getMode() == Gamemode.LOBBY || bs.getMode() == Gamemode.LOBBY_COUNTDOWN) {
			Player p = args.get("player", Player.class);
			bs.getBuilders().addBuilder(p);
			sender.sendMessage("You add " + p.getName() + " as a builder to the game");
		} else {
			sender.sendMessage("Cant set builders now");
		}

		return true;
	}

	@MethodId("builderRemove")
	public boolean builderRemoveCommand(CommandSender sender, Command command, ArgumentData args) {
		CommandSenderData data = getCheckSenderData(sender);
		if (data == null) {
			return false;
		}
		Game g = data.getGame();

		if (!(g instanceof BuildersGame)) {
			sender.sendMessage("This is not builders game!");
			return false;
		}

		BuildersGame bs = (BuildersGame) g;

		if (bs.getMode() == Gamemode.LOBBY || bs.getMode() == Gamemode.LOBBY_COUNTDOWN) {
			Player p = args.get("player", Player.class);

			if (!bs.getBuilders().isBuilder(p)) {
				sender.sendMessage(p.getName() + " is not a builder in this game");
				return false;
			}
			bs.getBuilders().removeBuilder(p);
			sender.sendMessage("You removed " + p.getName() + " from the builders of the game");
		} else {
			sender.sendMessage("Cant set builders now");
		}
		return true;

	}

	@MethodId("builderReset")
	public boolean builderResetCommand(CommandSender sender, Command command, ArgumentData args) {
		CommandSenderData data = getCheckSenderData(sender);
		if (data == null) {
			return false;
		}
		Game g = data.getGame();

		if (!(g instanceof BuildersGame)) {
			sender.sendMessage("This is not builders game!");
			return false;
		}

		BuildersGame bs = (BuildersGame) g;

		if (bs.getMode() == Gamemode.LOBBY || bs.getMode() == Gamemode.LOBBY_COUNTDOWN) {
			bs.getBuilders().clear();
			sender.sendMessage("You reset the builders of the game");
		} else {
			sender.sendMessage("Cant set builders now");
		}

		return true;
	}

	@MethodId("info")
	public boolean infoCommand(CommandSender sender, Command command, ArgumentData args) {
		CommandSenderData data = getCheckSenderData(sender);
		if (data == null) {
			return false;
		}
		Game g = data.getGame();

		int id = plugin.getGameManager().getGames().indexOf(g);
		sender.sendMessage("---- Game Info ----");
		sender.sendMessage("ID: " + id);
		sender.sendMessage("Type: " + g.getGameType());
		sender.sendMessage("Mode: " + g.getMode());
		sender.sendMessage("Players: " + g.getPlayers().size() + "/" + g.getMaxPlayers());
		if (g instanceof BuildersGame) {
			sender.sendMessage("Builders count: " + ((BuildersGame) g).getBuildersCount());
		}
		if (g.getMode() == Gamemode.LOBBY_COUNTDOWN) {
			sender.sendMessage("Time until game starts: " + Utils.timeString(g.getLobbyCountDown().getValue()));
		} else if (g.getMode() == Gamemode.RUNNING) {
			sender.sendMessage("Time until game ends: " + Utils.timeString(g.getGameCountDown().getValue()));
		}
		if (g.getMap() != null) {
			sender.sendMessage("Map: " + g.getMap().getName());
		}
		/*
		 * if (g.getWord() != null) { sender.sendMessage("Word: " +
		 * g.getWord().getWordEnglish()); }
		 */
		if (g instanceof BuildersGame) {
			BuildersGame bs = (BuildersGame) g;
			if (!bs.getBuilders().isEmpty()) {
				sender.sendMessage("Builder(s): " + bs.getBuilders());
			}
		}
		sender.sendMessage("Know the word: " + g.getKnows().size());
		sender.sendMessage("Force start: " + g.isForceStart());
		return true;

	}

	@MethodId("join")
	public boolean joinCommand(CommandSender sender, Command command, ArgumentData args) {
		CommandSenderData data = getCheckSenderData(sender);
		if (data == null) {
			return false;
		}
		Game g = data.getGame();

		Player p = args.get("player", Player.class);
		if (p == null) {
			sender.sendMessage("invalid player");
			return false;
		}
		if (g.check(p)) {
			sender.sendMessage(p.getName() + " is already in the game");
			return false;
		}
		if (plugin.getGameManager().joinGame(g, p)) {
			sender.sendMessage("Joined " + p.getName() + " to the game!");
		} else {
			sender.sendMessage("Game already started");
		}
		return true;

	}

	@MethodId("leave")
	public boolean leaveCommand(CommandSender sender, Command command, ArgumentData args) {
		CommandSenderData data = getCheckSenderData(sender);
		if (data == null) {
			return false;
		}
		Game g = data.getGame();

		Player p = args.get("player", Player.class);
		if (p == null) {
			sender.sendMessage("invalid player");
			return false;
		}
		if (!g.check(p)) {
			sender.sendMessage(p.getName() + " is not in the game");
			return false;
		}
		g.removePlayer(p);
		sender.sendMessage(p.getName() + " Removed from the game");
		return true;

	}

	@MethodId("openGames")
	public boolean openGamesCommand(CommandSender sender, Command command, ArgumentData args) {
		if (args.has("value")) {
			boolean open = args.get("value", Boolean.class);
			plugin.getGameManager().setOpenNewGames(open);
			sender.sendMessage("Set open new games to " + open);
			if (args.hasFlag("flags", "f")) {
				if (open) {
					plugin.getGameManager().startIfNeeded(-1);
					sender.sendMessage("Force to create games");
				} else {
					sender.sendMessage("Can't open games when openGames is false");
				}
			}
		} else {
			sender.sendMessage("Determine if new games will create and start");
			sender.sendMessage("Current: " + plugin.getGameManager().isOpenNewGames());
		}
		return true;
	}

	@MethodId("close")
	public boolean closeCommand(CommandSender sender, Command command, ArgumentData args) {
		if (args.hasFlag("flags", "all")) {
			plugin.getGameManager().closeAll();
			sender.sendMessage("You closed all the games.");
		} else {
			CommandSenderData data = getCheckSenderData(sender);
			if (data == null) {
				return false;
			}
			Game g = data.getGame();

			g.close();
			sender.sendMessage("You closed the game.");
		}
		return true;

	}
	@MethodId("maxPlayers")
	public boolean maxPlayersCommand(CommandSender sender, Command command, ArgumentData args) {
		boolean all = args.hasFlag("flags", "all");
		if (args.has("value")) {
			int max = args.get("value", Integer.class);
			if (all) {
				plugin.getGameManager().setMaxPlayers(max);
				sender.sendMessage("You set the max players of all the games to " + plugin.getGameManager().getMaxPlayers());
			} else {
				CommandSenderData data = getCheckSenderData(sender);
				if (data == null) {
					return false;
				}
				data.getGame().setMaxPlayers(max);
				sender.sendMessage("You set the max players of the game to " + data.getGame().getMaxPlayers());
			}
		} else {
			CommandSenderData data = getSenderData(sender);
			if (data != null && data.getGame() != null) {
				sender.sendMessage("The max players of the game is " + data.getGame().getMaxPlayers());
			}
			sender.sendMessage("The global max players is " + plugin.getGameManager().getMaxPlayers());
		}
		return true;
	}

}
