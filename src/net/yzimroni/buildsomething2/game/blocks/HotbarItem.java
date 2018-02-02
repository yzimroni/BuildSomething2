package net.yzimroni.buildsomething2.game.blocks;

import org.bukkit.inventory.ItemStack;

public interface HotbarItem {

	ItemStack toItemStack();
	
	int getId();
	
	String getTypeName();
	
}
