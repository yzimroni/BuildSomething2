package net.yzimroni.buildsomething2.game.effects.effects;

import net.yzimroni.buildsomething2.BuildSomethingPlugin;
import net.yzimroni.buildsomething2.game.effects.effects.lib.ParticleEffect;
import net.yzimroni.buildsomething2.utils.IntWarpper;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class Helix extends Effect {
	
	public Helix(BuildSomethingPlugin p) {
		super(p);
	}

	@Override
	public void run(final Location l, Player p) {
		long delay = 5;
		final IntWarpper i = new IntWarpper();
		final IntWarpper times = new IntWarpper();
		i.setValue(Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				if (times.getValue() > 10) {
					Bukkit.getScheduler().cancelTask(i.getValue());
				}
				times.setValue(times.getValue() + 1);
				createHelix(l);
			}
		}, delay, delay));
		//ParticleEffect.NAME.display(float offsetX, float offsetY, float offsetZ, float speed, int amount, Location center, double range);
		//ParticleEffect.HEART.display(5,5, 5, 3, 20, l, 3);
	}
	
	private void createHelix(Location loc) {
	   // Location loc = l;
	    int radius = 2;
	    for(double y = 0; y <= 15; y+=0.05) {
	        double x = radius * Math.cos(y);
	        double z = radius * Math.sin(y);
	        Location s = new Location(loc.getWorld(), (float) (loc.getX() + x), (float) (loc.getY() + y), (float) (loc.getZ() + z));
	        ParticleEffect.FIREWORKS_SPARK.display(0, 0, 0, 0, 1, s, 300);
	    }
	}

	@Override
	public Material getItemType() {
		return Material.NETHER_STAR;
	}

	@Override
	public String getName() {
		return "Helix";
	}

	@Override
	public int getId() {
		return 9;
	}

	@Override
	public double getPrice() {
		return 300;
	}
	
	@Override
	public int getMultiTimesRun() {
		return 1;
	}

}
