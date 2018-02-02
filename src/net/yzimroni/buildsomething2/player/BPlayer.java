package net.yzimroni.buildsomething2.player;

import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.yzimroni.buildsomething2.BuildSomethingPlugin;
import net.yzimroni.buildsomething2.player.achievement.AchievementInfo;
import net.yzimroni.buildsomething2.player.stats.GameTypeStats;

public class BPlayer {
	
	
	private UUID uuid;
	private PlayerData data;
	private long firstLogin;
	private long playTime;
	private long lastLogin;
	private String lastIp;
	private int loginTimes;
	private boolean hebrewWords;
	
	
	public BPlayer(UUID uuid, long firstLogin, long playTime, long lastLogin) {
		super();
		this.uuid = uuid;
		this.firstLogin = firstLogin;
		this.playTime = playTime;
		this.lastLogin = lastLogin;
	}

	
	
	public Player getPlayer() {
		return Bukkit.getPlayer(uuid);
	}


	/**
	 * @return the uuid
	 */
	public UUID getUUID() {
		return uuid;
	}

	/**
	 * @param uuid the uuid to set
	 */
	public void setUUID(UUID uuid) {
		this.uuid = uuid;
	}

	/**
	 * @return the data
	 */
	public PlayerData getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(PlayerData data) {
		this.data = data;
	}
	
	public long getFirstLogin() {
		return firstLogin;
	}

	public void setFirstLogin(long firstLogin) {
		this.firstLogin = firstLogin;
	}

	public long getPlayTime() {
		return playTime;
	}

	public void setPlayTime(long playTime) {
		this.playTime = playTime;
	}

	public long getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(long lastLogin) {
		this.lastLogin = lastLogin;
	}

	public String getLastIp() {
		return lastIp;
	}

	public void setLastIp(String lastIp) {
		this.lastIp = lastIp;
	}

	public void save(BuildSomethingPlugin p) {
		long cpt = (System.currentTimeMillis() / 1000) - (lastLogin / 1000);
		playTime += cpt;
		PreparedStatement ps = p.getDB().getPrepare("UPDATE players SET Name=?,hotbar_items=?," +
				"playTime=?,lastLogin=?,lastIp=?,loginTimes=?,hebrewWords=? WHERE UUID=?");
		try {
			ps.setString(1, getPlayer().getName());
			if (data.getHotbarItems() == null || data.getHotbarItems().isEmpty()) {
				ps.setNull(2, Types.VARCHAR);
			} else {
				ps.setString(2, data.getHotbarItems());
			}
			ps.setLong(3, playTime);
			ps.setLong(4, lastLogin);
			ps.setString(5, lastIp);
			ps.setInt(6, loginTimes);
			ps.setBoolean(7, hebrewWords);
			ps.setString(8, uuid.toString());
			ps.execute();
			
			
			lastLogin = System.currentTimeMillis();
			if (!data.getNewBlocks().isEmpty()) {
				PreparedStatement pre = p.getDB().getPrepare("INSERT INTO playerblocks (UUID,block_id) VALUES(?,?)");
				for (int id : data.getNewBlocks()) {
					pre.clearParameters();
					pre.setString(1, uuid.toString());
					pre.setInt(2, id);
					pre.executeUpdate();
				}
				pre.close();
				data.getNewBlocks().clear();
			}
			
			PreparedStatement bonusesPre = p.getDB().getPrepare("INSERT INTO playerbonuses (UUID,bonuseid,amount) VALUES (?,?,?) ON DUPLICATE KEY UPDATE amount = ?");
			for (Entry<Integer, Integer> s : data.bonuses.entrySet()) {
				bonusesPre.clearParameters();
				bonusesPre.setString(1, uuid.toString());
				bonusesPre.setInt(2, s.getKey());
				bonusesPre.setInt(3, s.getValue());
				bonusesPre.setInt(4, s.getValue());
				bonusesPre.executeUpdate();
			}
			bonusesPre.close();
			
			if (!data.getNewEffects().isEmpty()) {
				PreparedStatement pre = p.getDB().getPrepare("INSERT INTO playereffects (UUID,effect_id) VALUES(?,?)");
				for (int id : data.getNewEffects()) {
					pre.clearParameters();
					pre.setString(1, uuid.toString());
					pre.setInt(2, id);
					pre.executeUpdate();
				}
				pre.close();
				data.getNewEffects().clear();
			}
			
			PreparedStatement viewEffectsPre = p.getDB().getPrepare("INSERT INTO playereffectschoose (UUID,view_id,effect_id) VALUES (?,?,?) ON DUPLICATE KEY UPDATE effect_id = ?");
			for (Entry<Integer, Integer> s : data.views_effects.entrySet()) {
				viewEffectsPre.clearParameters();
				viewEffectsPre.setString(1, uuid.toString());
				viewEffectsPre.setInt(2, s.getKey());
				viewEffectsPre.setInt(3, s.getValue());
				viewEffectsPre.setInt(4, s.getValue());
				viewEffectsPre.executeUpdate();
			}
			viewEffectsPre.close();
			
			if (!data.getNewViews().isEmpty()) {
				PreparedStatement pre = p.getDB().getPrepare("INSERT INTO playereffectviews (UUID,view_id) VALUES(?,?)");
				for (int id : data.getNewViews()) {
					pre.clearParameters();
					pre.setString(1, uuid.toString());
					pre.setInt(2, id);
					pre.executeUpdate();
				}
				pre.close();
				data.getNewViews().clear();
			}
			
			if (!getData().getAchievements().isEmpty()) {
				List<AchievementInfo> achievementNew = getData().getAchievements().stream().filter(a -> !a.hasSaved()).collect(Collectors.toList());

				if (!achievementNew.isEmpty()) {
					System.out.println("saving achievemnts: " + achievementNew);
					PreparedStatement pre = p.getDB().getPrepare("INSERT INTO achievement (UUID,achievement,date) VALUES (?,?,?)");
					for (AchievementInfo i : achievementNew) {
						pre.clearParameters();
						pre.setString(1, uuid.toString());
						pre.setString(2, i.getAchievement().name());
						pre.setLong(3, i.getDate());
						i.setSaved(true);
						pre.executeUpdate();
					}
					pre.close();
				}
			}
			
			List<GameTypeStats> updated = data.getUpdatedStats();
			if (!updated.isEmpty()) {
				PreparedStatement psb = p.getDB().getPrepare("INSERT INTO playerstats (UUID,gameType,totalGames,builder,normal,know,knowFirst,allKnow) VALUES (?,?,?,?,?,?,?,?) " +
				"ON DUPLICATE KEY UPDATE totalGames=?,builder=?,normal=?,know=?,knowFirst=?,allKnow=?");
				for (GameTypeStats stat : updated) {
					psb.clearParameters();
					psb.setString(1, uuid.toString());
					psb.setInt(2, stat.getGameType().getId());
					int index = 3;
					for (int i = 0; i < 2; i++) {
						psb.setInt(index, stat.getTotalGames());
						index++;
						
						psb.setInt(index, stat.getBuilder());
						index++;
						
						psb.setInt(index, stat.getNormal());
						index++;
						
						psb.setInt(index, stat.getKnow());
						index++;
						
						psb.setInt(index, stat.getKnowFirst());
						index++;
						
						psb.setInt(index, stat.getAllKnow());
						index++;
					}
					psb.executeUpdate();
					stat.setUpdated(false);
				}
				psb.close();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getLoginTimes() {
		return loginTimes;
	}

	public void setLoginTimes(int loginTimes) {
		this.loginTimes = loginTimes;
	}

	public boolean isHebrewWords() {
		return hebrewWords;
	}

	public void setHebrewWords(boolean hebrewWords) {
		this.hebrewWords = hebrewWords;
	}
	
	public void addAchievement(AchievementInfo a) {
		if (!getData().hasAchievement(a.getAchievement())) {
			sendAchievementMessage(a);
			getData().addAchievementDirect(a);
		}
	}
	
	protected void sendAchievementMessage(AchievementInfo a) {
		//TODO change the message
		getPlayer().sendMessage("Achievement get: " + a.getAchievement().getName());
	}
	
}
