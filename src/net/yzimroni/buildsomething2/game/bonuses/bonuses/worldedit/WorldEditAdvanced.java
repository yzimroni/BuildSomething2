// * 2. WorlEdit Advanced: hpos, expand, contract, size, count, walls, 
//outline, cyl, hcyl, sphere, hsphere

package net.yzimroni.buildsomething2.game.bonuses.bonuses.worldedit;

import java.util.HashMap;

import org.bukkit.Material;

import net.yzimroni.buildsomething2.BuildSomethingPlugin;
import net.yzimroni.buildsomething2.utils.MaterialData;

public class WorldEditAdvanced extends WorldEditBonus {
	
	public WorldEditAdvanced(BuildSomethingPlugin p) {
		super(p);
	}
	

	@Override
	public int getWorldEditBonusId() {
		return 1;
	}

	@Override
	public MaterialData getItemType() {
		return new MaterialData(Material.GOLD_AXE);
	}

	@Override
	public String getName() {
		return "WorlEdit Advanced";
	}

	@Override
	public int getId() {
		return 7;
	}

	@Override
	public void init() {
		
	}

	@Override
	public double getPrice() {
		return 500;
	}
	
	@Override
	public HashMap<String, String> getCommands() {
		HashMap<String, String> commands = new HashMap<String, String>();
		commands.put("hpos1", "worldedit.selection.hpos");
		commands.put("hpos2", "worldedit.selection.hpos");
		commands.put("expand", "worldedit.selection.expand");
		commands.put("contract", "worldedit.selection.contract");
		commands.put("size", "worldedit.selection.size");
		commands.put("count", "worldedit.analysis.count");
		commands.put("walls", "worldedit.region.walls");
		commands.put("outline", "worldedit.region.faces");
		commands.put("hcyl", "worldedit.generation.cylinder");
		commands.put("cyl", "worldedit.generation.cylinder");
		commands.put("hsphere", "worldedit.generation.sphere");
		commands.put("sphere", "worldedit.generation.sphere");
		commands.put("tool", "worldedit.tool.*");

		return commands;
	}

}
