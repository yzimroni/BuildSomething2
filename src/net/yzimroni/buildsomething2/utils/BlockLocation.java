package net.yzimroni.buildsomething2.utils;

import org.bukkit.Material;

public class BlockLocation {

	private int offsetX;
	private int offsetY;
	private int offsetZ;
	
	private Material type;
	private byte data;
	
	public BlockLocation(int offsetX, int offsetY, int offsetZ) {
		this(offsetX, offsetY, offsetZ, null);
	}
	
	public BlockLocation(int offsetX, int offsetY, int offsetZ, Material type) {
		this(offsetX, offsetY, offsetZ, type, (byte) 0);
	}
	
	public BlockLocation(int offsetX, int offsetY, int offsetZ, Material type, byte data) {
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.offsetZ = offsetZ;
		this.type = type;
		this.data = data;
	}
	public int getOffsetX() {
		return offsetX;
	}
	public void setOffsetX(int offsetX) {
		this.offsetX = offsetX;
	}
	public int getOffsetY() {
		return offsetY;
	}
	public void setOffsetY(int offsetY) {
		this.offsetY = offsetY;
	}
	public int getOffsetZ() {
		return offsetZ;
	}
	public void setOffsetZ(int offsetZ) {
		this.offsetZ = offsetZ;
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
	@Override
	public String toString() {
		return "BlockLocation [offsetX=" + offsetX + ", offsetY=" + offsetY
				+ ", offsetZ=" + offsetZ + ", type=" + type + ", data=" + data
				+ "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + offsetX;
		result = prime * result + offsetY;
		result = prime * result + offsetZ;
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
		BlockLocation other = (BlockLocation) obj;
		if (offsetX != other.offsetX)
			return false;
		if (offsetY != other.offsetY)
			return false;
		if (offsetZ != other.offsetZ)
			return false;
		return true;
	}
	
	
	
}
