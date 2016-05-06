package net.yzimroni.buildsomething2.game.effects.effects;

import net.yzimroni.buildsomething2.BuildSomethingPlugin;
import net.yzimroni.buildsomething2.game.effects.effects.lib.ParticleEffect;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class DripWater extends Effect {
	
	public DripWater(BuildSomethingPlugin p) {
		super(p);
	}

	@Override
	public void run(Location l, Player p) {
		//ParticleEffect.NAME.display(float offsetX, float offsetY, float offsetZ, float speed, int amount, Location center, double range);
		ParticleEffect.DRIP_WATER.display(1, 1, 1, 0, 50, l, 300);
	}

	@Override
	public Material getItemType() {
		return Material.WATER_BUCKET;
	}

	@Override
	public String getName() {
		return "Drip water";
	}

	@Override
	public int getId() {
		return 5;
	}

	@Override
	public double getPrice() {
		return 200;
	}
}
