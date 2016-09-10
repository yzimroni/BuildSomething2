package net.yzimroni.buildsomething2.game.bonuses.bonuses.worldedit;

import java.util.ArrayList;
import java.util.List;
import net.yzimroni.buildsomething2.BuildSomethingPlugin;
import net.yzimroni.buildsomething2.game.blocks.BSBlock;
import net.yzimroni.buildsomething2.game.bonuses.BonusesManager;
import net.yzimroni.buildsomething2.game.bonuses.bonuses.Bonus;
import net.yzimroni.buildsomething2.game.games.Game;
import net.yzimroni.buildsomething2.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.LocalConfiguration;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.mask.RegionMask;
import com.sk89q.worldedit.regions.CuboidRegion;

public class WorldEditManager implements Listener {

	private BuildSomethingPlugin plugin;
	private BonusesManager manager;
	private List<String> we_commands = new ArrayList<String>();
	private WorldEditPlugin worldEdit = null;
	
	public WorldEditManager(BonusesManager m, BuildSomethingPlugin p) {
		if (!initWorldEdit()) {
			System.out.println("Error while loading WorldEditManager, WorldEdit not found");
		}
		plugin = p;
		manager = m;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	private boolean initWorldEdit() {
	    Plugin plugin = Bukkit.getPluginManager().getPlugin("WorldEdit");
	 
	    if (plugin == null || !(plugin instanceof WorldEditPlugin)) {
	        return false;
	    }
	 
	    worldEdit = (WorldEditPlugin) plugin;
	    return true;
	}
	
	public void initBonuses() {
		manager.addBonus(new WorldEditBasic(plugin));
		manager.addBonus(new WorldEditAdvanced(plugin));
		manager.addBonus(new WorldEditPro(plugin));
	}
	
	public void createWeCommands() {
		we_commands.clear();
		for (Bonus b : manager.getBonuses()) {
			if (b instanceof WorldEditBonus) {
				addCommands((WorldEditBonus) b);
			}
		}
	}
	
	private void addCommands(WorldEditBonus w) {
		for (String cmd : w.getCommands().keySet()) {
			we_commands.add(cmd.toLowerCase());
		}
	}
	
	private LocalSession getLocalSession(Player p){
		LocalSession session = WorldEdit.getInstance().getSessionManager().get(new BukkitPlayer(worldEdit, WorldEdit.getInstance().getServer(), p));
		return session;
	}
	
	public void initWorldEditPlayer(Player p, Game g, List<BSBlock> blocks) {
		LocalSession session = getLocalSession(p);
		Vector pos1 = g.getRegion().getMinimumPoint();
		Vector pos2 = g.getRegion().getMaximumPoint();
		
		CuboidRegion cr = new CuboidRegion((com.sk89q.worldedit.world.World) BukkitUtil.getLocalWorld(g.getMap().getBuilder().getWorld()), pos1, pos2);
				
		RegionMask rm = new RegionMask(cr);
		
		LocalConfiguration lc = newConfig();
		lc.allowedBlocks.clear();
		lc.onlyAllowedBlocks = true;
		
		for (BSBlock b : blocks) {
			lc.allowedBlocks.add(Utils.getId(b.getType()) + ":" + b.getData());
		}
		lc.allowedBlocks.add("0:0");
		session.setMask(rm);
		session.setConfiguration(lc);
	}
	
	private LocalConfiguration newConfig() {
		LocalConfiguration lc = new LocalConfiguration() {
			@Override
			public void load() {}
		};
		lc.allowedBlocks.clear();
		lc.maxChangeLimit = 1000;
		return lc;
	}
	
	public void removeMask(Player p) {
		LocalSession session = getLocalSession(p);
		if (!p.isOp()) session.clearHistory();
		Mask mask = null;
		session.setMask(mask);
		session.setConfiguration(newConfig());
	}
	
	@EventHandler
	public void onFilterCommand(PlayerCommandPreprocessEvent e) {
		String command = e.getMessage();
		command = command.toLowerCase();
		command = command.trim();
		command = command.split(" ")[0];
		command = command.replaceAll("/", "");
		if (command.contains(":")) {
			command = command.substring(command.indexOf(":") + 1);
		}
		command = command.trim();
		if (we_commands.contains(command.toLowerCase())) {
			if (!e.getPlayer().isOp() && !plugin.getGameManager().isBuilder(e.getPlayer())) {
				e.getPlayer().sendMessage(ChatColor.RED + "You are not permitted to do that, Are you in the right mode?");
				e.setCancelled(true);
				System.out.println(e.getPlayer().getName() + " use worldedit command when not a builder");
			}
		}
		
	}
	
}
