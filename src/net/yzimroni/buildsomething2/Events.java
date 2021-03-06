package net.yzimroni.buildsomething2;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import net.yzimroni.buildsomething2.game.bonuses.bonuses.Bonus;
import net.yzimroni.buildsomething2.player.BPlayer;
import net.yzimroni.buildsomething2.utils.Utils;

public class Events implements Listener {
	private BuildSomethingPlugin plugin;
	
	public Events(BuildSomethingPlugin p) {
		plugin = p;
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
				Utils.teleportSpawn(((Player) event.getEntity()));
				event.setCancelled(true);
			} else if (event.getCause() != EntityDamageEvent.DamageCause.SUICIDE) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onHunger(FoodLevelChangeEvent e) {
		e.setFoodLevel(20);
	}
	
	@EventHandler
	public void onItemCraft(CraftItemEvent e) {
		if (e.getWhoClicked() instanceof Player) {
			Player p = (Player) e.getWhoClicked();
			if (!p.isOp()) {
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void cmd(final PlayerCommandPreprocessEvent e) {
		// TODO Make it a proper command or deleting them (some of them are just for testing/debugging)
		if (!e.getPlayer().isOp()) return;
		if (e.getMessage().equalsIgnoreCase("/fly")) {
			e.setCancelled(true);
			Player p = e.getPlayer();
			if (p.getAllowFlight()) {
				p.setFlying(false);
				p.setAllowFlight(false);
			} else {
				p.setAllowFlight(true);
				p.setFlying(true);
			}
			p.sendMessage("Set fly to " + p.getAllowFlight());
		} else if (e.getMessage().equalsIgnoreCase("/h")) {
			BPlayer bp = plugin.getPlayerManager().getPlayer(e.getPlayer());
			e.getPlayer().sendMessage("Hebrew word: " + bp.isHebrewWords());
			bp.setHebrewWords(!bp.isHebrewWords());
			e.getPlayer().sendMessage("Hebrew word (set): " + bp.isHebrewWords());
			e.setCancelled(true);
		} else if (e.getMessage().equalsIgnoreCase("/hat")) {
			e.setCancelled(true);
			ItemStack i = e.getPlayer().getInventory().getItemInMainHand();
			if (i == null || i.getType() == null || i.getType() == Material.AIR) {
				e.getPlayer().sendMessage("invalid item");
			}
			e.getPlayer().getInventory().setHelmet(i);
			e.getPlayer().sendMessage("set!");
		} else if (e.getMessage().equalsIgnoreCase("/bonusitems")) {
			e.setCancelled(true);
			for (Bonus b : plugin.getGameManager().getBonusesManager().getBonuses()) {
				e.getPlayer().getInventory().addItem(b.getItem());
			}
		} else if (e.getMessage().equalsIgnoreCase("/effectsmenu")) {
			e.setCancelled(true);
			plugin.getGameManager().getEffectsManager().openMainInv(e.getPlayer());
		} else if (e.getMessage().startsWith("/effectrun ")) {
			e.setCancelled(true);
			int id = Utils.getInt(e.getMessage().toLowerCase().split(" ")[1]);
			plugin.getGameManager().getEffectsManager().getEffectById(id).run(e.getPlayer().getEyeLocation(), e.getPlayer());
		} else if (e.getMessage().equalsIgnoreCase("/lang")) {
			e.setCancelled(true);
			e.getPlayer().sendMessage(e.getPlayer().spigot().getLocale());
			e.getPlayer().sendMessage(e.getPlayer().getAddress().getHostName());
		} else if (e.getMessage().equalsIgnoreCase("/botarea")) {
			/*Plot pl = PlotMeCoreManager.getInstance().getPlotById(Utils.plotId("1;0"), PlotMeCoreManager.getInstance().getFirstWorld());
			World w = Bukkit.getWorld("plotworld");
			//Location l1 = new Location(w, pl.getPlotBottomLoc().getBlockX(), pl.getPlotBottomLoc().getBlockY(), pl.getPlotBottomLoc().getBlockZ());
			Location l1 = new Location(w, pl.getBottomX(), 64, pl.getTopZ());
			Location l2 = new Location(w, pl.getPlotTopLoc().getBlockX(), pl.getPlotTopLoc().getBlockY(), pl.getPlotTopLoc().getBlockZ());
			Cuboid c = new Cuboid(l1, l2);
			List<Block> b = c.getBlocks();
			
			Location l1 = new Location(w, pl.getPlotBottomLoc().getBlockX(), 64, pl.getPlotBottomLoc().getBlockZ());
			Location l2 = new Location(w, pl.getPlotTopLoc().getBlockX(), pl.getPlotTopLoc().getBlockY(), pl.getPlotTopLoc().getBlockZ());

			Bukkit.broadcastMessage(l1.getBlockX() + ", " + l1.getBlockY() + ", " + l1.getBlockZ());*/
			
			/*World w = Bukkit.getWorld("plotworld");
			Plot pl = PlotMeCoreManager.getInstance().getPlotById(Utils.plotId("1;0"), PlotMeCoreManager.getInstance().getFirstWorld());
			Location l1 = new Location(w, pl.getPlotBottomLoc().getBlockX(), 64, pl.getPlotBottomLoc().getBlockZ());
			Location l2 = new Location(w, pl.getPlotTopLoc().getBlockX(), pl.getPlotTopLoc().getBlockY(), pl.getPlotTopLoc().getBlockZ());

			
			Cuboid c = new Cuboid(l1, l2);
			int x = c.getUpperX();
			int y = c.getUpperY();
			int z = c.getUpperZ();
			Bukkit.broadcastMessage(x + ", " + y + ", " + z);
			
			e.setCancelled(true);*/
		}
		/* else if (e.getMessage().equalsIgnoreCase("/ld")) {
			if (ld == null) {
				ld = e.getPlayer().getLocation();
				e.getPlayer().sendMessage("set!");
			} else {
				e.getPlayer().sendMessage("" + e.getPlayer().getLocation().distanceSquared(ld));
			}
		}*/
		
		
		
		
		
		/*else if (e.getMessage().equalsIgnoreCase("/npcfix")) {
			e.setCancelled(true);
			for (NPC c : CitizensAPI.getNPCRegistry()) {
				e.getPlayer().sendMessage("NPC: " + c.getName() + " Starting");
				Trait t = c.getTrait(BSTrait.class);
				if (t != null && t instanceof BSTrait) {
					e.getPlayer().sendMessage("NPC: " + c.getName() + " Found BSTrait");
					if (c.data().has("word")) {
						e.getPlayer().sendMessage("NPC: " + c.getName() + " Already have data, skipping");
						continue;
					}
					BSTrait bt = (BSTrait) t;
					c.data().setPersistent("word", bt.getWordId());
					e.getPlayer().sendMessage("NPC: " + c.getName() + " Added data");
					c.removeTrait(BSTrait.class);
					e.getPlayer().sendMessage("NPC: " + c.getName() + " Removed BSTrait");
				}
				e.getPlayer().sendMessage("NPC: " + c.getName() + " Finish");
			}
		}*/
		
		
		/*if (e.getMessage().equalsIgnoreCase("/npctest")) {
			e.setCancelled(true);
			plugin.getGamemanager().getPlotsManager().getNpcManager().createNPC(e.getPlayer().getLocation(), e.getPlayer(), plugin.getGamemanager().randomWord());
		}*/
		/*if (e.getMessage().equalsIgnoreCase("/worldedittest")) {
			e.setCancelled(true);
			LocalConfiguration lc = new LocalConfiguration() {
				
				@Override
				public void load() {
					// 
					
				}
			};
			lc.disallowedBlocks.clear();
			lc.disallowedBlocks.add("1:0");
			WorldEdit.getInstance().getSessionManager().findByName(e.getPlayer().getName()).setConfiguration(lc);
		}*/
	}
	
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent e) {
		if (!e.getPlayer().isOp()) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		e.setMessage(Utils.uppersLettersFixed(e.getMessage()));
	}
	
	

}
