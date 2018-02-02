package net.yzimroni.buildsomething2.game.effects.views;

import org.bukkit.Material;

import net.yzimroni.buildsomething2.BuildSomethingPlugin;
import net.yzimroni.buildsomething2.game.effects.effects.Effect;

public class KnowWord extends EffectView {

	public KnowWord(BuildSomethingPlugin p) {
		super(p);
	}

	@Override
	public String getName() {
		return "Know the word";
	}

	@Override
	public int getId() {
		return 0;
	}

	@Override
	public Material getItemType() {
		return Material.BOOK;
	}

	@Override
	public Effect getDefaultEffect() {
		return plugin.getGameManager().getEffectsManager().getEffectById(1);
	}

}
