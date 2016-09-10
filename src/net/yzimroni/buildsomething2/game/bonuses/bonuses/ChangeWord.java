package net.yzimroni.buildsomething2.game.bonuses.bonuses;

import net.yzimroni.buildsomething2.BuildSomethingPlugin;
import net.yzimroni.buildsomething2.game.Gamemode;
import net.yzimroni.buildsomething2.game.bonuses.BonusUser;
import net.yzimroni.buildsomething2.game.games.BuildersGame;
import net.yzimroni.buildsomething2.game.games.Game;
import net.yzimroni.buildsomething2.player.BPlayer;
import net.yzimroni.buildsomething2.utils.MaterialData;
import net.yzimroni.buildsomething2.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ChangeWord extends Bonus {

	public ChangeWord(BuildSomethingPlugin p) {
		super(p);
	}

	@Override
	public MaterialData getItemType() {
		return new MaterialData(Material.SULPHUR);
	}
	
	@Override
	public String getName() {
		return "Change Word";
	}

	@Override
	public int getId() {
		return 5;
	}

	@Override
	public BonusUser getUsers() {
		return BonusUser.BUILDER;
	}

	@Override
	public void onGameStart(final Game g) {
		if (isBuildersGame(g)) {
			final BuildersGame bs = (BuildersGame) g;
			for (BPlayer b : bs.getBuilders().getBPlayers()) {
				b.getPlayer().getInventory().setItem(Utils.getFirstFreeInvSlot(b.getPlayer().getInventory()), getItem());
			}
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				
				@Override
				public void run() {
					if (bs == null || bs.getBuilders() == null || bs.getBuilders().isEmpty() || bs.getMode() != Gamemode.RUNNING) {
						return;
					}
					for (Player p : bs.getBuilders().getPlayers()) {
						p.getInventory().remove(getItem());
						bs.setMetadata(metadata() + "_over", "true");
					}
					
				}
			}, 30 * 20);
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
		if (isBuildersGame(g)) {
			final BuildersGame bs = (BuildersGame) g;
			if (canUse(g, p)) {
				for (Player b : bs.getBuilders().getPlayers()) {
					b.getInventory().remove(getItem());
					bs.setMetadata(metadata() + "_over", "true");
				}
				bs.changeWord();
				g.message(ChatColor.AQUA + "The builder " + ChatColor.GREEN + p.getPlayer().getName() + ChatColor.AQUA + " changed the word!");
				bs.sendWordInfo();
			} else {
				sendCantUseMessage(p.getPlayer());
			}
		}
	}
	
	@Override
	public boolean canUse(Game g, BPlayer p) {
		return !g.hasMetadata(metadata()) && isBuildersGame(g) && getUsers().canUse(((BuildersGame) g).getBuilders().isBuilder(p)) && !g.hasMetadata(metadata() + "_over") && g.getKnows().isEmpty();
	}

	@Override
	public void init() {
		
	}

	@Override
	public double getPrice() {
		return 0D;
	}
	
	@Override
	public int getMaxAmount() {
		return 0;
	}
	
	@Override
	public boolean canBuy(BPlayer p) {
		return false;
	}
	
	@Override
	public boolean has(BPlayer p) {
		return true;
	}
	
	@Override
	public boolean isBuyable() {
		return false;
	}

	@Override
	public boolean needBuildersGame() {
		return true;
	}

}
