package net.yzimroni.buildsomething2.game.effects.effects;

import java.util.List;

import net.yzimroni.buildsomething2.BuildSomethingPlugin;
import net.yzimroni.buildsomething2.player.BPlayer;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class Effect {

	protected BuildSomethingPlugin plugin;
	protected int VISIBILLITY = 200;
	
	public Effect(BuildSomethingPlugin p) {
		plugin = p;
	}
	
	public ItemStack getItem() {
		ItemStack i = new ItemStack(getItemType());
		ItemMeta im = i.getItemMeta();
		im.setDisplayName(ChatColor.RED + getName());
		i.setItemMeta(im);
		return i;
	}
	
	public abstract void run(Location l, Player p);
	
	public abstract Material getItemType();

	public abstract String getName();
	
	public abstract int getId();
		
	public void init() {
		
	}

	public boolean has(BPlayer p) {
		return getPrice() == 0 || p.getData().hasEffect(getId());
	}
	
	public abstract double getPrice();
	
	public boolean canBuy(BPlayer p) {
		return true;
	}
		
	public boolean isBuyable() {
		return true;
	}
	
	
	public List<Integer> getUsages() {
		return all();
	}
	
	public boolean canBeUse(int id) {
		return getUsages().contains(id);
	}

	protected List<Integer> all() {
		return plugin.getGameManager().getEffectsManager().getEffectViewsIds();
	}
	
	protected List<Integer> allWithout(List<Integer> l) {
		List<Integer> all = all();
		for (Integer i : l) {
			all.remove(i);
		}
		return all;
	}
	
	public int getMultiTimesRun() {
		return 30;
	}
	
}
