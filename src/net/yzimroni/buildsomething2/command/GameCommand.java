package net.yzimroni.buildsomething2.command;

import java.util.HashMap;
import java.util.UUID;

import net.yzimroni.buildsomething2.BuildSomethingPlugin;
import net.yzimroni.buildsomething2.game.Gamemode;
import net.yzimroni.buildsomething2.game.games.BuildersGame;
import net.yzimroni.buildsomething2.game.games.Game;
import net.yzimroni.buildsomething2.player.BPlayer;
import net.yzimroni.buildsomething2.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class GameCommand implements CommandExecutor {
	private final UUID CONSOLE_UUID = UUID.randomUUID();
	private BuildSomethingPlugin plugin;
	private HashMap<UUID, UUID> selects;

	public GameCommand(BuildSomethingPlugin p) {
		plugin = p;
		selects = new HashMap<UUID, UUID>();
	}
		
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (commandLabel.equalsIgnoreCase("game") || commandLabel.equalsIgnoreCase("bs") || commandLabel.equalsIgnoreCase("buildsomething")) {
			UUID u = null;
			if (sender instanceof ConsoleCommandSender) {
				u = CONSOLE_UUID;
			} else if (sender instanceof Player) {
				u = ((Player) sender).getUniqueId();
			} else {
				return false;
			}
			if (sender.isOp()) {
				if (args.length == 0) {
					Game g = getGame(u);
					if (g != null) {
						sender.sendMessage("Game selected: " + plugin.getGameManager().getGames().indexOf(g));
					}
					sender.sendMessage("/" + commandLabel.toLowerCase() + " games - View all opens games");
					sender.sendMessage("/" + commandLabel.toLowerCase() + " sel <id> - Select a game to manage");
					sender.sendMessage("/" + commandLabel.toLowerCase() + " start - Start the selected game");
					sender.sendMessage("/" + commandLabel.toLowerCase() + " stop - Stop the selected game");
					sender.sendMessage("/" + commandLabel.toLowerCase() + " word - Get the word of the selected game");
					sender.sendMessage("/" + commandLabel.toLowerCase() + " addbuilder <player> - Add a player as a builder");
					sender.sendMessage("/" + commandLabel.toLowerCase() + " removebuilder <player> - Remove a player from the builders");
					sender.sendMessage("/" + commandLabel.toLowerCase() + " resetbuilders - Reset the builders of the game");
					sender.sendMessage("/" + commandLabel.toLowerCase() + " info - The info about the selected game");
					sender.sendMessage("/" + commandLabel.toLowerCase() + " join [player] - Join [player/you] to the selected game");
					sender.sendMessage("/" + commandLabel.toLowerCase() + " leave [player] - Remove [player/you] to the selected game");
					sender.sendMessage("/" + commandLabel.toLowerCase() + " opengames [boolean] - Determine if new games will create and start");
					sender.sendMessage("/" + commandLabel.toLowerCase() + " forceopen - Force open new games (use after set opengames to true)");
					sender.sendMessage("/" + commandLabel.toLowerCase() + " close - Close the selected game.");
					sender.sendMessage("/" + commandLabel.toLowerCase() + " closeall - Close all the games.");
					sender.sendMessage("/" + commandLabel.toLowerCase() + " setmaxplayers [mp] - Set the max players of the game");
					sender.sendMessage("/" + commandLabel.toLowerCase() + " setmaxplayersg [mp] - Set the max players of all the games");

				} else if (args.length == 1) {
					if (args[0].equalsIgnoreCase("games")) {
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
								sender.sendMessage("" + index + " | " + g.getGameType() + " | " + g.getMap().getName()  + " | " + g.getMode() + " | " + g.getPlayers().size() + "/" + g.getMaxPlayers() + builders);
							}
						}
					} else if (args[0].equalsIgnoreCase("start")) {
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
					} else if (args[0].equalsIgnoreCase("stop")) {
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
					} else if (args[0].equalsIgnoreCase("word")) {
						Game g = getGame(u);
						if (g == null) {
							sender.sendMessage("You must select a game");
							return false;
						}
						
						if (g.getWord() != null) {
							String wordh = g.getWord().getWordHebrew();
							if (sender instanceof Player) {
								wordh = plugin.hebrewMessage((Player) sender, g.getWord().getWordHebrew());
							}
							sender.sendMessage("Word: " + g.getWord().getWordEnglish() + " | " + wordh + " | " + g.getWord().getId());
						} else {
							sender.sendMessage("The word is not set");
						}
					} else if (args[0].equalsIgnoreCase("info")) {
						Game g = getGame(u);
						if (g == null) {
							sender.sendMessage("You must select a game");
							return false;
						}
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
						/*if (g.getWord() != null) {
							sender.sendMessage("Word: " + g.getWord().getWordEnglish());
						}*/
						if (g instanceof BuildersGame) {
							BuildersGame bs = (BuildersGame) g;
							if (!bs.getBuilders().isEmpty()) {
								sender.sendMessage("Builder(s): " + bs.getBuilders());
							}
						}
						sender.sendMessage("Know the word: " + g.getKnows().size());
						sender.sendMessage("Force start: " + g.isForceStart());
						
						
					} else if (args[0].equalsIgnoreCase("join")) {
						if (!(sender instanceof Player)) {
							sender.sendMessage("Only players can do this command");
							return false;
						}
						Player p = (Player) sender;
						Game g = getGame(u);
						if (g == null) {
							sender.sendMessage("You must select a game");
							return false;
						}
						if (g.check(p)) {
							p.sendMessage("You are already in the game");
							return false;
						}
						if (plugin.getGameManager().joinGame(g, p)) {
							p.sendMessage("Joined!");
						} else {
							p.sendMessage("Game already started");
						}
					} else if (args[0].equalsIgnoreCase("leave")) {
						if (!(sender instanceof Player)) {
							sender.sendMessage("Only players can do this command");
							return false;
						}
						Player p = (Player) sender;
						Game g = getGame(u);
						if (g == null) {
							sender.sendMessage("You must select a game");
							return false;
						}
						if (!g.check(p)) {
							p.sendMessage("You are not in the game");
							return false;
						}
						g.removePlayer(p);
						p.sendMessage("You left the game");
						
					} else if (args[0].equalsIgnoreCase("opengames")) {
						sender.sendMessage("Determine if new games will create and start");
						sender.sendMessage("Current: " + plugin.getGameManager().isOpenNewGames());
						
					} else if (args[0].equalsIgnoreCase("forceopen")) {
						if (!plugin.getGameManager().isOpenNewGames()) {
							sender.sendMessage("Can't open new games due to opengames is false");
						} else {
							plugin.getGameManager().startIfNeeded(-1);
							sender.sendMessage("Force to create games");
						}
					} else if (args[0].equalsIgnoreCase("close")) {
						Game g = getGame(u);
						if (g == null) {
							sender.sendMessage("You must select a game");
							return false;
						}
						g.close();
						sender.sendMessage("You closed the game.");
					} else if (args[0].equalsIgnoreCase("closeall")) {
						plugin.getGameManager().closeAll();
						sender.sendMessage("You closed all the games.");
					} else if (args[0].equalsIgnoreCase("resetbuilders")) {
						Game g = getGame(u);
						if (g == null) {
							sender.sendMessage("You must select a game");
							return false;
						}
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

					} else if (args[0].equalsIgnoreCase("setmaxplayers")) {
						Game g = getGame(u);
						if (g == null) {
							sender.sendMessage("You must select a game");
							return false;
						}
						sender.sendMessage("The max players of the game is " + g.getMaxPlayers());
					} else if (args[0].equalsIgnoreCase("setmaxplayersg")) {
						sender.sendMessage("The global max players is " + plugin.getGameManager().getMaxPlayers());
					}
				} else if (args.length == 2) {
					if (args[0].equalsIgnoreCase("sel") || args[0].equalsIgnoreCase("select")) {
						if (Utils.isInt(args[1])) {
							int id = Utils.getInt(args[1]);
							Game g = null;
							try {
							g = plugin.getGameManager().getGames().get(id);
							} catch (Exception e) {}
							if (g == null) {
								sender.sendMessage("invalid game, game is null");
							} else {
								selects.remove(u);
								selects.put(u, g.getId());
								sender.sendMessage("Choose game number " + id);
							}
						} else {
							sender.sendMessage("invalid int!");
						}
					} else if (args[0].equalsIgnoreCase("addbuilder")) {
						Game g = getGame(u);
						if (g == null) {
							sender.sendMessage("You must select a game");
							return false;
						}
						
						if (!(g instanceof BuildersGame)) {
							sender.sendMessage("This is not builders game!");
							return false;
						}
						
						BuildersGame bs = (BuildersGame) g;
						
						if (bs.getMode() == Gamemode.LOBBY || bs.getMode() == Gamemode.LOBBY_COUNTDOWN) {
							Player p = Bukkit.getPlayer(args[1]);
							if (p == null) {
								sender.sendMessage("invalid player");
								return false;
							}
							BPlayer b = plugin.getPlayerManager().getPlayer(p.getUniqueId());
							if (b == null) {
								sender.sendMessage("invalid player");
								return false;
							}
							bs.getBuilders().addBuilder(p);
							sender.sendMessage("You add " + p.getName() + " as a builder to the game");
						} else {
							sender.sendMessage("Cant set builders now");
						}
					} else if (args[0].equalsIgnoreCase("removebuilder")) {
						Game g = getGame(u);
						if (g == null) {
							sender.sendMessage("You must select a game");
							return false;
						}
						
						if (!(g instanceof BuildersGame)) {
							sender.sendMessage("This is not builders game!");
							return false;
						}
						
						BuildersGame bs = (BuildersGame) g;
						
						if (bs.getMode() == Gamemode.LOBBY || bs.getMode() == Gamemode.LOBBY_COUNTDOWN) {
							Player p = Bukkit.getPlayer(args[1]);
							if (p == null) {
								sender.sendMessage("invalid player");
								return false;
							}
							BPlayer b = plugin.getPlayerManager().getPlayer(p.getUniqueId());
							if (b == null) {
								sender.sendMessage("invalid player");
								return false;
							}
							if (!bs.getBuilders().isBuilder(p)) {
								sender.sendMessage(p.getName() + " is not a builder in this game");
								return false;
							}
							bs.getBuilders().removeBuilder(p);
							sender.sendMessage("You removed " + p.getName() + " from the builders of the game");
						} else {
							sender.sendMessage("Cant set builders now");
						}
					} else if (args[0].equalsIgnoreCase("join")) {
						Game g = getGame(u);
						if (g == null) {
							sender.sendMessage("You must select a game");
							return false;
						}
						Player p = Bukkit.getPlayer(args[1]);
						if (p == null) {
							sender.sendMessage("invalid player");
							return false;
						}
						if (g.check(p)) {
							sender.sendMessage(p.getName() + " are already in the game");
							return false;
						}
						if (plugin.getGameManager().joinGame(g, p)) {
							sender.sendMessage("Joined " + p.getName() + " to the game!");
						} else {
							sender.sendMessage("Game already started");
						}
					} else if (args[0].equalsIgnoreCase("leave")) {
						Game g = getGame(u);
						if (g == null) {
							sender.sendMessage("You must select a game");
							return false;
						}
						Player p = Bukkit.getPlayer(args[1]);
						if (p == null) {
							sender.sendMessage("invalid player");
							return false;
						}
						if (!g.check(p)) {
							sender.sendMessage(p.getName() + " are not in the game");
							return false;
						}
						g.removePlayer(p);
						sender.sendMessage(p.getName() + " Removed from the game");
					
					} else if (args[0].equalsIgnoreCase("opengames")) {
						boolean open = Utils.getBoolean(args[1]);
						plugin.getGameManager().setOpenNewGames(open);
						sender.sendMessage("Set open new games to " + open);
					} else if (args[0].equalsIgnoreCase("setmaxplayers")) {
						Game g = getGame(u);
						if (g == null) {
							sender.sendMessage("You must select a game");
							return false;
						}
						if (!Utils.isInt(args[1])) {
							sender.sendMessage("Invalid integer");
							return false;
						}
						int mp = Utils.getInt(args[1]);
						if (mp < 2) {
							sender.sendMessage("Max players must be 2 >=");
							return false;
						}
						g.setMaxPlayers(mp);
						sender.sendMessage("You set the max players of the game to " + g.getMaxPlayers());
					} else if (args[0].equalsIgnoreCase("setmaxplayersg")) {
						if (!Utils.isInt(args[1])) {
							sender.sendMessage("Invalid integer");
							return false;
						}
						int mp = Utils.getInt(args[1]);
						if (mp < 2) {
							sender.sendMessage("Max players must be 2 >=");
							return false;
						}
						plugin.getGameManager().setMaxPlayers(mp);
						sender.sendMessage("You set the max players of all the games to " + plugin.getGameManager().getMaxPlayers());
					}
				}
			}
		}
		return false;
	}
	
	private Game getGame(UUID u) {
		if (selects.containsKey(u)) {
			return plugin.getGameManager().getGameById(selects.get(u));
		}
		return null;
	}

}
