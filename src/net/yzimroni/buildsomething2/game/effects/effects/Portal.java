package net.yzimroni.buildsomething2.game.effects.effects;

import net.yzimroni.buildsomething2.BuildSomethingPlugin;
import net.yzimroni.buildsomething2.game.effects.effects.lib.ParticleEffect;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class Portal extends Effect {
	
	public Portal(BuildSomethingPlugin p) {
		super(p);
	}

	@Override
	public void run(Location l, Player p) {
		//ParticleEffect.NAME.display(float offsetX, float offsetY, float offsetZ, float speed, int amount, Location center, double range);
		ParticleEffect.PORTAL.display(1, 1, 1, 3, 300, l, 300);
	}

	@Override
	public Material getItemType() {
		return Material.PORTAL;
	}

	@Override
	public String getName() {
		return "Portal";
	}

	@Override
	public int getId() {
		return 7;
	}

	@Override
	public double getPrice() {
		return 250;
	}

}
