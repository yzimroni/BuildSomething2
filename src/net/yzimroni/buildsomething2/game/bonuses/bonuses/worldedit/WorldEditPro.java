package net.yzimroni.buildsomething2.game.bonuses.bonuses.worldedit;

import java.util.HashMap;

import org.bukkit.Material;

import net.yzimroni.buildsomething2.BuildSomethingPlugin;
import net.yzimroni.buildsomething2.utils.MaterialData;

public class WorldEditPro extends WorldEditBonus {

	public WorldEditPro(BuildSomethingPlugin p) {
		super(p);
	}


	@Override
	public MaterialData getItemType() {
		return new MaterialData(Material.DIAMOND_AXE);
	}

	@Override
	public String getName() {
		return "WorldEdit Pro";
	}
	
	@Override
	public int getWorldEditBonusId() {
		return 2;
	}


	@Override
	public int getId() {
		return 8;
	}

	@Override
	public void init() {
		
	}

	@Override
	public double getPrice() {
		return 500;
	}
	
	// * 3. WorldEdit Pro: smooth, deform, hollow, copy, paste, rotate, flip, cut, 
	//pyramid, hpyramid, forestgen(?), pumpkins,

	@Override
	public HashMap<String, String> getCommands() {
		HashMap<String, String> commands = new HashMap<String, String>();
		
		commands.put("smooth", "worldedit.region.smooth");
		commands.put("deform", "worldedit.region.deform");
		commands.put("hollow", "worldedit.region.hollow");
		commands.put("copy", "worldedit.clipboard.copy");
		commands.put("flip", "worldedit.clipboard.flip");
		commands.put("rotate", "worldedit.clipboard.rotate");
		commands.put("paste", "worldedit.clipboard.paste");
		commands.put("cut", "worldedit.clipboard.cut");
		commands.put("pyramid", "worldedit.generation.pyramid");
		commands.put("hpyramid", "worldedit.generation.pyramid");
		commands.put("forestgen", "worldedit.generation.forest");
		commands.put("pumpkins", "worldedit.generation.pumpkins");		
		commands.put("brush", "worldedit.brush.*"); //TODO check this		
		commands.put("overlay", "worldedit.region.overlay"); 
		commands.put("center", "worldedit.region.center"); 
		commands.put("distr", "worldedit.analysis.distr"); 
		commands.put("deltree", "worldedit.tool.deltree"); 
		commands.put("snow", "worldedit.snow"); 
		return commands;
	}

}
