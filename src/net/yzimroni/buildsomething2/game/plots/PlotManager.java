package net.yzimroni.buildsomething2.game.plots;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.worldcretornica.plotme_core.ClearReason;
import com.worldcretornica.plotme_core.Plot;
import com.worldcretornica.plotme_core.Plot.AccessLevel;
import com.worldcretornica.plotme_core.PlotMeCoreManager;
import com.worldcretornica.plotme_core.api.IWorld;

import net.citizensnpcs.api.npc.NPC;
import net.yzimroni.buildsomething2.BuildSomethingPlugin;
import net.yzimroni.buildsomething2.game.Builders;
import net.yzimroni.buildsomething2.game.games.Game;
import net.yzimroni.buildsomething2.utils.Utils;
import net.yzimroni.buildsomething2.utils.WorldEditClipboard;

public class PlotManager {

	private BuildSomethingPlugin plugin;
	private NPCManager npcmanager;

	public PlotManager(BuildSomethingPlugin p) {
		plugin = p;
		npcmanager = new NPCManager(plugin);
	}
		
	public PlotInfo createGameBuildPlot(Game g, Builders builders, WorldEditClipboard clipboard, CPlotType type) {
		try {
			if (g.getWord() == null) return null;
			World world = Bukkit.getWorld(type.getWorld());
			Plot plot = getFreePlot(builders.getPlayers().get(0), world.getName());
			
			PlotInfo info = new PlotInfo(plot.getId().toString());
			if (builders.size() > 1) {
				List<Player> players = builders.getPlayers();
				for (int i = 1; i < players.size(); i++) {
					plot.addMember(players.get(i).getName(), AccessLevel.ALLOWED);
				}
			}
			
			if (type == CPlotType.NORMAL) {
				com.worldcretornica.plotme_core.api.Vector lm = plot.getPlotTopLoc();
				Location l = new Location(world, lm.getX(), lm.getY(), lm.getZ());
				int step = 0;
				for (Player p : builders.getPlayers()) {
					Location npcl = new Location(l.getWorld(), l.getX(), l.getY(), l.getZ());
					npcl.subtract(2 * step, 0, 0);
					NPC c = npcmanager.createPlotBuildNPC(npcl, p, g.getWord());
					l.getChunk().load();
					info.addBuilder(p.getUniqueId(), c.getId());
					step++;
				}
			}
			pastePlot(plot, g.getMap().getYPaste(), clipboard);
			return info;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void pastePlot(Plot plot, int level, WorldEditClipboard clipboard) {
		//TODO switch to the new worldedit api
		World world = Bukkit.getWorld(plot.getWorld().getName());
		com.worldcretornica.plotme_core.api.Vector lm = plot.getPlotTopLoc();
		Location l = new Location(world, lm.getX(), lm.getY(), lm.getZ());

		l.setY(level);
		try {
			clipboard.getClipBoard().paste(clipboard.getEditSession(), BukkitUtil.toVector(new Location(world, l.getX(), l.getY(), l.getZ())), false);
		} catch (MaxChangedBlocksException e) {
			e.printStackTrace();
		}
	}
	
	private Plot getFreePlot(OfflinePlayer p, String worldname) {
		IWorld mp = PlotMeCoreManager.getInstance().getWorld(worldname);
		if (mp == null) {
			System.out.println("ERROR! mp is null! " + p + " " + p.getUniqueId() + " " + worldname);
			mp = PlotMeCoreManager.getInstance().getWorld("plotworld");
		}
		int limit = 25000;
		for (int i = 0; i < limit; i++) {
			for (int x = -i; x <= i; x++) {
				for (int z = -i; z <= i; z++) {
					String id = "" + x + ";" + z;
					if (PlotMeCoreManager.getInstance().isPlotAvailable(Utils.plotId(id), mp)) {
						Plot plot = PlotMeCoreManager.getInstance().createPlot(Utils.plotId(id), mp, p.getName(), p.getUniqueId(), PlotMeCoreManager.getInstance().getMap(mp));
						return plot;
					}
					
				}
			}
		}
		return null;
	}
	
	public boolean movePlotToBot(Plot from) {
		if (from != null) {
			if (from.getOwnerId() != null) {
				try {
					PreparedStatement gamePre = plugin.getDB().getPrepare("SELECT ID FROM games WHERE plot_type='0' AND plot_id=?");
					gamePre.setString(1, from.getId().toString());
					
					ResultSet rs = gamePre.executeQuery();
					int id = -1;
					if (rs.next()) {
						id = rs.getInt("ID");
					}
					gamePre.close();
					rs.close();
					if (id == -1) {
						System.out.println("the plot is not a game plot!");
						return false;
					}
					Plot to = getFreePlot(Bukkit.getOfflinePlayer(from.getOwnerId()), "botplot");
					if (to != null) {
						plugin.getCommandManager().addPlotId(id, null);
						movePlot(from, to);
						PlotMeCoreManager.getInstance().deletePlot(from);
						PreparedStatement updatePre = plugin.getDB().getPrepare("UPDATE games SET plot_type='1',request_bot='0',plot_id=? WHERE ID=?");
						updatePre.setString(1, to.getId().toString());
						updatePre.setInt(2, id);
						updatePre.executeUpdate();
						updatePre.close();
						
						PreparedStatement npcPre = plugin.getDB().getPrepare("SELECT npc_id FROM game_players WHERE game_id=? AND npc_id >= 0");
						npcPre.setInt(1, id);
						
						ResultSet npcs = npcPre.executeQuery();
						while (npcs.next()) {
							int npc_id = npcs.getInt("npc_id");
							npcmanager.removeNPC(npc_id);
							//TODO need to remove the npc id from the row as well?
						}
						npcs.close();
						npcPre.close();
						return true;
					} else {
						System.out.println("to is null");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				System.out.println("owner id is null");
			}
		} else {
			System.out.println("from is null");
		}
		return false;
	}
	
	@SuppressWarnings("deprecation")
	public boolean movePlot(Plot from, Plot to) {
		PlotMeCoreManager.getInstance().clear(to, null, ClearReason.Clear);
		
		World w = Bukkit.getWorld(from.getWorld().getName());
		Location l1 = new Location(w, from.getPlotBottomLoc().getBlockX(), 65, from.getPlotBottomLoc().getBlockZ());
		Location l2 = new Location(w, from.getPlotTopLoc().getBlockX(), 100, from.getPlotTopLoc().getBlockZ());
		
		try {
			EditSession es = new EditSession(BukkitUtil.getLocalWorld(w), Integer.MAX_VALUE);
			Vector bvmin = BukkitUtil.toVector(l1);
			Vector bvmax = BukkitUtil.toVector(l2);
			Vector pos = bvmax;
			CuboidClipboard clipboard = new CuboidClipboard(bvmax.subtract(bvmin).add(new Vector(1, 1, 1)),bvmin, bvmin.subtract(pos));
			clipboard.copy(es);
			EditSession em = new EditSession(BukkitUtil.getLocalWorld(Bukkit.getWorld("plotworld")),Integer.MAX_VALUE);
			com.worldcretornica.plotme_core.api.Vector lm = to.getPlotTopLoc();
			Location l = new Location(Bukkit.getWorld(to.getWorld().getName()), lm.getX(), lm.getY(), lm.getZ());
			l.setY(100); //TODO check this
			clipboard.paste(em, BukkitUtil.toVector(new Location(l.getWorld(), l.getX(), l.getY(), l.getZ())), false);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
	public String getPlotId(int id) {
		try {
			PreparedStatement pre = plugin.getDB()
					.getPrepare("SELECT plot_id FROM games WHERE plot_type='0' AND ID=?");
			pre.setInt(1, id);

			ResultSet rs = pre.executeQuery();
			if (rs.next()) {
				return rs.getString("plot_id");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * @return the npcmanager
	 */
	public NPCManager getNpcManager() {
		return npcmanager;
	}
	
	public enum CPlotType{
		NORMAL("plotworld"), BOT("botplot"), REPORT("reportplot");
		
		private String world;
		
		CPlotType(String world) {
			this.world = world;
		}
		
		public String getWorld() {
			return this.world;
		}
	}
}
