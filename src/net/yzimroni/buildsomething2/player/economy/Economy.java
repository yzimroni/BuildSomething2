package net.yzimroni.buildsomething2.player.economy;

import java.sql.ResultSet;
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
		money.clear();
	}
	
	public boolean hasAccount(UUID u) {
		try {
			ResultSet rs = plugin.getDB().get("SELECT * FROM economy WHERE UUID='" + u.toString() + "'");			
			return rs.first();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public void createAccount(UUID u) {
		plugin.getDB().set("INSERT INTO economy (UUID) VALUES('" + u.toString() + "')");
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
		ResultSet rs = plugin.getDB().get("SELECT * FROM bot_coins WHERE UUID='" + u + "'");
		try {
			if (rs.next()) {
				final double coins = rs.getDouble("coins");
				if (delete) {
					plugin.getDB().set("DELETE FROM bot_coins WHERE UUID='" + u + "'");
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
			plugin.getDB().set("INSERT INTO bot_coins (UUID,coins) VALUES ('" + u + "','" + coins + "') ON DUPLICATE KEY UPDATE coins += '" + coins + "'");
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
			//plugin.getDB().set("UPDATE economy SET amount='" + money.get(u) + "' WHERE UUID='" + u.toString() + "'");
			money.remove(u);
			//TO DO
		}
	}
	
	private void save(UUID u) {
		plugin.getDB().set("UPDATE economy SET amount='" + money.get(u) + "' WHERE UUID='" + u.toString() + "'");
	}
	
	private double load(UUID u) {
		if (money.containsKey(u)) return money.get(u);
		try {
			ResultSet rs = plugin.getDB().get("SELECT * FROM economy WHERE UUID='" + u.toString() + "'");
			if (rs.first()) {
				money.put(u, rs.getDouble("amount"));
			} else {
				throw new IllegalArgumentException("Economy account not found for player " + u.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return money.get(u);
		//TO DO
	}
}
