package net.yzimroni.buildsomething2.player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import net.yzimroni.buildsomething2.BuildSomethingPlugin;
import net.yzimroni.buildsomething2.player.achievement.AchievementInfo;
import net.yzimroni.buildsomething2.player.achievement.AchievementManager;
import net.yzimroni.buildsomething2.player.economy.Economy;
import net.yzimroni.buildsomething2.utils.Utils;
import net.yzimroni.party.parties.PartyManager;

public class PlayerManager implements Listener {
	private BuildSomethingPlugin plugin;
	private HashMap<UUID, BPlayer> bplayers;
	private Economy economy;
	private AchievementManager achievementManager;
	
	
	public PlayerManager(BuildSomethingPlugin p) {
		bplayers = new HashMap<UUID, BPlayer>();
		plugin = p;
		economy = new Economy(plugin);
		achievementManager = new AchievementManager(plugin);

		Bukkit.getPluginManager().registerEvents(economy, plugin);
		Bukkit.getPluginManager().registerEvents(achievementManager, plugin);
		
		
		if (Bukkit.getOnlinePlayers().size() > 0) {
			for (Player pm : Bukkit.getOnlinePlayers()) {
				loadPlayer(pm.getUniqueId());
			}
		}
		autoSave();
	}
	
	public void onDisable() {
		economy.onDisable();
		for (UUID u : bplayers.keySet()) {
			getPlayer(u).save(plugin);
		}
	}
	
	private void autoSave() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				plugin.log.info("Auto saving players...");
				for (BPlayer p : bplayers.values()) {
					p.save(plugin);
				}
				economy.saveAll();
				plugin.log.info("Finish auto saving players!");
				plugin.getTopPlayers().update();
			}
		}, 5 * 60 * 20, 5 * 60 * 20);
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		loadPlayer(e.getPlayer().getUniqueId());
		BPlayer b = getPlayer(e.getPlayer());
		b.setLastIp(e.getPlayer().getAddress().getAddress().getHostAddress());
		b.setLoginTimes(b.getLoginTimes() + 1);
		String header = ChatColor.GREEN + "Build Something " + ChatColor.AQUA + "2"
				+ "\n";
		Utils.setPlayerListHeader(e.getPlayer(), header, "");
		plugin.getScoreboardManager().createPlayerScoreboard(e.getPlayer());
		checkCoinsFromBot(e.getPlayer());
		checkOfflineAchievement(b);
	}
	
	@SuppressWarnings("deprecation")
	private void checkCoinsFromBot(final Player p) {
		Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {

			@Override
			public void run() {
				final double coins = economy.getBotCoins(p.getUniqueId(), true);
				if (coins != 0) {
					Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
						@Override
						public void run() {
							economy.depositPlayer(p.getUniqueId(), coins);
							p.sendMessage(ChatColor.GREEN + "You received " + ChatColor.YELLOW + coins + " coins" + ChatColor.GREEN + " From bot builds");
						}
					});
				}
			}
		});
	}
	
	private void checkOfflineAchievement(final BPlayer p) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				for (AchievementInfo info : p.getData().getAchievements()) {
					if (!info.isMessageSent()) {
						p.sendAchievementMessage(info);
						info.setMessageSent(true);
					}
				}
				
			}
		}, 20L);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST) //To prevent BPlayer be null in the event in Game.class
	public void onPlayerQuit(PlayerQuitEvent e) {
		removePlayer(e.getPlayer().getUniqueId());
	}
	
	public void removePlayer(UUID u) {
		BPlayer bp = bplayers.get(u);
		if (bp == null) return;
		bp.save(plugin);
		bplayers.remove(u);
	}

	/**
	 * @return the bplayers
	 */
	public HashMap<UUID, BPlayer> getBPlayers() {
		return bplayers;
	}
	
	public boolean loadPlayer(UUID u) {
		if (isLoad(u)) return true;
		try {
			PreparedStatement pre = plugin.getDB().getPrepare("SELECT * FROM players WHERE UUID=?");
			pre.setString(1, u.toString());
			ResultSet rs = pre.executeQuery();

			if (rs.first()) {
				BPlayer bp = new BPlayer(u, rs.getLong("firstLogin"), rs.getLong("playTime"), System.currentTimeMillis());
				bp.setUUID(u);
				bp.setLastIp(rs.getString("lastIp"));
				bp.setLoginTimes(rs.getInt("loginTimes"));
				bp.setHebrewWords(rs.getBoolean("hebrewWords"));
				PlayerData d = PlayerData.loadRS(u, rs, plugin);
				bp.setData(d);
				bplayers.put(u, bp);
				rs.close();
				pre.close();
				return true;
			} else {
				Player p = Bukkit.getPlayer(u);
				long firstLogin = System.currentTimeMillis();
				PreparedStatement insertPre = plugin.getDB().getPrepare("INSERT INTO players (UUID,Name,firstLogin) VALUES(?,?,?)");
				insertPre.setString(1, u.toString());
				insertPre.setString(2, p.getName());
				insertPre.setLong(3, firstLogin);
				insertPre.executeUpdate();
				BPlayer bp = new BPlayer(u, firstLogin, 0L, firstLogin);				
				PlayerData d = PlayerData.createNew(u);
				bp.setData(d);
				bplayers.put(u, bp);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean isLoad(UUID u) {
		return bplayers.containsKey(u);
	}
	
	public BPlayer getPlayer(UUID u) {
		return bplayers.get(u);
	}
		
	public BPlayer getPlayer(Player p) {
		if (p == null) return null;
		return getPlayer(p.getUniqueId());
	}
	
	private PlayerData loadData(UUID u) {
		PlayerData d = PlayerData.load(plugin, u);
		return d;
	}
	
	public PlayerData getData(UUID u) {
		BPlayer bp = getPlayer(u);
		if (bp != null) {
			return bp.getData();
		}
		return loadData(u);
	}

	public Economy getEconomy() {
		return economy;
	}

	public void setEconomy(Economy economy) {
		this.economy = economy;
	}

	/**
	 * @return the partyManager
	 */
	public PartyManager getPartyManager() {
		return PartyManager.manager();
	}

	public AchievementManager getAchievementManager() {
		return achievementManager;
	}

	public void setAchievementManager(AchievementManager achievementManager) {
		this.achievementManager = achievementManager;
	}

	

}
