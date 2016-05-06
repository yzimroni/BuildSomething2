package net.yzimroni.buildsomething2.game.effects.effects;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import net.yzimroni.buildsomething2.BuildSomethingPlugin;
import net.yzimroni.buildsomething2.game.effects.effects.lib.ParticleEffect;

public class DripLava extends Effect {
	
	public DripLava(BuildSomethingPlugin p) {
		super(p);
	}

	@Override
	public void run(Location l, Player p) {
		//ParticleEffect.NAME.display(float offsetX, float offsetY, float offsetZ, float speed, int amount, Location center, double range);
		ParticleEffect.DRIP_LAVA.display(1, 1, 1, 0, 150, l, 300);
	}

	@Override
	public Material getItemType() {
		return Material.LAVA_BUCKET;
	}

	@Override
	public String getName() {
		return "Drip lava";
	}

	@Override
	public int getId() {
		return 6;
	}

	@Override
	public double getPrice() {
		return 200;
	}

}
