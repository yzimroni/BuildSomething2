package net.yzimroni.buildsomething2.game.effects.views;

import org.bukkit.Material;

import net.yzimroni.buildsomething2.BuildSomethingPlugin;
import net.yzimroni.buildsomething2.game.effects.effects.Effect;

public class EndGameBuilder extends EffectView {

	public EndGameBuilder(BuildSomethingPlugin p) {
		super(p);
	}

	@Override
	public String getName() {
		return "End Game (Builder)";
	}

	@Override
	public int getId() {
		return 1;
	}

	@Override
	public Material getItemType() {
		return Material.TNT;
	}

	@Override
	public Effect getDefaultEffect() {
		return plugin.getGameManager().getEffectsManager().getEffectById(0);
	}

}
