package net.yzimroni.buildsomething2.game.blocks;

import org.bukkit.inventory.ItemStack;

public interface HotbarItem {

	public ItemStack toItemStack();
	
	public int getId();
	
	public String getTypeName();
	
}
