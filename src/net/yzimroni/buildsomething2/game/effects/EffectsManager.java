package net.yzimroni.buildsomething2.game.effects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.yzimroni.buildsomething2.BuildSomethingPlugin;
import net.yzimroni.buildsomething2.game.effects.effects.BoomBox;
import net.yzimroni.buildsomething2.game.effects.effects.DripLava;
import net.yzimroni.buildsomething2.game.effects.effects.DripWater;
import net.yzimroni.buildsomething2.game.effects.effects.Effect;
import net.yzimroni.buildsomething2.game.effects.effects.Explosion;
import net.yzimroni.buildsomething2.game.effects.effects.Firework;
import net.yzimroni.buildsomething2.game.effects.effects.Flame;
import net.yzimroni.buildsomething2.game.effects.effects.HappyVillager;
import net.yzimroni.buildsomething2.game.effects.effects.Heart;
import net.yzimroni.buildsomething2.game.effects.effects.Helix;
import net.yzimroni.buildsomething2.game.effects.effects.None;
import net.yzimroni.buildsomething2.game.effects.effects.Portal;
import net.yzimroni.buildsomething2.game.effects.views.EffectView;
import net.yzimroni.buildsomething2.game.effects.views.EndGameBuilder;
import net.yzimroni.buildsomething2.game.effects.views.KnowWord;
import net.yzimroni.buildsomething2.player.BPlayer;
import net.yzimroni.buildsomething2.utils.Utils;

public class EffectsManager implements Listener {
	private BuildSomethingPlugin plugin;
	private HashMap<Integer, Effect> effects;
	private HashMap<Integer, EffectView> views;
	private String MAIN_INV_TITLE = "Effects Menu";
	private String BUY_EFFECTS_TITLE = "Buy Effects";
	private String CHOOSE_EFFECTS_TITLE = "Choose Effect View";
	private String CHOOSE_EFFETCS_BROWSE_TITLE = "Choose Effects";
	private String BUY_EFFECTS_VIEWS_TITLE = "Buy Effect View";
	private List<Integer> ALL_VIEWS = new ArrayList<Integer>();
	
	public EffectsManager(BuildSomethingPlugin p) {
		plugin = p;
		effects = new HashMap<Integer, Effect>();
		views = new HashMap<Integer, EffectView>();
		
		initEffects();
		initViews();
		
		initViewsIdList();
		
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	private void initEffects() {
		addEffect(new None(plugin));
		addEffect(new Explosion(plugin));
		addEffect(new Firework(plugin));
		addEffect(new Flame(plugin));
		addEffect(new HappyVillager(plugin));
		addEffect(new DripWater(plugin));
		addEffect(new DripLava(plugin));
		addEffect(new Portal(plugin));
		addEffect(new Heart(plugin));
		addEffect(new Helix(plugin));
		addEffect(new BoomBox(plugin));
	}
	
	private void initViews() {
		addEffectView(new KnowWord(plugin));
		addEffectView(new EndGameBuilder(plugin));
	}
	
	private void initViewsIdList() {
		ALL_VIEWS.clear();
		for (EffectView e : views.values()) {
			ALL_VIEWS.add(e.getId());
		}
	}
	
	public List<Integer> getEffectViewsIds() {
		return new ArrayList<Integer>(ALL_VIEWS);
	}
	
	public Collection<Effect> getAllEffects() {
		return effects.values();
	}
	
	public Effect getEffectById(int id) {
		return effects.get(id);
	}
	
	private void addEffect(Effect e) {
		if (!effects.containsKey(e.getId())) {
			effects.put(e.getId(), e);
			e.init();
		} else {
			plugin.log.warning("TRY TO addEffect(" + e + ") for already existing effect!");
		}
	}
	
	public Effect getEffectByItem(ItemStack i) {
		for (Effect e : effects.values()) {
			if (e.getItem().isSimilar(i)) {
				return e;
			}
		}
		return null;
	}
	
	public EffectView getViewById(int id) {
		return views.get(id);
	}
	
	private void addEffectView(EffectView e) {
		if (!views.containsKey(e.getId())) {
			views.put(e.getId(), e);
		} else {
			plugin.log.warning("TRY TO addEffectView(" + e + ") for already existing effect view!");
		}
	}
	
	public EffectView getViewByItem(ItemStack i) {
		for (EffectView e : views.values()) {
			if (e.getItem().isSimilar(i)) {
				return e;
			}
		}
		return null;
	}
	
	public void openMainInv(Player p) {
		Inventory i = Bukkit.createInventory(p, 9, MAIN_INV_TITLE);
		i.addItem(getBuyEffectsItem());
		i.addItem(getChooseEffectsItem());
		
		p.openInventory(i);
	}
	
	public void openBuyEffectsInv(Player p) {
		Inventory i = Bukkit.createInventory(p, 6 * 9, BUY_EFFECTS_TITLE);
		BPlayer bp = plugin.getPlayerManager().getPlayer(p);
		
		for (Effect e : effects.values()) {
			ItemStack s = e.getItem();
			if (e.has(bp)) {
				Utils.addGlow(s);
			}
			i.addItem(s);
		}
		
		p.openInventory(i);
	}
	
	public void openChooseEffectsInv(Player p) {
		Inventory i = Bukkit.createInventory(p, 6 * 9, CHOOSE_EFFECTS_TITLE);
		BPlayer bp = plugin.getPlayerManager().getPlayer(p);
		
		for (EffectView e : views.values()) {
			ItemStack s = e.getItem();
			if (e.has(bp)) {
				Utils.addGlow(s);
			}
			i.addItem(s);
		}
		
		p.openInventory(i);
	}
	
	public void openChooseEffectsViewInv(Player p, EffectView eu) {
		BPlayer bp = plugin.getPlayerManager().getPlayer(p);
		Inventory i = Bukkit.createInventory(p, 6 * 9, CHOOSE_EFFETCS_BROWSE_TITLE);
		
		i.addItem(eu.getItem());
		Effect choose = eu.getEffect(bp);
		for (Effect e : eu.getAllEffects(bp)) {
			ItemStack s = e.getItem();
			if (e.getId() == choose.getId()) {
				Utils.addGlow(s);
			}
			i.addItem(s);
		}
		p.openInventory(i);
	}
	
	public void openBuyEffectViewInv(Player p, EffectView eu) {
		Inventory i = Bukkit.createInventory(p, 9, BUY_EFFECTS_VIEWS_TITLE);
		i.addItem(eu.getItem());
		i.addItem(getAcceptItem());
		i.addItem(getDeclineItem());
		
		
		/*i.addItem(eu.getItem());
		Effect choose = eu.getEffect(bp);
		for (Effect e : eu.getAllEffects(bp)) {
			ItemStack s = e.getItem();
			if (e.getId() == choose.getId()) {
				Utils.addGlow(s);
			}
			i.addItem(s);
		}*/
		p.openInventory(i);
	}
	
	private ItemStack getAcceptItem() {
		ItemStack i = new ItemStack(Material.DIAMOND_BLOCK);
		ItemMeta im = i.getItemMeta();
		im.setDisplayName("Accept");
		i.setItemMeta(im);
		
		return i;
	}
	
	private ItemStack getDeclineItem() {
		ItemStack i = new ItemStack(Material.BEDROCK);
		ItemMeta im = i.getItemMeta();
		im.setDisplayName("Decline");
		i.setItemMeta(im);
		
		return i;
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		ItemStack current = e.getCurrentItem();
		if (e.getInventory().getTitle().equals(MAIN_INV_TITLE)) {
			e.setCancelled(true);
			if (current == null || current.getType() == null || current.getType() == Material.AIR) {
				return;
			}
			if (getBuyEffectsItem().isSimilar(current)) {
				openBuyEffectsInv(p);
			} else if (getChooseEffectsItem().isSimilar(current)) {
				openChooseEffectsInv(p);
			}
			
		} else if (e.getInventory().getTitle().equals(BUY_EFFECTS_TITLE)) {
			BPlayer bp = plugin.getPlayerManager().getPlayer(p);
			e.setCancelled(true);
			if (current == null || current.getType() == null || current.getType() == Material.AIR) {
				return;
			}
			Effect ef = getEffectByItem(current);
			if (ef != null) {
				if (ef.has(bp)) {
					p.sendMessage("You already have this effect!");
				} else {
					if (buyEffect(bp, ef)) {
						p.closeInventory();
					}
				}
			}
			
		} else if (e.getInventory().getTitle().equals(CHOOSE_EFFECTS_TITLE)) {
			BPlayer bp = plugin.getPlayerManager().getPlayer(p);
			e.setCancelled(true);
			if (current == null || current.getType() == null || current.getType() == Material.AIR) {
				return;
			}
			EffectView eu = getViewByItem(current);
			if (eu != null) {
				if (eu.has(bp)) {
					openChooseEffectsViewInv(p, eu);
				} else {
					openBuyEffectViewInv(p, eu);
				}
			}
		} else if (e.getInventory().getTitle().equals(CHOOSE_EFFETCS_BROWSE_TITLE)) {
			BPlayer bp = plugin.getPlayerManager().getPlayer(p);
			e.setCancelled(true);
			if (current == null || current.getType() == null || current.getType() == Material.AIR) {
				return;
			}
			Effect ef = getEffectByItem(current);
			EffectView eu = getViewByItem(e.getInventory().getItem(0));
			
			if (ef != null && eu != null) {
				if (eu.getEffect(bp).getId() == ef.getId()) {
					p.sendMessage("This effect is already choose for " + eu.getName() + "!");
					return;
				}
				bp.getData().setEffectChoose(eu.getId(), ef.getId());
				p.sendMessage("Change the effect of " + eu.getName() + " to " + ef.getName());
			}
		} else if (e.getInventory().getTitle().equals(BUY_EFFECTS_VIEWS_TITLE)) {
			BPlayer bp = plugin.getPlayerManager().getPlayer(p);
			e.setCancelled(true);
			if (current == null || current.getType() == null || current.getType() == Material.AIR) {
				return;
			}
			EffectView eu = getViewByItem(e.getInventory().getItem(0));
			if (eu != null) {
				if (current.isSimilar(getAcceptItem())) {
					//TO DO buy
					buyEffectView(bp, eu);
				} else if (current.isSimilar(getDeclineItem())) {
					p.closeInventory();
				}
			}
		}
	}
	
	public boolean buyEffectView(BPlayer p, EffectView eu) {
		
		if (!eu.isBuyable() || !eu.canBuy(p)) {
			p.getPlayer().sendMessage("You can't buy this view");
			return false;
		}
		
		if (eu.getPrice() == 0) {
			p.getPlayer().sendMessage("This Effect view is free!");
			return false;
		}
		
		if (eu.has(p)) {
			p.getPlayer().sendMessage("You already have this view!");
			return false;
		}
		
		if (plugin.getPlayerManager().getEconomy().has(p.getUUID(), eu.getPrice()) && plugin.getPlayerManager().getEconomy().withdrawPlayer(p.getUUID(), eu.getPrice())) {
			//p.getData().addEffect(eu.getId());
			p.getData().addView(eu.getId());
			p.getPlayer().sendMessage("You bought " + eu.getName() + "!");
			return true;
		} else {
			p.getPlayer().sendMessage("You dont have enough money");
			return false;
		}
	}
	
	public boolean buyEffect(BPlayer p, Effect e) {
		
		if (!e.isBuyable() || !e.canBuy(p)) {
			p.getPlayer().sendMessage("You can't buy this effect");
			return false;
		}
		
		if (e.getPrice() == 0) {
			p.getPlayer().sendMessage("This Effect is free!");
			return false;
		}
		
		if (e.has(p)) {
			p.getPlayer().sendMessage("You already have this effect!");
			return false;
		}
		
		if (plugin.getPlayerManager().getEconomy().has(p.getUUID(), e.getPrice()) && plugin.getPlayerManager().getEconomy().withdrawPlayer(p.getUUID(), e.getPrice())) {
			p.getData().addEffect(e.getId());
			p.getPlayer().sendMessage("You bought " + e.getName() + "!");
			return true;
		} else {
			p.getPlayer().sendMessage("You dont have enough money");
			return false;
		}
	}
	
	private ItemStack getBuyEffectsItem() {
		ItemStack i = new ItemStack(Material.ENCHANTMENT_TABLE);
		ItemMeta im = i.getItemMeta();
		
		im.setDisplayName(ChatColor.GREEN + "Buy Effects");
		
		i.setItemMeta(im);
		return i;
	}
	
	private ItemStack getChooseEffectsItem() {
		ItemStack i = new ItemStack(Material.PISTON_BASE);
		ItemMeta im = i.getItemMeta();
		
		im.setDisplayName(ChatColor.GREEN + "Choose Effects");
		
		i.setItemMeta(im);
		return i;
	}
}
