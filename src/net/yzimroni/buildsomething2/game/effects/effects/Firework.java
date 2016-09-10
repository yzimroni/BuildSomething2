package net.yzimroni.buildsomething2.game.effects.effects;

import java.util.Arrays;
import java.util.List;

import net.yzimroni.buildsomething2.BuildSomethingPlugin;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

public class Firework extends Effect {
	public Firework(BuildSomethingPlugin p) {
		super(p);
	}

	@Override
	public void run(Location l, Player p) {
		FireWork(l.getWorld(), l, Arrays.asList(Color.AQUA), (int) 0.5, Type.BALL);
	}
	
	private org.bukkit.entity.Firework FireWork(World w, Location l, List<Color> colors, int power, Type type) {
		org.bukkit.entity.Firework fw = (org.bukkit.entity.Firework) w.spawnEntity(l, EntityType.FIREWORK);

		FireworkMeta fwm = fw.getFireworkMeta();
		fwm.setPower(power);
		Builder effect1 = FireworkEffect.builder();
		effect1.with(type);
		if (colors.size() != 0) {
			for (Color c : colors) {
				effect1.withColor(c);
			}
		}

		effect1.withFlicker();
		fwm.addEffect(effect1.build());

		fw.setFireworkMeta(fwm);
		return fw;

	}

	@Override
	public Material getItemType() {
		return Material.FIREWORK;
	}

	@Override
	public String getName() {
		return "Firework";
	}

	@Override
	public int getId() {
		return 1;
	}

	@Override
	public double getPrice() {
		return 150;
	}
	
}
