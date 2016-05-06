package net.yzimroni.buildsomething2.game.effects.effects;

import net.yzimroni.buildsomething2.BuildSomethingPlugin;
import net.yzimroni.buildsomething2.game.effects.effects.lib.ParticleEffect;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class Heart extends Effect {
	
	public Heart(BuildSomethingPlugin p) {
		super(p);
	}

	@Override
	public void run(Location l, Player p) {
		//ParticleEffect.NAME.display(float offsetX, float offsetY, float offsetZ, float speed, int amount, Location center, double range);
		ParticleEffect.HEART.display(1, 1, 1, 0, 20, l, 300);
	}

	@Override
	public Material getItemType() {
		return Material.REDSTONE;
	}

	@Override
	public String getName() {
		return "Heart";
	}

	@Override
	public int getId() {
		return 8;
	}

	@Override
	public double getPrice() {
		return 150;
	}

}
