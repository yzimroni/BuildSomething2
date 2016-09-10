package net.yzimroni.buildsomething2.game.blocks;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BSBlock implements HotbarItem {
	private int id;
	private Material type;
	private byte data;
	private String name;
	private double price;
	
	public BSBlock(int id, Material type, byte data, String name, double price) {
		this.id = id;
		this.type = type;
		this.data = data;
		this.name = name;
		this.price = price;
	}

	public ItemStack toItemStack() {
		ItemStack i = null;
		if (data == 0) {
			i = new ItemStack(type);
		} else {
			@SuppressWarnings("deprecation")
			ItemStack temp = new ItemStack(type, 1, (short) 0, data);
			i = temp; //Walk-around for the deprecated warning
		}
		if (hasName()) {
			ItemMeta im = i.getItemMeta();
			im.setDisplayName(ChatColor.RESET + getName());
			i.setItemMeta(im);
		}
		return i;
	}

	public Material getType() {
		return type;
	}

	public void setType(Material type) {
		this.type = type;
	}

	public byte getData() {
		return data;
	}

	public void setData(byte data) {
		this.data = data;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	public boolean hasName() {
		return false;
		//return getName() != null && !getName().isEmpty();
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getTypeName() {
		return "Block";
	}
	
}
