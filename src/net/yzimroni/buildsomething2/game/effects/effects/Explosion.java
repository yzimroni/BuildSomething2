package net.yzimroni.buildsomething2.game.effects.effects;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import net.yzimroni.buildsomething2.BuildSomethingPlugin;

public class Explosion extends Effect {

	public Explosion(BuildSomethingPlugin p) {
		super(p);
	}

	@Override
	public void run(Location l, Player p) {
		l.getWorld().createExplosion(l.getX(), l.getY(), l.getZ(), 4, false, false);
	}

	@Override
	public Material getItemType() {
		return Material.TNT;
	}

	@Override
	public String getName() {
		return "Explosion";
	}

	@Override
	public int getId() {
		return 0;
	}

	@Override
	public double getPrice() {
		return 100;
	}


}
