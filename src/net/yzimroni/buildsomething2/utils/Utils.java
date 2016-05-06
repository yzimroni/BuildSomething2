package net.yzimroni.buildsomething2.utils;

import io.puharesource.mc.titlemanager.api.TitleObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.material.MaterialData;

import com.worldcretornica.plotme_core.PlotId;

public class Utils {
	
	public static void setPlayerListHeader(Player player,String header, String footer){
		TitleAPI.sendTabTitle(player, header, footer);
       /* CraftPlayer cplayer = (CraftPlayer) player;
        PlayerConnection connection = cplayer.getHandle().playerConnection;
        IChatBaseComponent hj = ChatSerializer.a("{\"text\":\""+header+"\"}");
        PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter();
        try{
            Field headerField = packet.getClass().getDeclaredField("a");
            headerField.setAccessible(true);
            headerField.set(packet, hj);
            headerField.setAccessible(!headerField.isAccessible());
          
        } catch (Exception e){
            e.printStackTrace();
        }
        connection.sendPacket(packet);*/
    }
	
	@SuppressWarnings("deprecation")
	public static OfflinePlayer getOfflinePlayer(String name) {
		return Bukkit.getOfflinePlayer(name);
	}
	
	@SuppressWarnings("deprecation")
	public static int getId(Material m) {
		return m.getId();
	}
	
	public static int getId(ItemStack s) {
		return getId(s.getType());
	}
	
	public static byte getData(ItemStack s) {
		return getData(s.getData());
	}
	
	@SuppressWarnings("deprecation")
	public static byte getData(MaterialData m) {
		return m.getData();
	}
	
	public static PlotId plotId(String s) {
		return new PlotId(s);
	}

	public static String timeString(long t) {
		long days = t / 86400;
		t = t - (days * 86400);
		long hours = t / 3600;
		t = t - (hours * 3600);
		long minutes = t / 60;
		t = t - (minutes * 60);
		String content = "";
		if (days != 0) {
			content += days + " day" + (days == 1 ? "" : "s");
		}
		// && days != 0
		if (hours != 0) {
			if (!content.isEmpty()) {
				content += ", ";
			}
			content += hours + " hour" + (hours == 1 ? "" : "s");
		}
		if (minutes != 0) {
			if (!content.isEmpty()) {
				content += ", ";
			}
			content += minutes + " minute" + (minutes == 1 ? "" : "s");
		}
		if (content.isEmpty() || (!content.isEmpty() && t != 0)) {
			if (!content.isEmpty()) {
				content += " and ";
			}

			content += t + " second" + (t == 1 ? "" : "s");

		}
		return content;
	}
	
	public static String foramtTimeShort(long t) {
		long minutes = t / 60;
		t = t - (minutes * 60);
		return (minutes <= 9 ? "0" : "") + minutes + ":" + (t <= 9 ? "0" : "") + t;
	}
	
	public static void teleportSpawn(Player p) {
		p.teleport(getSpawn());
	}
	
	public static Location getSpawn() {
		return new Location(Bukkit.getWorld("Island"), 116.5, 101.5, 83.5);
	}
	
	public static String uppersLettersFixed(String s) {
		if (s.length() <= 3) {
			return s;
		}
		// String str = s;
		return s.substring(0, 1).toUpperCase()
				+ s.substring(1, s.length()).toLowerCase();
	}
	
	public static boolean isInt(String s) {
		try {
			Integer.valueOf(s);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public static int getInt(String s) {
		try {
			return Integer.valueOf(s);
		} catch (Exception e) {}
		return 0;
	}
	
	public static boolean isDouble(String s) {
		try {
			Double.valueOf(s);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	

    public static boolean getBoolean(String s) {
        if (s == null || s.isEmpty()) return false;
        if (s.equalsIgnoreCase("true") || s.equalsIgnoreCase("on") || s.equalsIgnoreCase("1") || s.equalsIgnoreCase("yes") || s.equalsIgnoreCase("yep") || s.equalsIgnoreCase("yea")) {
            return true;
        } else {
            return false;
        }
    }
	
	public static double getDouble(String s) {
		try {
			return Double.valueOf(s);
		} catch (Exception e) {}
		return 0;
	}
	
	public static int getFirstFreeInvSlot(PlayerInventory i) {
		for (int n = 9; n<36; n++) {
			ItemStack is = i.getItem(n);
			if (is == null || is.getType() == null || is.getType() == Material.AIR || is.getAmount() == 0) {
				return n;
			}
		}
		return -1;
	}
	
	
	public static void sendTitle(Player player, String title, int fadeIn, int stay, int fadeOut) {
		  new TitleObject(title, TitleObject.TitleType.TITLE).setFadeIn(fadeIn).setStay(stay).setFadeOut(fadeOut).send(player);
		  /*TitleObject o = new TitleObject(title, TitleType.TITLE);
		  o.setTitle(title)*/
	}
	
	public static void sendTitleSub(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
		  new TitleObject(title, TitleObject.TitleType.TITLE).setSubtitle(subtitle).setFadeIn(fadeIn).setStay(stay).setFadeOut(fadeOut).send(player);
	}
	
	public static void addGlow(ItemStack s) {
		s.addUnsafeEnchantment(Enchantment.SILK_TOUCH, 32);
		s.addUnsafeEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 32);
	}
	
	public static String formatPlayerList(List<UUID> players, List<UUID> exclude) {
		String result = "";
		String last_name = "";
		for (UUID u : players) {
			if (!exclude.contains(u)) {
				String name = Bukkit.getOfflinePlayer(u).getName();
				if (!last_name.isEmpty()) {
					if (!result.isEmpty()) {
						result += ", ";
					}
					result += last_name;
				}
				last_name = name;
			}
		}
		if (!last_name.isEmpty()) {
			if (!result.isEmpty()) {
				result += " and ";
			}
			result += last_name;
		}
		return result;
	}
	
	public static String formatPlayerList(List<UUID> players, UUID... exclude) {
		List<UUID> ex = new ArrayList<UUID>();
		for (int i = 0; i < exclude.length; i++) {
			ex.add(exclude[i]);
		}
		return formatPlayerList(players, ex);
	}

		
}
