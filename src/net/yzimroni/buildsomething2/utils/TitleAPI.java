package net.yzimroni.buildsomething2.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TitleAPI {
	
	  public static void sendPacket(Player player, Object packet) {
	    try {
	      Object handle = player.getClass().getMethod("getHandle", new Class[0]).invoke(player, new Object[0]);
	      Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
	      playerConnection.getClass().getMethod("sendPacket", new Class[] { getNMSClass("Packet") }).invoke(playerConnection, new Object[] { packet });
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	  }

	  public static Class<?> getNMSClass(String name) {
	    String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
	    try {
	      return Class.forName("net.minecraft.server." + version + "." + name);
	    } catch (ClassNotFoundException e) {
	      e.printStackTrace(); }
	    return null;
	  }

	  public static void sendTabTitle(Player player, String header, String footer) {
	    try
	    {
	      Object tabHeader = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke(null, new Object[] { "{\"text\":\"" + header + "\"}" });
	      Object tabFooter = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke(null, new Object[] { "{\"text\":\"" + footer + "\"}" });
	      
	      Constructor<?> titleConstructor = getNMSClass("PacketPlayOutPlayerListHeaderFooter").getConstructor(new Class[] { getNMSClass("IChatBaseComponent") });
	      Object packet = titleConstructor.newInstance(new Object[] { tabHeader });
	      Field field = packet.getClass().getDeclaredField("b");
	      field.setAccessible(true);
	      field.set(packet, tabFooter);
	      
	      sendPacket(player, packet);
	    } catch (Exception ex) {
	      ex.printStackTrace();
	    }
	  }


}
