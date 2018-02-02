package net.yzimroni.buildsomething2.game.blocks;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.World;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;

import net.yzimroni.buildsomething2.BuildSomethingPlugin;
import net.yzimroni.buildsomething2.player.BPlayer;
import net.yzimroni.buildsomething2.utils.Utils;

public class BlockShop implements Listener {

	private BlockManager manager;
	private BuildSomethingPlugin plugin;
	
	public BlockShop(BuildSomethingPlugin p) {
		plugin = p;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	public void initManager() {
		manager = plugin.getGameManager().getBlockManager();
	}
	
	@EventHandler
	public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
		// TODO Make it a proper command
		if (e.getPlayer().isOp()) {
			if (e.getMessage().equalsIgnoreCase("/clearitemframes")) {
				e.setCancelled(true);
				WorldEditPlugin we = (WorldEditPlugin) plugin.getServer().getPluginManager().getPlugin("WorldEdit");
				Selection s = we.getSelection(e.getPlayer());
				if (s == null) {
					return;
				}
				World w = s.getWorld();
				for (ItemFrame i : w.getEntitiesByClass(ItemFrame.class)) {
					if (s.contains(i.getLocation())) {
						i.setItem(null);
					}
				}
			} else if (e.getMessage().equalsIgnoreCase("/createshoparea")) {
				e.setCancelled(true);
				WorldEditPlugin we = (WorldEditPlugin) plugin.getServer().getPluginManager().getPlugin("WorldEdit");
				Selection s = we.getSelection(e.getPlayer());
				if (s == null) {
					return;
				}
				createShopArea(s);
			} else if (e.getMessage().startsWith("/addblock ")) {
				e.setCancelled(true);
				String[] split = e.getMessage().split(" ");
				if (split.length == 2) {
					if (Utils.isDouble(split[1])) {
						double d = Utils.getDouble(split[1]);
						if (d >= 0) {
							ItemStack i = e.getPlayer().getInventory().getItemInMainHand();
							if (i == null || i.getType() == null || i.getType() == Material.AIR || i.getAmount() == 0) {
								e.getPlayer().sendMessage("invalid item");
								return;
							}
							if (i.getType().isBlock()) {
								BSBlock t = manager.getByType(i.getType(), Utils.getData(i.getData()));
								if (t == null) {
									PreparedStatement pre = plugin.getDB().getPrepare("INSERT INTO blocks (type,data,price) VALUES(?,?,?)");
									try {
										pre.setString(1, i.getType().name());
										pre.setByte(2, Utils.getData(i.getData()));
										pre.setDouble(3, d);
										pre.executeUpdate();
										pre.close();
										e.getPlayer().sendMessage("Block added!");
										manager.initBlocks();
									} catch (SQLException e1) {
										e1.printStackTrace();
									}
								} else {
									e.getPlayer().sendMessage("the block already in the system");
								}
							} else {
								e.getPlayer().sendMessage("you must hold a block");
							}
						} else {
							e.getPlayer().sendMessage("invalid price");
						}
					} else {
						e.getPlayer().sendMessage("invalid double");
					}
				} else {
					e.getPlayer().sendMessage("/addblock <price>");
				}
			}
		}
	}
	
	public void createShopArea(Selection s) {
		Iterator<BSBlock> it = manager.getBlocks().iterator();
		World w = s.getWorld();
		for (ItemFrame i : w.getEntitiesByClass(ItemFrame.class)) {
			if (it.hasNext()) {
				if (s.contains(i.getLocation())) {
					BSBlock b = nextBlock(it);
					if (b == null) {
						break;
					}
					ItemStack is = b.toItemStack();
					ItemMeta im = is.getItemMeta();
					im.setDisplayName(ChatColor.AQUA + "Price: " + ChatColor.GREEN + b.getPrice() + "$");
					im.setLore(Arrays.asList("SHOPITEM", "ID:" + b.getId()));
					
					is.setItemMeta(im);
					i.setItem(is);
				}
			} else {
				break;
			}
		}
	}
	
	private BSBlock nextBlock(Iterator<BSBlock> it) {
		BSBlock b = null;
		for (int i = 0; i<500; i++) {
			if (it.hasNext()) {
				BSBlock bm = it.next();
				if (bm.getPrice() != 0) {
					b = bm;
					break;
				}
			} else {
				break;
			}
		}
		return b;
	}
	
	@EventHandler
	public void onPlayerInteractShop(PlayerInteractEntityEvent e) {
		if (e.getRightClicked() instanceof ItemFrame) {
			ItemFrame a = (ItemFrame) e.getRightClicked();
			ItemStack s = a.getItem();
			if (s == null || s.getType() == null || s.getType() == Material.AIR) {
				return;
			}
			if (s.hasItemMeta() && s.getItemMeta().hasLore()) {
				List<String> lore = s.getItemMeta().getLore();
				if (lore != null && !lore.isEmpty() && lore.size() == 2) {
					if (lore.get(0).equals("SHOPITEM")) {
						String sid = lore.get(1);
						if (sid.startsWith("ID:")) {
							a.setRotation(Rotation.COUNTER_CLOCKWISE_45);
							String[] split = sid.split(":");
							int id = Utils.getInt(split[1]);
							if (e.getPlayer().isSneaking()) {
								showBlockInfo(plugin.getPlayerManager().getPlayer(e.getPlayer().getUniqueId()), id);
							} else {
								buyBlock(plugin.getPlayerManager().getPlayer(e.getPlayer().getUniqueId()), id);
							}
						}
					}
				}
			}
		}
	}
	
	public void showBlockInfo(BPlayer p, int id) {
		BSBlock b = manager.getBlockById(id);
		String[] messages = new String[]{
				"====== Block Info: =======",
				"Name: " + b.getDisplayName(),
				"Price: " + (b.getPrice() == 0 ? "Free" : b.getPrice()),
				"You " + (manager.hasBlock(p, b) ? "" : "don't") + " have this block!"
		};
		p.getPlayer().sendMessage(messages);
	}
	
	public boolean buyBlock(BPlayer p, int id) {
		BSBlock b = manager.getBlockById(id);
		if (b.getPrice() == 0) {
			p.getPlayer().sendMessage("This Block is free!");
			return false;
		}
		if (manager.hasBlock(p, b)) {
			p.getPlayer().sendMessage("You already have " + b.getDisplayName() + "!");
			return false;
		}
		if (b != null) {
			if (plugin.getPlayerManager().getEconomy().has(p.getUUID(), b.getPrice()) && plugin.getPlayerManager().getEconomy().withdrawPlayer(p.getUUID(), b.getPrice())) {
				p.getData().addBlock(b.getId());
				p.getPlayer().sendMessage("You bought " + b.getDisplayName() + "!");
				plugin.getScoreboardManager().createPlayerScoreboard(p.getPlayer());
				return true;
			} else {
				p.getPlayer().sendMessage("You dont have enough money");
			}
		}
		return false;
	}
	
}
