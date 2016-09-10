package net.yzimroni.buildsomething2.game.bonuses.bonuses;

import net.yzimroni.buildsomething2.BuildSomethingPlugin;
import net.yzimroni.buildsomething2.game.bonuses.BonusUser;
import net.yzimroni.buildsomething2.game.games.BuildersGame;
import net.yzimroni.buildsomething2.game.games.Game;
import net.yzimroni.buildsomething2.player.BPlayer;
import net.yzimroni.buildsomething2.utils.MaterialData;
import net.yzimroni.buildsomething2.utils.Utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class Timebomb extends Bonus {

	public Timebomb(BuildSomethingPlugin p) {
		super(p);
	}

	@Override
	public MaterialData getItemType() {
		return new MaterialData(Material.CLAY_BALL);
	}
	
	@Override
	public String getName() {
		return "Timebomb";
	}

	@Override
	public int getId() {
		return 0;
	}

	@Override
	public BonusUser getUsers() {
		return BonusUser.BUILDER;
	}

	@Override
	public void onGameStart(Game g) {
		if (isBuildersGame(g)) {
			for (BPlayer b : ((BuildersGame) g).getBuilders().getBPlayers()) {
				if (has(b)) {
					b.getPlayer().getInventory().setItem(Utils.getFirstFreeInvSlot(b.getPlayer().getInventory()), getItem());
				}
			}
		}
	}
	
	@Override
	public void onGameStop(Game g) {
		for (BPlayer p : g.getPlayers()) {
			p.getPlayer().getInventory().remove(getItem());
		}
	}

	@Override
	public void onUse(Game g, BPlayer p) {
		if (canUse(g, p)) {
			if (isBuildersGame(g)){
				for (Player b : ((BuildersGame) g).getBuilders().getPlayers()) {
					b.getInventory().remove(getItem());
				}
				takeBonus(p);
				int sec = 90;
				g.setMetadata(metadata(), "true");
				g.addTime(sec);
				g.message(ChatColor.AQUA + "The builder " + ChatColor.GREEN + p.getPlayer().getName() + ChatColor.AQUA + " used " + ChatColor.GREEN + getName() + ChatColor.AQUA + " and added " + ChatColor.GREEN + Utils.timeString(sec) + ChatColor.AQUA + " to the game!");
			}
		}
	}

	@Override
	public void init() {
		
	}

	@Override
	public double getPrice() {
		return 20D;
	}
	
	@Override
	public boolean needBuildersGame() {
		return true;
	}

}
