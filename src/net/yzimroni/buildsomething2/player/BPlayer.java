package net.yzimroni.buildsomething2.player;

import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import net.yzimroni.buildsomething2.BuildSomethingPlugin;
import net.yzimroni.buildsomething2.player.achievement.AchievementInfo;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BPlayer {
	
	
	private UUID uuid;
	private PlayerData data;
	private long firstLogin;
	private long playTime;
	private long lastLogin;
	private String lastIp;
	private int loginTimes;
	private boolean hebrewWords;
	
	
	
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
		PreparedStatement ps = p.getDB().getPrepare("UPDATE players SET Name=?,total=?,builder=?,normal=?,know=?,knowfirst=?,hotbar_items=?," +
				"playTime=?,lastLogin=?,lastIp=?,loginTimes=?,allknow=?,hebrewWords=? WHERE UUID='" + uuid.toString() + "'");
		try {
			ps.setString(1, getPlayer().getName());
			ps.setInt(2, data.getTotalGames());
			ps.setInt(3, data.getBuilder());
			ps.setInt(4, data.getNormalPlayer());
			ps.setInt(5, data.getKnow());
			ps.setInt(6, data.getKnowFirst());
			if (data.getHotbarItems() == null || data.getHotbarItems().isEmpty()) {
				ps.setNull(7, Types.VARCHAR);
			} else {
				ps.setString(7, data.getHotbarItems());
			}
			ps.setLong(8, playTime);
			ps.setLong(9, lastLogin);
			ps.setString(10, lastIp);
			ps.setInt(11, loginTimes);
			ps.setInt(12, data.getAllKnow());
			ps.setBoolean(13, hebrewWords);
			ps.execute();
			lastLogin = System.currentTimeMillis();
			if (!data.getNewBlocks().isEmpty()) {
				String s = "INSERT INTO playerblocks (UUID,block_id) ";
				boolean value = false;
				for (int id : data.getNewBlocks()) {
					if (!value) {
						s += "VALUES";
					} else {
						s += ",";
					}
					
					s += "('" + uuid.toString() + "','" + id + "')";
					
					if (!value) {
						value = true;
					}
				}
				p.getDB().set(s);
				data.getNewBlocks().clear();
			}
			
			for (Entry<Integer, Integer>  s : data.bonuses.entrySet()) {
				try {
					PreparedStatement psb = p.getDB().getPrepare("INSERT INTO playerbonuses (UUID,bonuseid,amount) VALUES (?,?,?) ON DUPLICATE KEY UPDATE amount = ?");
					psb.setString(1, uuid.toString());
					psb.setInt(2, s.getKey());
					psb.setInt(3, s.getValue());
					psb.setInt(4, s.getValue());
					psb.executeUpdate();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			if (!data.getNewEffects().isEmpty()) {
				String s = "INSERT INTO playereffects (UUID,effect_id) ";
				boolean value = false;
				for (int id : data.getNewEffects()) {
					if (!value) {
						s += "VALUES";
					} else {
						s += ",";
					}
					
					s += "('" + uuid.toString() + "','" + id + "')";
					
					if (!value) {
						value = true;
					}
				}
				p.getDB().set(s);
				data.getNewEffects().clear();
			
			}
			
			for (Entry<Integer, Integer>  s : data.views_effects.entrySet()) {
				try {
					PreparedStatement psb = p.getDB().getPrepare("INSERT INTO playereffectschoose (UUID,view_id,effect_id) VALUES (?,?,?) ON DUPLICATE KEY UPDATE effect_id = ?");
					psb.setString(1, uuid.toString());
					psb.setInt(2, s.getKey());
					psb.setInt(3, s.getValue());
					psb.setInt(4, s.getValue());
					psb.executeUpdate();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			if (!data.getNewViews().isEmpty()) {
				String s = "INSERT INTO playereffectviews (UUID,view_id) ";
				boolean value = false;
				for (int id : data.getNewViews()) {
					if (!value) {
						s += "VALUES";
					} else {
						s += ",";
					}
					
					s += "('" + uuid.toString() + "','" + id + "')";
					
					if (!value) {
						value = true;
					}
				}
				p.getDB().set(s);
				data.getNewViews().clear();
			}
			
			if (!getData().getAchievements().isEmpty()) {
				List<AchievementInfo> achievementNew = new ArrayList<AchievementInfo>();
				for (AchievementInfo info : getData().getAchievements()) {
					if (!info.hasSaved()) {
						achievementNew.add(info);
					}
				}

				if (!achievementNew.isEmpty()) {
					System.out.println("saving achievemnts: " + achievementNew);
					String query = "INSERT INTO achievement (UUID,achievement,date) values ";
					String querydata = "";
					for (int i = 0; i < achievementNew.size(); i++) {
						if (!querydata.isEmpty()) {
							querydata += ",";
						}
						querydata += "(?,?,?)";
					}
					PreparedStatement achievementSQL = p.getDB().getPrepare(query + querydata);
					int count = 1;
					for (AchievementInfo i : achievementNew) {
						achievementSQL.setString(count, uuid.toString());
						count++;
						achievementSQL.setString(count, i.getAchievement().name());
						count++;
						achievementSQL.setLong(count, i.getDate());
						count++;
						i.setSaved(true);
					}
					//Bukkit.broadcastMessage(achievementSQL.toString()); TODO

					achievementSQL.executeUpdate();

				}
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
		//TODO
		getPlayer().sendMessage("Achievement get: " + a.getAchievement().getName());
	}
	
}
