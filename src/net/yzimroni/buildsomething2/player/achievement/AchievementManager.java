package net.yzimroni.buildsomething2.player.achievement;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

import net.yzimroni.buildsomething2.BuildSomethingPlugin;
import net.yzimroni.buildsomething2.game.blocks.BSBlock;
import net.yzimroni.buildsomething2.game.bonuses.bonuses.Bonus;
import net.yzimroni.buildsomething2.game.effects.effects.Effect;
import net.yzimroni.buildsomething2.game.games.Game;
import net.yzimroni.buildsomething2.player.BPlayer;
import net.yzimroni.buildsomething2.player.TopPlayers;

import org.bukkit.event.Listener;

public class AchievementManager implements Listener {
	
	/*
	 * Ways to get achievements:
	 * 1. callable in every achievement (class to each thing, like BonusAchievement#onBonusUse(BPlayer, Bonus) and PlayerAchievement#onGameEnd(BPlayer, Game))
	 * then call all the achievements callable if they are the right type
	 * 
	 * 2. methods on this class like onBonusUse(BPlayer, Bonus) and check inside the methods for the achievement (if the know if more then X and if the player
	 * doesn't have the achievement Y, give it to the player)
	 * 
	 * 3. raw in the code: in knowTheWord, check if player know the word more then X times, if then give their the achievement X
	 * 
	 * I think 2 is the prefered, without alot of new anonymos classes and memory use but no much static and problem to maintance
	 * And we can do for special achievements, the checking will be in another place
	 */
	
	/*
	 * Ways to save achievements:
	 * 1. In an enum class BAchievement
	 * 2. In a HashMap or a list (with object that contains the name and the decrption) FMCAchievements
	 */
	
	private BuildSomethingPlugin plugin;
	
	/*
	 * TODO
	 * on command?
	 * Things on the game classes
	 * Option to add achievement to an offline player (without loading BPlayer):
	 * 		Call the db async and check if the player dosen't have the achievement, if so insert it and add a colum to the achievement table "messageSent", 
	 * 		indicate if the player know he recivied the achievement
	 */
	
	public AchievementManager(BuildSomethingPlugin p) {
		plugin = p;
	}
	
	private void addAchievementOffline(UUID u, AchievementInfo info) {
		BPlayer bp = plugin.getPlayerManager().getPlayer(u);
		if (bp != null) {
			bp.addAchievement(info);
		} else {
			boolean haveAchievement = false;
			try {
				ResultSet rs = plugin.getDB().get("SELECT ID FROM achievements WHERE UUID='" + u.toString() + "' AND achievement='" + info.getAchievement().name() + "'");
				haveAchievement = rs.next();
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("failed add achievement " + info + " to player " + u);
				return;
			}
			if (!haveAchievement){
				try {
					PreparedStatement achievementSQL = plugin.getDB().getPrepare("INSERT INTO achievement (UUID,achievement,date,messageSent) values (?,?,?,0)");
					achievementSQL.setString(1, u.toString());
					achievementSQL.setString(2, info.getAchievement().name());
					achievementSQL.setLong(3, info.getDate());
					
					achievementSQL.executeUpdate();
					info.setSaved(true);
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("failed add achievement1 " + info + " to player " + u);
					return;
				}
			} else {
				System.out.println("already have Achievement" + info + " " + u);
			}
		}
	}
	
	/**
	 * Call for general things for the player, for exemple join, quit etc 
	 * @param b The player
	 */
	public void onBPlayer(BPlayer b) {
		
	}
	
	/**
	 * Call for player things with a game, for exemple game start, the player is a builder etc
	 * @param b The player
	 * @param g The game
	 */
	
	public void onGame(BPlayer b, Game g) {
		
	}
	
	public void onBonus(BPlayer b, Bonus bonus) {
		
	}
	
	public void onGameBonus(BPlayer b, Game g, Bonus bonus) {
		
	}

	
	
	public void onBlock(BPlayer b, BSBlock block) {
		
	}
	
	public void onEffect(BPlayer b, Effect f) {
		
	}
	
	public void onGameEffect(BPlayer b, Game g, Effect f) {
		
	}
	
	public void onEconomy(BPlayer b) {
		
	}
	
	public void onTopPlayers(TopPlayers t) {
		
	}
	
	

}
