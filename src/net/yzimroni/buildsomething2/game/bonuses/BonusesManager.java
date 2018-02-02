package net.yzimroni.buildsomething2.game.bonuses;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

import net.yzimroni.buildsomething2.BuildSomethingPlugin;
import net.yzimroni.buildsomething2.game.bonuses.bonuses.Bonus;
import net.yzimroni.buildsomething2.game.bonuses.bonuses.ChangeWord;
import net.yzimroni.buildsomething2.game.bonuses.bonuses.NumberOfLetters;
import net.yzimroni.buildsomething2.game.bonuses.bonuses.RandomLetters;
import net.yzimroni.buildsomething2.game.bonuses.bonuses.Timebomb;
import net.yzimroni.buildsomething2.game.bonuses.bonuses.worldedit.WorldEditManager;
import net.yzimroni.buildsomething2.player.BPlayer;

public class BonusesManager implements Listener {
	public BuildSomethingPlugin plugin;
	private List<Bonus> bonuses;
	private WorldEditManager worldeditmanager;
	
	public BonusesManager(BuildSomethingPlugin p) {
		plugin = p;
		bonuses = new ArrayList<Bonus>();
		Bukkit.getPluginManager().registerEvents(this, plugin);
		worldeditmanager = new WorldEditManager(this, plugin);
		initBonuses();
		worldeditmanager.createWeCommands();
	}
	
	private void initBonuses() {
		addBonus(new Timebomb(plugin));
		addBonus(new ChangeWord(plugin));
		addBonus(new NumberOfLetters(plugin));
		addBonus(new RandomLetters(plugin));
		
		worldeditmanager.initBonuses();
	}
	
	public List<Bonus> getBonuses() {
		return bonuses;
	}
	
	public void addBonus(Bonus b) {
		if (getById(b.getId()) == null) {
			bonuses.add(b);
			b.init();
		} else {
			plugin.log.warning("TRY TO addBonuse(" + b + ") for already existing bouns!");
		}
	}
	
	public Bonus getByItem(ItemStack i) {
		for (Bonus b : getBonuses()) {
			if (b.getItem().isSimilar(i)) {
				return b;
			}
		}
		return null;
	}
	
	public Bonus getById(int id) {
		for (Bonus b : getBonuses()) {
			if (b.getId() == id) {
				return b;
			}
		}
		return null;
	}
	
	@EventHandler
	public void onPlayerInteractShop(PlayerInteractEntityEvent e) {
		if (e.getRightClicked() instanceof ItemFrame) {
			ItemFrame a = (ItemFrame)e.getRightClicked();
			ItemStack s = a.getItem();
			if (s == null || s.getType() == null || s.getType() == Material.AIR) {
				return;
			}
			Bonus b = getByItem(s);
			if (b != null) {
				a.setRotation(Rotation.COUNTER_CLOCKWISE_45);
				buyBonus(plugin.getPlayerManager().getPlayer(e.getPlayer().getUniqueId()), b);
			}
		}
	}
	
	private boolean buyBonus(BPlayer p, Bonus b) {
		if (!b.isBuyable()) {
			return false;
		}
		if (b.getPrice() == 0) {
			p.getPlayer().sendMessage("This Bonus is free!");
			return false;
		}
		
		if (!b.canBuy(p)) {
			p.getPlayer().sendMessage("You can't buy this bonus");
			return false;
		}
		
		if (b.getMaxAmount() != -1 && p.getData().getBonus(b.getId()) >= b.getMaxAmount()) {
			p.getPlayer().sendMessage("You already have this bonus!");
			return false;
		}
		
		if (plugin.getPlayerManager().getEconomy().has(p.getUUID(), b.getPrice()) && plugin.getPlayerManager().getEconomy().withdrawPlayer(p.getUUID(), b.getPrice())) {
			p.getData().addBonus(b.getId());
			p.getPlayer().sendMessage("You bought " + b.getName() + "!");
			return true;
		} else {
			p.getPlayer().sendMessage("You don't have enough money");
			return false;
		}
	}
	
	/**
	 * @return the worldeditmanager
	 */
	public WorldEditManager getWorldeditManager() {
		return worldeditmanager;
	}

	/**
	 * @param worldeditmanager the worldeditmanager to set
	 */
	public void setWorldeditManager(WorldEditManager worldeditmanager) {
		this.worldeditmanager = worldeditmanager;
	}
}