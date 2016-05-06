package net.yzimroni.buildsomething2.game.effects.effects;


import net.yzimroni.buildsomething2.BuildSomethingPlugin;
import net.yzimroni.buildsomething2.game.effects.effects.lib.ParticleEffect;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class HappyVillager extends Effect {
	public HappyVillager(BuildSomethingPlugin p) {
		super(p);
	}

	@Override
	public void run(Location l, Player p) {
		//ParticleEffect.NAME.display(float offsetX, float offsetY, float offsetZ, float speed, int amount, Location center, double range);
		//ParticleEffect.VILLAGER_HAPPY.display(5 ,5, 5, 3, 20, l, 3);
		ParticleEffect.VILLAGER_HAPPY.display(1, 1, 1, 0, 100, l, 300);
	}

	@Override
	public Material getItemType() {
		return Material.GREEN_RECORD;
	}

	@Override
	public String getName() {
		return "Happy Villager";
	}

	@Override
	public int getId() {
		return 4;
	}

	@Override
	public double getPrice() {
		return 150;
	}


}
