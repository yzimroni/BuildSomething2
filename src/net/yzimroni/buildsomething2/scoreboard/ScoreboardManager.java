package net.yzimroni.buildsomething2.scoreboard;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

import net.yzimroni.buildsomething2.BuildSomethingPlugin;
import net.yzimroni.buildsomething2.game.Gamemode;
import net.yzimroni.buildsomething2.game.games.BuildersGame;
import net.yzimroni.buildsomething2.game.games.Game;
import net.yzimroni.buildsomething2.player.BPlayer;
import net.yzimroni.buildsomething2.player.stats.GlobalStats;
import net.yzimroni.buildsomething2.utils.Utils;

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
		
		GlobalStats stats = bp.getData().getGlobalStats();
		sib.add("Total games: " + ChatColor.GREEN + stats.getTotalGames());
		sib.add("Builder: " + ChatColor.GREEN + stats.getBuilder());
		sib.add("Know the word: " + ChatColor.GREEN + stats.getKnow());
		
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
					if (g.getLobbyCountDown().getValue() != 0 && g.getLobbyCountDown().getValue() != Game.LOOBY_COUNT_DOWN_TIME) {
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
