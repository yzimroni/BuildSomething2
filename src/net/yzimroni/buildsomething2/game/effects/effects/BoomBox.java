package net.yzimroni.buildsomething2.game.effects.effects;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.yzimroni.buildsomething2.BuildSomethingPlugin;
import net.yzimroni.buildsomething2.utils.IntWarpper;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class BoomBox extends Effect {
	
	private static Material[] MATERIALS = new Material[]{Material.DIAMOND_SWORD, Material.GOLD_SWORD, Material.IRON_SWORD, Material.STONE_SWORD, Material.WOOD_SWORD, Material.DIAMOND, Material.GRASS};
	
	public BoomBox(BuildSomethingPlugin p) {
		super(p);
	}

	@Override
	public void run(final Location ll, final Player p) {
		final LocationWrapper l = new LocationWrapper();
		l.l = ll;
		if (p != null && p.isOnline()) {
			l.l = p.getEyeLocation();
		}
		final Item i = l.l.getWorld().dropItem(l.l, new ItemStack(Material.TNT));
		i.setPickupDelay(Integer.MAX_VALUE);
		i.setVelocity(new Vector(0, 0, 0));
		i.setVelocity(new Vector(0, 1, 0));
		final List<Item> items = new ArrayList<Item>();
		final IntWarpper task = new IntWarpper();
		final IntWarpper times = new IntWarpper();
		task.setValue(Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				if (p != null && p.isOnline()) {
					l.l = p.getEyeLocation();
				}
				if (times.getValue() > 100) {
					Bukkit.getScheduler().cancelTask(task.getValue());
					return;
				}
				times.setValue(times.getValue() + 1);
				if (!i.isDead()) {
					i.remove();
				}
				for (int i = 0; i < 10; i++) {
					Item m = l.l.getWorld().dropItem(l.l, new ItemStack(randomMaterial()));
					m.setPickupDelay(Integer.MAX_VALUE);
					m.setVelocity(new Vector(randomDouble(), 0.5, randomDouble()));
					items.add(m);
				}
				
			}
			
		}, 40L, 1L));
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				for (Item item : items) {
					item.remove();
				}
			}
		}, 40 + 120);
	}
	
	private double randomDouble() {
		Random r = new Random();
		double i = r.nextInt(7)-3;
		if (i == 0) {
			i = 1;
		}
		i += Double.valueOf("0." + r.nextInt(100));
		return i;
	}
	
	private Material randomMaterial() {
		return MATERIALS[new Random().nextInt(MATERIALS.length)];
	}

	@Override
	public Material getItemType() {
		return Material.IRON_PICKAXE;
	}

	@Override
	public String getName() {
		return "Boom Box";
	}

	@Override
	public int getId() {
		return 10;
	}

	@Override
	public double getPrice() {
		return 500;
	}

}
class LocationWrapper{
	Location l;
}