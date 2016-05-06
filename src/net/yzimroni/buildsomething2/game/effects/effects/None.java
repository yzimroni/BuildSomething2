package net.yzimroni.buildsomething2.game.effects.effects;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import net.yzimroni.buildsomething2.BuildSomethingPlugin;

public class None extends Effect {

	public None(BuildSomethingPlugin p) {
		super(p);
	}

	@Override
	public void run(Location l, Player p) {
		
	}

	@Override
	public Material getItemType() {
		return Material.BARRIER;
	}

	@Override
	public String getName() {
		return "None";
	}

	@Override
	public int getId() {
		return 2;
	}

	@Override
	public double getPrice() {
		return 0;
	}
	
	@Override
	public int getMultiTimesRun() {
		return 1;
	}


}
