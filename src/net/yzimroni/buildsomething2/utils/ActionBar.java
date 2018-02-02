package net.yzimroni.buildsomething2.utils;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_9_R1.IChatBaseComponent;
import net.minecraft.server.v1_9_R1.PacketPlayOutChat;
import net.yzimroni.buildsomething2.BuildSomethingPlugin;

public class ActionBar {
	private BuildSomethingPlugin plugin;
	private HashMap<UUID, String> messages;
	
	public ActionBar(BuildSomethingPlugin p) {
		plugin = p;
		messages = new HashMap<UUID, String>();
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				sendBars();
				
			}
		}, 20, 20);
	}
	
	private void sendBars() {
		if (messages.isEmpty()) return;
		for (Entry<UUID, String> i : messages.entrySet()) {
			Player p = Bukkit.getPlayer(i.getKey());
			if (p == null) {
				messages.remove(i.getKey());
				continue;
			}
			sendPacket(p, i.getValue());
		}
	}
	
	private void sendPacket(Player p, String s) {
		IChatBaseComponent text = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + s + "\"}");
	    PacketPlayOutChat bar = new PacketPlayOutChat(text, (byte) 2);
	    ((CraftPlayer) p).getHandle().playerConnection.sendPacket(bar);
	}
	
	public boolean hasActionBar(Player p) {
		return messages.containsKey(p.getUniqueId());
	}
	
	public String getActionBar(Player p) {
		return messages.get(p.getUniqueId());
	}
	
	public void removeActionBar(Player p) {
		messages.remove(p.getUniqueId());
	}
	
	public void sendActionBar(Player p, String s) {
		if (s == null || s.isEmpty()) {
			removeActionBar(p);
			return;
		}
		messages.put(p.getUniqueId(), s);
		sendPacket(p, s);
	}
}
