package net.yzimroni.buildsomething2.scoreboard;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import net.yzimroni.buildsomething2.BuildSomethingPlugin;
import net.yzimroni.buildsomething2.game.Gamemode;
import net.yzimroni.buildsomething2.game.games.BuildersGame;
import net.yzimroni.buildsomething2.game.games.Game;
import net.yzimroni.buildsomething2.player.BPlayer;
import net.yzimroni.buildsomething2.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

public class ScoreboardManager {
	private BuildSomethingPlugin plugin;
	
	public ScoreboardManager(BuildSomethingPlugin p) {
		plugin = p;
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				if (Bukkit.getOnlinePlayers().size() > 0) {
					for (Player pm : Bukkit.getOnlinePlayers()) {
						createPlayerScoreboard(pm);
					}
				}
				
			}
		});
	};
	
	
	
	public void initTeams(Scoreboard s) {
		initTeamStaff(s);
	}
	
	private void initTeamStaff(Scoreboard s) {
		if (s.getTeam("Staff") != null) {
			s.getTeam("Staff").unregister();
		}
		Team t = s.registerNewTeam("Staff");
		t.setOption(Option.COLLISION_RULE, OptionStatus.NEVER);
			
		t.setDisplayName("Staff");
		t.setPrefix(ChatColor.RED + "");
		t.setSuffix(ChatColor.RESET + "");
		
		/*TODO maybe other way (maybe hashmap with uuid and the name of the team [or permissions], 
		 * and when a player connect add them to the group they should be in)
		 * 
		 * I think to do this using permission, but need to think about how to do this
		*/
		
		t.addPlayer(Bukkit.getOfflinePlayer(UUID.fromString("341899b6-b28f-47a3-b85e-3aa3b491d0d3")));
		t.addPlayer(Bukkit.getOfflinePlayer(UUID.fromString("860d3119-ea5a-490b-9bf4-29b581164eca")));
		t.addPlayer(Bukkit.getOfflinePlayer(UUID.fromString("696de9c6-96af-47a7-b934-96e6e2853817")));
	}
	
	public SimpleScoreboard createGameScoreboard(Game g) {
		/**
		 * Time to start
		 * map
		 * players waiting
		 * how much builders will be/the mode
		 * 
		 */
		SimpleScoreboard sib = new SimpleScoreboard("Build Something");
		
		sib.blankLine();
		sib.add(new SimpleDateFormat("dd.MM.yyyy").format(new Date()));
		sib.add("Game Id: " + g.getNumberId());
		sib.blankLine();
		/*if (g.getBuilders().size() == 1) {
			sib.add("Builder: " + g.getBuilders().getPlayers().get(0).getName());
		} else {
			sib.add("Builders:");
			for (Player p : g.getBuilders().getPlayers()) {
				sib.add(p.getName());
			}
		}*/
		if (g.getMode() == Gamemode.RUNNING) {
			if (g instanceof BuildersGame) {
				BuildersGame bsg = (BuildersGame) g;
				sib.add(ChatColor.GREEN + "Builder" + (bsg.getBuilders().size() == 1 ? "" : "s") + ":");
				for (Player p : bsg.getBuilders().getPlayers()) {
					sib.add(ChatColor.AQUA + p.getName());
				}
				sib.blankLine();
			}
		}
		if (g.getLobbyCountDown().getValue() != 0 && g.getLobbyCountDown().getValue() == Game.LOOBY_COUNT_DOWN_TIME) {
			sib.add(time(g.getLobbyCountDown().getValue()));
		} else if (g.getMode() == Gamemode.RUNNING) {
			sib.add(time(g.getGameCountDown().getValue()));
		}
		sib.add("Players: " + g.getPlayersBukkit().size());
		sib.add("Map: " + g.getMap().getName());
		sib.build();
		Scoreboard sb = sib.getScoreboard();
		
		initTeams(sb);
		
		return sib;
	}
	
	public SimpleScoreboard createPlayerScoreboard(Player p) {
		SimpleScoreboard sib = new SimpleScoreboard(ChatColor.AQUA + p.getName() + " Stats");
		
		//sib.blankLine();
		sib.add("Coins: " + ChatColor.GREEN +  plugin.getPlayerManager().getEconomy().getBalance(p));
		//sib.add("");
		sib.blankLine();
		
		BPlayer bp = plugin.getPlayerManager().getPlayer(p);
		
		sib.add("Blocks: " + ChatColor.GREEN + bp.getData().getBlocks().size());
		//sib.add("");
		sib.blankLine();
		//sib.blankLine();
		/**
		 * total games X
		 * builder
		 * know the word
		 * know first X
		 */
		
		/*sib.add("Total games:");
		sib.add("" + ChatColor.GREEN + bp.getData().getTotalGames());
		sib.add("Builder:");
		sib.add("" + ChatColor.GREEN + bp.getData().getBuilder());
		sib.add("Know the word:");
		sib.add("" + ChatColor.GREEN + bp.getData().getKnow());*/
		
		sib.add("Total games: " + ChatColor.GREEN + bp.getData().getTotalGames());
		sib.add("Builder: " + ChatColor.GREEN + bp.getData().getBuilder());
		sib.add("Know the word: " + ChatColor.GREEN + bp.getData().getKnow());
		
		sib.build();
		
		Scoreboard sb = sib.getScoreboard();
		
		initTeams(sb);
		
		p.setScoreboard(sb);
		
		return sib;
	}
	
	private String TIME_PREFIX = ChatColor.GREEN + "Time: ";
	
	private String time(int s) {
		return TIME_PREFIX + Utils.foramtTimeShort(s);
	}
	
	public void updateGameScoreboardTime(Game g) {
		Objective o = g.getScoreboard().getScoreboard().getObjective(DisplaySlot.SIDEBAR);
		if (o != null) {
			for (String s : o.getScoreboard().getEntries()) {
				if (s.startsWith(TIME_PREFIX)) {
					int score = o.getScore(s).getScore();
					o.getScoreboard().resetScores(s);
					if (g.getLobbyCountDown().getValue() != 0 && g.getLobbyCountDown().getValue() != Game.LOOBY_COUNT_DOWN_TIME) { //TODO != or == in the last?
						o.getScore(time(g.getLobbyCountDown().getValue())).setScore(score);
					} else if (g.getMode() == Gamemode.RUNNING) {
						o.getScore(time(g.getGameCountDown().getValue())).setScore(score);
					}					
					
					//break;
				} else if (s.startsWith("Players: ")) {
					int score = o.getScore(s).getScore();
					o.getScoreboard().resetScores(s);
					o.getScore("Players: " + g.getPlayersBukkit().size()).setScore(score);
				}
			}
			//TO DO update the time
		}
	}
	
	public void updateGameScoreboard(Game g) {
		SimpleScoreboard sib = createGameScoreboard(g);
		g.setScoreboard(sib);
	}
}
