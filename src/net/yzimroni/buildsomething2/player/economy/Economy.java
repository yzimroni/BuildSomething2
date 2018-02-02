package net.yzimroni.buildsomething2.player.economy;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import net.yzimroni.buildsomething2.BuildSomethingPlugin;

public class Economy implements Listener {
	private BuildSomethingPlugin plugin;
	private HashMap<UUID, Double> money = new HashMap<UUID, Double>();
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		if (!hasAccount(e.getPlayer().getUniqueId())) {
			createAccount(e.getPlayer().getUniqueId());
		}
	}
	
	public void saveAll() {
		for (UUID u : money.keySet()) {
			save(u);
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		remove(e.getPlayer().getUniqueId());
	}
	
	public Economy(BuildSomethingPlugin p) {
		plugin = p;
	}
	
	public void onDisable() {
		saveAll();
	}
	
	public boolean hasAccount(UUID u) {
		try {
			PreparedStatement pre = plugin.getDB().getPrepare("SELECT * FROM economy WHERE UUID=?");
			pre.setString(1, u.toString());
			ResultSet rs = pre.executeQuery();
			return rs.first();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public void createAccount(UUID u) {
		PreparedStatement pre = plugin.getDB().getPrepare("INSERT INTO economy (UUID) VALUES(?)");
		try {
			pre.setString(1, u.toString());
			pre.executeUpdate();
			pre.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public double getBalance(UUID u) {
		if (money.containsKey(u)) {
			return money.get(u);
		}
		return load(u);
	}
	
	public double getBalance(Player p) {
		return getBalance(p.getUniqueId());
	}
	
	public double getBotCoins(UUID u, boolean delete) {
		PreparedStatement pre = plugin.getDB().getPrepare("SELECT * FROM bot_coins WHERE UUID=?");
		ResultSet rs = null;
		try {
			pre.setString(1, u.toString());
			rs = pre.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			if (rs.next()) {
				final double coins = rs.getDouble("coins");
				rs.close();
				pre.close();
				if (delete) {
					PreparedStatement delPre = plugin.getDB().getPrepare("DELETE FROM bot_coins WHERE UUID=?");
					delPre.setString(1, u.toString());
					delPre.executeUpdate();
					delPre.close();
				}
				return coins;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;

	}
	
	public void addBotCoins(UUID u, double coins) {
		Player p = Bukkit.getPlayer(u);
		if (p != null) {
			depositPlayer(p.getUniqueId(), coins);
			p.sendMessage(ChatColor.GREEN + "You received " + ChatColor.YELLOW + coins + " coins" + ChatColor.GREEN + " From bot builds");
		} else {
			PreparedStatement pre = plugin.getDB().getPrepare("INSERT INTO bot_coins (UUID,coins) VALUES (?,?) ON DUPLICATE KEY UPDATE coins += ?");
			try {
				pre.setString(1, u.toString());
				pre.setDouble(2, coins);
				pre.setDouble(3, coins);
				pre.executeUpdate();
				pre.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void set(UUID u, double d) {
		if (money.containsKey(u)) {
			money.remove(u);
		}
		money.put(u, d);
	}
	
	public boolean depositPlayer(UUID u, double d) {
		if (d <= 0) return false;
		set(u, getBalance(u) + d);
		return true;
	}
	
	public boolean withdrawPlayer(UUID u, double d) {
		if (d <= 0) return false;
		if (!has(u, d)) return false;
		set(u, getBalance(u) - d);
		return true;
	}
	
	public boolean has(UUID u, double d) {
		return getBalance(u) >= d;
	}
	
	public void remove(UUID u) {
		if (money.containsKey(u)) {
			save(u);
			money.remove(u);
		}
	}
	
	private void save(UUID u) {
		PreparedStatement pre = plugin.getDB().getPrepare("UPDATE economy SET amount=? WHERE UUID=?");
		try {
			pre.setDouble(1, money.get(u));
			pre.setString(2, u.toString());
			pre.executeUpdate();
			pre.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private double load(UUID u) {
		if (money.containsKey(u)) return money.get(u);
		try {
			PreparedStatement pre = plugin.getDB().getPrepare("SELECT * FROM economy WHERE UUID=?");
			pre.setString(1, u.toString());
			ResultSet rs = pre.executeQuery();
			if (rs.first()) {
				money.put(u, rs.getDouble("amount"));
			} else {
				throw new IllegalArgumentException("Economy account not found for player " + u.toString());
			}
			pre.close();
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return money.get(u);
	}
}
