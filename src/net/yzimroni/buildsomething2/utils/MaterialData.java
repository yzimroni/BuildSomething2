package net.yzimroni.buildsomething2.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class MaterialData {
	private Material type;
	private byte data;
	
	public MaterialData(Material type) {
		this.type = type;
		this.data = -1;
	}

	public MaterialData(Material type, byte data) {
		this.type = type;
		this.data = data;
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
	
	@SuppressWarnings("deprecation")
	public boolean isSame(ItemStack i) {
		if (i == null || i.getType() == null) {
			return false;
		}
		if (i.getType() == type) {
			if (data == -1) {
				return true;
			} else {
				if (data == i.getData().getData()) {
					return true;
				}
			}
		}
		return false;
	}
	
	public ItemStack toItemStack() {
		ItemStack i = null;
		if (data == -1) {
			i = new ItemStack(type);
		} else {
			i = new ItemStack(type, 1, data);
		}
		return i;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + data;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MaterialData other = (MaterialData) obj;
		if (data != other.data)
			return false;
		if (type != other.type)
			return false;
		return true;
	}
	
}
