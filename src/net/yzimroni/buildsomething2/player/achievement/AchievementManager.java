package net.yzimroni.buildsomething2.player.achievement;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

import org.bukkit.event.Listener;

import net.yzimroni.buildsomething2.BuildSomethingPlugin;
import net.yzimroni.buildsomething2.game.blocks.BSBlock;
import net.yzimroni.buildsomething2.game.bonuses.bonuses.Bonus;
import net.yzimroni.buildsomething2.game.effects.effects.Effect;
import net.yzimroni.buildsomething2.game.games.Game;
import net.yzimroni.buildsomething2.player.BPlayer;
import net.yzimroni.buildsomething2.player.TopPlayers;

public class AchievementManager implements Listener {
	
	private BuildSomethingPlugin plugin;
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
				PreparedStatement pre = plugin.getDB().getPrepare("SELECT ID FROM achievements WHERE UUID=? AND achievement=?");
				pre.setString(1, u.toString());
				pre.setString(2, info.getAchievement().name());
				ResultSet rs = pre.executeQuery();
				haveAchievement = rs.next();
				rs.close();
				pre.close();
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
