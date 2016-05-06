package net.yzimroni.buildsomething2.game.bonuses.bonuses;

import net.yzimroni.buildsomething2.BuildSomethingPlugin;
import net.yzimroni.buildsomething2.game.bonuses.BonusUser;
import net.yzimroni.buildsomething2.game.games.BuildersGame;
import net.yzimroni.buildsomething2.game.games.Game;
import net.yzimroni.buildsomething2.player.BPlayer;
import net.yzimroni.buildsomething2.utils.MaterialData;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class Bonus {
	
	protected BuildSomethingPlugin plugin;
	
	public Bonus(BuildSomethingPlugin p) {
		plugin = p;
	}
	
	public ItemStack getItem() {
		ItemStack i = getItemType().toItemStack();
		ItemMeta im = i.getItemMeta();
		im.setDisplayName(ChatColor.GREEN + getName());
		i.setItemMeta(im);
		return i;
	}
	
	public abstract boolean needBuildersGame();
	
	public abstract MaterialData getItemType();

	public abstract String getName();
	
	public abstract int getId();
	
	public abstract BonusUser getUsers();
	
	public abstract void onGameStart(Game g);
	
	public abstract void onGameStop(Game g);
	
	public void onPlayerQuit(Game g, BPlayer bp) {
		
	}
	
	public abstract void onUse(Game g, BPlayer p);
	
	public boolean canUse(Game g, BPlayer p) {
		return !g.hasMetadata(metadata()) && (isBuildersGame(g) ? true : getUsers() != BonusUser.BUILDER) && getUsers().canUse(isBuilder(g, p));
	}
	
	protected boolean isBuilder(Game g, BPlayer p) {
		if (isBuildersGame(g)) {
			return ((BuildersGame) g).getBuilders().isBuilder(p);
		}
		return false;
	}
	
	protected boolean isBuildersGame(Game g) {
		return g instanceof BuildersGame;
	}

	public abstract void init();

	public boolean has(BPlayer p) {
		return getPrice() == 0 || p.getData().hasBonus(getId());
	}
	
	public String metadata() {
		return getName() + "_" + "use";
	}
	
	public String metadata(String s) {
		return metadata() + "_" + s;
	}
	
	public String metadataRaw(String s) {
		return metadata() + "_" + s;
	}
	
	public int getMaxAmount() {
		return -1;
	}
	
	public abstract double getPrice();

	@Override
	public String toString() {
		return "Bonus [getItemType()=" + getItemType() + ", getName()="
				+ getName() + ", getId()=" + getId() + ", metadata()="
				+ metadata() + ", getPrice()=" + getPrice() + "]";
	}
	
	protected void takeBonus(BPlayer p) {
		if (getPrice() > 0) {
			p.getData().takeBonus(getId());
		}
	}
	
	public boolean canBuy(BPlayer p) {
		return true;
	}
	
	public void sendCantUseMessage(Player p) {
		p.getInventory().remove(getItem());
		p.sendMessage(ChatColor.RED + "You can't use that!");
	}
	
	public boolean isBuyable() {
		return true;
	}
}
