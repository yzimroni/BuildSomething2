package net.yzimroni.buildsomething2.game.effects.effects;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import net.yzimroni.buildsomething2.BuildSomethingPlugin;
import net.yzimroni.buildsomething2.game.effects.effects.lib.ParticleEffect;

public class Flame extends Effect {

	public Flame(BuildSomethingPlugin p) {
		super(p);
	}

	@Override
	public void run(Location l, Player p) {
		//ParticleEffect.NAME.display(float offsetX, float offsetY, float offsetZ, float speed, int amount, Location center, double range);
		//ParticleEffect.FLAME.display(1, 1, 1, 0, 50, l, 3);
		for (int i = 0; i<5; i++) {
			//ParticleEffect.FLAME.display(1, 1, 1, i, 50, l);
			ParticleEffect.FLAME.display(1, 1, 1, i, 50, l, 300);
		}
	}

	@Override
	public Material getItemType() {
		return Material.FIRE;
	}

	@Override
	public String getName() {
		return "Flame";
	}

	@Override
	public int getId() {
		return 3;
	}

	@Override
	public double getPrice() {
		return 150;
	}

}
