package net.yzimroni.buildsomething2.command;

import java.util.HashMap;
import java.util.UUID;

import net.yzimroni.buildsomething2.BuildSomethingPlugin;
import net.yzimroni.buildsomething2.game.Gamemode;
import net.yzimroni.buildsomething2.game.Word;
import net.yzimroni.buildsomething2.game.games.BuildersGame;
import net.yzimroni.buildsomething2.game.games.Game;
import net.yzimroni.buildsomething2.utils.Utils;
import net.yzimroni.commandmanager.command.Command;
import net.yzimroni.commandmanager.command.SubCommand;
import net.yzimroni.commandmanager.command.args.ArgumentData;
import net.yzimroni.commandmanager.command.args.arguments.BooleanArgument;
import net.yzimroni.commandmanager.command.args.arguments.FlagsArgument;
import net.yzimroni.commandmanager.command.args.arguments.IntegerArgument;
import net.yzimroni.commandmanager.command.args.arguments.PlayerArgument;
import net.yzimroni.commandmanager.command.methodexecutor.MethodExecutor;
import net.yzimroni.commandmanager.command.methodexecutor.MethodExecutorClass;
import net.yzimroni.commandmanager.command.methodexecutor.MethodId;
import net.yzimroni.commandmanager.manager.CommandManager;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class GameCommand implements MethodExecutorClass {
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
		
		SubCommand wordSet = new SubCommand("set", "Set the word of a game", MethodExecutor.createByMethodId(this, "wordSet"));
		wordSet.addArgument(new IntegerArgument("word", true, 0, null, false));
		word.addSubCommand(wordSet);
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
		
		SubCommand time = new SubCommand("time", "Control the game time/length", MethodExecutor.createByMethodId(this, "time"));
		
		SubCommand timeSet = new SubCommand("set", "Set the time of the game", MethodExecutor.createByMethodId(this, "timeSet"));
		timeSet.addArgument(new IntegerArgument("time", true, 1, null, true));
		time.addSubCommand(timeSet);
		
		SubCommand timeAdd = new SubCommand("add", "Add time of the game", MethodExecutor.createByMethodId(this, "timeAdd"));
		timeAdd.addArgument(new IntegerArgument("time", true, 1, null, true));
		time.addSubCommand(timeAdd);
		
		game.addSubCommand(time);
		
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
	public void mainCommand(CommandSender sender, Command command, ArgumentData args) {
		CommandSenderData data = getSenderData(sender);
		if (data.getGame() != null) {
			sender.sendMessage("Game selected: " + plugin.getGameManager().getGames().indexOf(data.getGame()));
			sender.sendMessage("");
		}
		command.printHelp(sender, -1);
	}

	@MethodId("games")
	public void gamesCommand(CommandSender sender, Command command, ArgumentData args) {
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
				sender.sendMessage("" + index + " | " + g.getGameType() + " | " + g.getMap().getName() + " | "
						+ g.getMode() + " | " + g.getPlayers().size() + "/" + g.getMaxPlayers() + builders);
			}
		}
	}

	@MethodId("select")
	public void selectCommand(CommandSender sender, Command command, ArgumentData args) {
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
	}

	@MethodId("start")
	public void startCommand(CommandSender sender, Command command, ArgumentData args) {
		CommandSenderData data = getCheckSenderData(sender);
		if (data == null) {
			return;
		}
		Game g = data.getGame();

		if (g.getMode() == Gamemode.LOBBY) {
			g.setForceStart(true);
			g.countdown();
			sender.sendMessage("Game started!");
		} else {
			sender.sendMessage("Cant start the game");
		}

	}

	@MethodId("stop")
	public void stopCommand(CommandSender sender, Command command, ArgumentData args) {
		CommandSenderData data = getCheckSenderData(sender);
		if (data == null) {
			return;
		}
		Game g = data.getGame();

		if (g.getMode() == Gamemode.RUNNING) {
			g.stop(true, false);
			sender.sendMessage("Game stopped");
		} else {
			sender.sendMessage("The game not running");
		}

	}

	@MethodId("word")
	public void wordCommand(CommandSender sender, Command command, ArgumentData args) {
		CommandSenderData data = getCheckSenderData(sender);
		if (data == null) {
			return;
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
	}

	@MethodId("wordSet")
	public void wordSetCommand(CommandSender sender, Command command, ArgumentData args) {
		CommandSenderData data = getCheckSenderData(sender);
		if (data == null) {
			return;
		}
		Game g = data.getGame();
		if (g instanceof BuildersGame) {
			BuildersGame builders = (BuildersGame) g;
			Word w = plugin.getGameManager().getWord(args.get("word", Integer.class));
			if (w != null) {
				builders.setWord(w);
				if (builders.getMode() == Gamemode.RUNNING) {
					builders.message("The word has changed");
					builders.sendWordInfo();
				}
			} else {
				sender.sendMessage("Word not found");
			}
		} else {
			sender.sendMessage("You can set the game word only on builder-game");
		}
	}

	@MethodId("builder")
	public void builderCommand(CommandSender sender, Command command, ArgumentData args) {
		CommandSenderData data = getCheckSenderData(sender);
		if (data == null) {
			return;
		}
		Game g = data.getGame();

		if (!(g instanceof BuildersGame)) {
			sender.sendMessage("This is not builders game!");
			return;
		}

		BuildersGame bs = (BuildersGame) g;
		sender.sendMessage("Builders (" + bs.getBuildersCount() + "): " + bs.getBuilders().toString());
	}

	@MethodId("builderAdd")
	public void builderAddCommand(CommandSender sender, Command command, ArgumentData args) {
		CommandSenderData data = getCheckSenderData(sender);
		if (data == null) {
			return;
		}
		Game g = data.getGame();

		if (!(g instanceof BuildersGame)) {
			sender.sendMessage("This is not builders game!");
			return;
		}

		BuildersGame bs = (BuildersGame) g;

		if (bs.getMode() == Gamemode.LOBBY || bs.getMode() == Gamemode.LOBBY_COUNTDOWN) {
			Player p = args.get("player", Player.class);
			bs.getBuilders().addBuilder(p);
			sender.sendMessage("You add " + p.getName() + " as a builder to the game");
		} else {
			sender.sendMessage("Cant set builders now");
		}

	}

	@MethodId("builderRemove")
	public void builderRemoveCommand(CommandSender sender, Command command, ArgumentData args) {
		CommandSenderData data = getCheckSenderData(sender);
		if (data == null) {
			return;
		}
		Game g = data.getGame();

		if (!(g instanceof BuildersGame)) {
			sender.sendMessage("This is not builders game!");
			return;
		}

		BuildersGame bs = (BuildersGame) g;

		if (bs.getMode() == Gamemode.LOBBY || bs.getMode() == Gamemode.LOBBY_COUNTDOWN) {
			Player p = args.get("player", Player.class);

			if (!bs.getBuilders().isBuilder(p)) {
				sender.sendMessage(p.getName() + " is not a builder in this game");
				return;
			}
			bs.getBuilders().removeBuilder(p);
			sender.sendMessage("You removed " + p.getName() + " from the builders of the game");
		} else {
			sender.sendMessage("Cant set builders now");
		}

	}

	@MethodId("builderReset")
	public void builderResetCommand(CommandSender sender, Command command, ArgumentData args) {
		CommandSenderData data = getCheckSenderData(sender);
		if (data == null) {
			return;
		}
		Game g = data.getGame();

		if (!(g instanceof BuildersGame)) {
			sender.sendMessage("This is not builders game!");
			return;
		}

		BuildersGame bs = (BuildersGame) g;

		if (bs.getMode() == Gamemode.LOBBY || bs.getMode() == Gamemode.LOBBY_COUNTDOWN) {
			bs.getBuilders().clear();
			sender.sendMessage("You reset the builders of the game");
		} else {
			sender.sendMessage("Cant set builders now");
		}

	}

	@MethodId("info")
	public void infoCommand(CommandSender sender, Command command, ArgumentData args) {
		CommandSenderData data = getCheckSenderData(sender);
		if (data == null) {
			return;
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
		if (g instanceof BuildersGame) {
			BuildersGame bs = (BuildersGame) g;
			if (!bs.getBuilders().isEmpty()) {
				sender.sendMessage("Builder(s): " + bs.getBuilders());
			}
		}
		sender.sendMessage("Know the word: " + g.getKnows().size());
		sender.sendMessage("Force start: " + g.isForceStart());

	}

	@MethodId("join")
	public void joinCommand(CommandSender sender, Command command, ArgumentData args) {
		CommandSenderData data = getCheckSenderData(sender);
		if (data == null) {
			return;
		}
		Game g = data.getGame();

		Player p = args.get("player", Player.class);
		if (p == null) {
			sender.sendMessage("invalid player");
			return;
		}
		if (g.check(p)) {
			sender.sendMessage(p.getName() + " is already in the game");
			return;
		}
		if (plugin.getGameManager().joinGame(g, p)) {
			sender.sendMessage("Joined " + p.getName() + " to the game!");
		} else {
			sender.sendMessage("Game already started");
		}

	}

	@MethodId("leave")
	public void leaveCommand(CommandSender sender, Command command, ArgumentData args) {
		CommandSenderData data = getCheckSenderData(sender);
		if (data == null) {
			return;
		}
		Game g = data.getGame();

		Player p = args.get("player", Player.class);
		if (p == null) {
			sender.sendMessage("invalid player");
			return;
		}
		if (!g.check(p)) {
			sender.sendMessage(p.getName() + " is not in the game");
			return;
		}
		g.removePlayer(p);
		sender.sendMessage(p.getName() + " Removed from the game");

	}

	@MethodId("openGames")
	public void openGamesCommand(CommandSender sender, Command command, ArgumentData args) {
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
		return;
	}

	@MethodId("close")
	public void closeCommand(CommandSender sender, Command command, ArgumentData args) {
		if (args.hasFlag("flags", "all")) {
			plugin.getGameManager().closeAll();
			sender.sendMessage("You closed all the games.");
		} else {
			CommandSenderData data = getCheckSenderData(sender);
			if (data == null) {
				return;
			}
			Game g = data.getGame();

			g.close();
			sender.sendMessage("You closed the game.");
		}

	}

	@MethodId("maxPlayers")
	public void maxPlayersCommand(CommandSender sender, Command command, ArgumentData args) {
		boolean all = args.hasFlag("flags", "all");
		if (args.has("value")) {
			int max = args.get("value", Integer.class);
			if (all) {
				plugin.getGameManager().setMaxPlayers(max);
				sender.sendMessage(
						"You set the max players of all the games to " + plugin.getGameManager().getMaxPlayers());
			} else {
				CommandSenderData data = getCheckSenderData(sender);
				if (data == null) {
					return;
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
	}

	@MethodId("time")
	public void timeCommand(CommandSender sender, Command command, ArgumentData args) {
		CommandSenderData data = getCheckSenderData(sender);
		if (data == null) {
			return;
		}
		Game g = data.getGame();
		sender.sendMessage("Game time: " + Utils.timeString(g.getTime()));
	}

	@MethodId("timeSet")
	public void timeSetCommand(CommandSender sender, Command command, ArgumentData args) {
		CommandSenderData data = getCheckSenderData(sender);
		if (data == null) {
			return;
		}
		Game g = data.getGame();
		int time = args.get("time", Integer.class);
		g.setTime(time);
		sender.sendMessage("Added " + Utils.timeString(time) + " to the game, the time of the game now: "
				+ Utils.timeString(g.getTime()));
	}

	@MethodId("timeAdd")
	public void timeAddCommand(CommandSender sender, Command command, ArgumentData args) {
		CommandSenderData data = getCheckSenderData(sender);
		if (data == null) {
			return;
		}
		Game g = data.getGame();
		int time = args.get("time", Integer.class);
		g.addTime(time);
		sender.sendMessage("Set the game time to " + Utils.timeString(time) + "");
	}

}
