//http://wiki.sk89q.com/wiki/WorldEdit/Permissions

/**
 * WorldEdit bonuses:
 * getMaxAmount = 1
 * takeBonus do nothing
 * canBuy return true if the player have the previous bonus
 * isBuyable = true
 * 
 * Bonuses:
 * 1. WorldEdit: wand, pos, set, replace, redo, undo, desel
 * 2. WorlEdit Advanced: hpos, expand, contract, outset, shift, size, count, overlay, walls, outline, cyl, hcyl, sphere, hsphere
 * 3. WorldEdit Pro: smooth, deform, hollow, copy, paste, rotate, flip, cut, pyramid, hpyramid, forestgen(?), pumpkins,
 * 
 * When the game start every bonus called and adding all the needed permissions to the player, and each bonus add metadata to the game with the bonus id 
 * and the commands.
 * After all the bonuses called, a method in the game check if there is a builder that have worldedit permissions, is so, it get all the commands the player have,
 * and then it send in to the player with explntion and give a wand
 * 
 * When adding a new worldedit bonus:
 * 1. add like normal bonus
 * 2. set the number in Game:checkWorldEditCommands() X
 * 3. add the bonus in WorldEditManager:createWeCommands() X
 */

package net.yzimroni.buildsomething2.game.bonuses.bonuses.worldedit;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.entity.Player;

import net.yzimroni.buildsomething2.BuildSomethingPlugin;
import net.yzimroni.buildsomething2.game.bonuses.BonusUser;
import net.yzimroni.buildsomething2.game.bonuses.bonuses.Bonus;
import net.yzimroni.buildsomething2.game.games.BuildersGame;
import net.yzimroni.buildsomething2.game.games.Game;
import net.yzimroni.buildsomething2.player.BPlayer;

public abstract class WorldEditBonus extends Bonus {

	public WorldEditBonus(BuildSomethingPlugin p) {
		super(p);
	}
	
	protected boolean isFirst() {
		return getWorldEditBonusId() == 0;
	}
	
	public abstract int getWorldEditBonusId();
	
	@Override
	public int getMaxAmount() {
		return 1;
	}
	
	@Override
	protected void takeBonus(BPlayer p) {
		
	}
	
	@Override
	public boolean canBuy(BPlayer p) {
		if (isFirst()) {
			return true;
		} else {
			int pd = getId() - 1;
			return plugin.getGameManager().getBonusesManager().getById(pd).getPrice() == 0 || p.getData().getBonus(pd) == 1;
		}
	}
	
	@Override
	public boolean isBuyable() {
		return true;
	}
	
	@Override
	public void onUse(Game g, BPlayer p) {
		
	}
	
	public abstract HashMap<String, String> getCommands();
	
	@Override
	public void onGameStart(Game g) {
		if (isBuildersGame(g)) {
			BuildersGame bs = (BuildersGame) g;
			for (BPlayer bp : bs.getBuilders().getBPlayers()) {
				if (has(bp)) {
					Player p = bp.getPlayer();
					for (Entry<String, String> o : getCommands().entrySet()) {
						givePermission(p, o.getValue());
					}
				}
			}
		}
	}
	
	private void givePermission(Player p, String perm) {
		p.addAttachment(plugin, perm, true);
		
	}
	
	private void removePermission(Player p, String perm) {
		p.addAttachment(plugin, perm, false);
		//TODO check THIS!
	}
	
	@Override
	public void onGameStop(Game g) {
		if (isBuildersGame(g)) {
			BuildersGame bs = (BuildersGame) g;
			for (BPlayer bp : bs.getBuilders().getBPlayers()) {
				if (has(bp)) {
					Player p = bp.getPlayer();
					for (Entry<String, String> o : getCommands().entrySet()) {
						removePermission(p, o.getValue());
					}
				}
			}
		}
	}
	
	@Override
	public void onPlayerQuit(Game g, BPlayer bp) {
		if (canUse(g, bp)) {
			if (has(bp)) {
				Player p = bp.getPlayer();
				for (Entry<String, String> o : getCommands().entrySet()) {
					removePermission(p, o.getValue());
				}
			}
		}
	}
	
	@Override
	public BonusUser getUsers() {
		return BonusUser.BUILDER;
	}

	@Override
	public boolean needBuildersGame() {
		return true;
	}
	
}
