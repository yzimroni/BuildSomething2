package net.yzimroni.buildsomething2.game;

import java.util.List;

import org.bukkit.Location;

public class Map {
	private int id;
	private String name;
	private Location normal;
	private Location builder;
	private String wg_id;
	private boolean use;
	private boolean nightvision;
	private int ypaste;
	private int ybase;
	private List<Location> outlineBorderBlocks;
	
	
	public Map(int id, String name, Location normal, Location builder, String wg_id, boolean nightvision, int ypaste) {
		super();
		this.id = id;
		this.name = name;
		this.normal = normal;
		this.builder = builder;
		this.wg_id = wg_id;
		this.nightvision = nightvision;
		this.ypaste = ypaste;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public Location getNormal() {
		return normal;
	}
	
	public void setNormal(Location normal) {
		this.normal = normal;
	}
	
	public Location getBuilder() {
		return builder;
	}
	
	public void setBuilder(Location builder) {
		this.builder = builder;
	}
	
	public String getWg_id() {
		return wg_id;
	}
	
	public void setWg_id(String wg_id) {
		this.wg_id = wg_id;
	}

	/**
	 * @return the use
	 */
	public boolean isUse() {
		return use;
	}

	/**
	 * @param use the use to set
	 */
	public void setUse(boolean use) {
		this.use = use;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the nightvision
	 */
	public boolean isNightVision() {
		return nightvision;
	}

	/**
	 * @param nightvision the nightvision to set
	 */
	public void setNightVision(boolean nightvision) {
		this.nightvision = nightvision;
	}

	/**
	 * @return the ypaste
	 */
	public int getYPaste() {
		return ypaste;
	}

	/**
	 * @param ypaste the ypaste to set
	 */
	public void setYPaste(int ypaste) {
		this.ypaste = ypaste;
	}

	/**
	 * @return the outlineBorderBlocks
	 */
	public List<Location> getOutlineBorderBlocks() {
		return outlineBorderBlocks;
	}

	/**
	 * @param outlineBorderBlocks the outlineBorderBlocks to set
	 */
	public void setOutlineBorderBlocks(List<Location> outlineBorderBlocks) {
		this.outlineBorderBlocks = outlineBorderBlocks;
	}

	/**
	 * @return the ybase
	 */
	public int getYbase() {
		return ybase;
	}

	/**
	 * @param ybase the ybase to set
	 */
	public void setYbase(int ybase) {
		this.ybase = ybase;
	}
}
