// wand, pos, set, replace, redo, undo, desel

package net.yzimroni.buildsomething2.game.bonuses.bonuses.worldedit;

import java.util.HashMap;

import org.bukkit.Material;

import net.yzimroni.buildsomething2.BuildSomethingPlugin;
import net.yzimroni.buildsomething2.utils.MaterialData;

public class WorldEditBasic extends WorldEditBonus {
	

	public WorldEditBasic(BuildSomethingPlugin p) {
		super(p);
	}


	@Override
	public MaterialData getItemType() {
		return new MaterialData(Material.WOOD_AXE);
	}

	@Override
	public String getName() {
		return "WorldEdit";
	}
	
	@Override
	public int getWorldEditBonusId() {
		return 0;
	}

	@Override
	public int getId() {
		return 6;
	}

	@Override
	public void init() {
		
	}

	@Override
	public double getPrice() {
		return 300;
	}
	
	@Override
	public HashMap<String, String> getCommands() {
		HashMap<String, String> commands = new HashMap<String, String>();
		commands.put("wand", "worldedit.wand");
		commands.put("pos1", "worldedit.selection.pos");
		commands.put("pos2", "worldedit.selection.pos");
		commands.put("set", "worldedit.region.set");
		commands.put("replace", "worldedit.region.replace");
		commands.put("undo", "worldedit.history.undo");
		commands.put("redo", "worldedit.history.redo");
		
		return commands;
	}

}
