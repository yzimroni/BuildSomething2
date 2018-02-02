package net.yzimroni.buildsomething2.game.effects.views;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.yzimroni.buildsomething2.BuildSomethingPlugin;
import net.yzimroni.buildsomething2.game.effects.effects.Effect;
import net.yzimroni.buildsomething2.player.BPlayer;

public abstract class EffectView {
	
	protected BuildSomethingPlugin plugin;
	
	public EffectView(BuildSomethingPlugin p) {
		plugin = p;
	}
	
	public abstract String getName();
	
	public abstract int getId();
	
	public abstract Material getItemType();
	
	public ItemStack getItem() {
		ItemStack i = new ItemStack(getItemType());
		ItemMeta im = i.getItemMeta();
		im.setDisplayName(ChatColor.GREEN + getName());
		i.setItemMeta(im);
		return i;
	}
	
	public double getPrice() {
		return 0;
	}
	
	public boolean has(BPlayer p) {
		return getPrice() == 0 || p.getData().hasView(getId());
	}
	
	public abstract Effect getDefaultEffect();
	
	public Effect getEffect(BPlayer p) {
		if (p == null || !has(p)) return plugin.getGameManager().getEffectsManager().getEffectById(2);
		Effect e = getDefaultEffect();
		if (!e.has(p)) {
			e = plugin.getGameManager().getEffectsManager().getEffectById(2);
		}
		int def_player = p.getData().getEffectChoose(getId());
		if (def_player != -1) {
			Effect temp = plugin.getGameManager().getEffectsManager().getEffectById(def_player);
			if (temp.has(p)) {
				if (temp.canBeUse(getId())) {
					e = temp;
				}
			}
		}
		
		return e;
	}
	
	public List<Effect> getAllEffects() {
		List<Effect> effects = new ArrayList<Effect>();
		for (Effect e : plugin.getGameManager().getEffectsManager().getAllEffects()) {
			if (e.canBeUse(getId())) {
				effects.add(e);
			}
		}
		return effects;
	}
	
	public List<Effect> getAllEffects(BPlayer p) {
		List<Effect> effects = new ArrayList<Effect>();
		for (Effect e : plugin.getGameManager().getEffectsManager().getAllEffects()) {
			if (e.canBeUse(getId()) && e.has(p)) {
				effects.add(e);
			}
		}
		return effects;
	}
	
	public boolean canBuy(BPlayer p) {
		return true;
	}
		
	public boolean isBuyable() {
		return true;
	}

}
