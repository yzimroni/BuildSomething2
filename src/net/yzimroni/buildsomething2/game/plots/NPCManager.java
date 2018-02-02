package net.yzimroni.buildsomething2.game.plots;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.LookClose;
import net.yzimroni.buildsomething2.BuildSomethingPlugin;
import net.yzimroni.buildsomething2.game.Word;
import net.yzimroni.buildsomething2.player.BPlayer;

public class NPCManager implements Listener {
	private BuildSomethingPlugin plugin;
	
	public NPCManager(BuildSomethingPlugin p) {
		plugin = p;
		initCitizens();
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	private void initCitizens() {
		if(Bukkit.getServer().getPluginManager().getPlugin("Citizens") == null || !Bukkit.getServer().getPluginManager().getPlugin("Citizens").isEnabled()) {
			plugin.getLogger().log(Level.SEVERE, "Citizens 2.0 not found or not enabled");
			Bukkit.getServer().getPluginManager().disablePlugin(plugin);	
			return;
		}
	}
	
	public NPC createPlotBuildNPC(Location l, Player p, Word w) {
		l = new Location(l.getWorld(), l.getX() + 1.5, 66, l.getZ() + 1.5);
		NPC c = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, p.getName());
		c.getTrait(LookClose.class).lookClose(true);
		c.data().setPersistent("word", w.getId());
		c.spawn(l);
		return c;
	}
	
	public boolean removeNPC(int id) {
		NPC c = CitizensAPI.getNPCRegistry().getById(id);
		if (c == null) {
			return false;
		}
		if (c.isSpawned()) {
			c.despawn();
		}
		c.destroy();
		return true;
	}
	
	@EventHandler
	public void onNPCRightClick(NPCRightClickEvent e) {
		if (e.getNPC().data().has("word")) {
			if (!e.getNPC().isSpawned()) return;
			Word w = null;
			if (e.getNPC().getEntity().hasMetadata("word_o")) {
				Object o = e.getNPC().getEntity().getMetadata("word_o").get(0).value();
				if (o instanceof Word) {
					w = (Word) o;
				} else {
					e.getNPC().getEntity().removeMetadata("word_o", plugin);
				}
			}
			if (w == null) {
				int id = e.getNPC().data().get("word");
				w = plugin.getGameManager().getWord(id);
				e.getNPC().getEntity().setMetadata("word_o", new FixedMetadataValue(plugin, w));
			}
			if (w != null) {
				BPlayer bp = plugin.getPlayerManager().getPlayer(e.getClicker());
				e.getClicker().sendMessage("The word was " + w.getWordEnglish() + (bp.isHebrewWords() ? " (" + plugin.hebrewMessage(e.getClicker(), w.getWordHebrew()) + ")" : ""));
			}

		}
	}
	
	
}
