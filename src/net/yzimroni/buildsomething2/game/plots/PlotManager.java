package net.yzimroni.buildsomething2.game.plots;

import java.sql.ResultSet;
import java.util.List;

import net.citizensnpcs.api.npc.NPC;
import net.yzimroni.buildsomething2.BuildSomethingPlugin;
import net.yzimroni.buildsomething2.game.Builders;
import net.yzimroni.buildsomething2.game.games.Game;
import net.yzimroni.buildsomething2.utils.Utils;
import net.yzimroni.buildsomething2.utils.WorldEditClipboard;

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
			//return id;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void pastePlot(Plot plot, int level, WorldEditClipboard clipboard) {
		//TODO try to work with the new worldedit api
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
					ResultSet rs = plugin.getDB().get("SELECT ID FROM games WHERE plot_type='0' AND plot_id='" + from.getId() + "'");
					int id = -1;
					if (rs.next()) {
						id = rs.getInt("ID");
					}
					if (id == -1) {
						System.out.println("the plot is not a game plot!");
						return false;
					}
					Plot to = getFreePlot(Bukkit.getOfflinePlayer(from.getOwnerId()), "botplot");
					if (to != null) {
						plugin.getCommandManager().addPlotId(id, null);
						movePlot(from, to);
						PlotMeCoreManager.getInstance().deletePlot(from);
						plugin.getDB().set("UPDATE games SET plot_type='1',bot_request='0',plot_id='" + to.getId() + "' WHERE ID='" + id + "'");
						ResultSet npcs = plugin.getDB().get("SELECT npc_id FROM game_players WHERE game_id='" + id + "' AND npc_id >= 0");
						while (npcs.next()) {
							int npc_id = npcs.getInt("npc_id");
							npcmanager.removeNPC(npc_id);
						}
						return true;
						/*
						 * TO DO
						 * update the plot id, the plot type and the bot request in the game db V
						 * Get the game db id first V
						 * Remove the npcs V
						 */
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

		
		/*
		 * TO DO 
		 * remove the content of "to" plot V
		 * copy the plot from to V
		 */
		
		return true;
	}
	
	public String getPlotId(int id) {
		try {
			 ResultSet rs = plugin.getDB().get("SELECT plot_id FROM games WHERE plot_type='0' AND ID='" + id + "'");
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
	
	/*private int sendPlotInfo(Builders builders, Game g, Plot plot, List<NPC> c) {
		try {
			/**
			 * TO DO:
			 * tables: plots (without UUID and npc_id), playerplots (with UUID, plots_id, npc_id)
			 * 
			 
			PreparedStatement ps = plugin.getDB().getPrepare("INSERT INTO plots (plot_id,word_id,map_id,date) VALUES(?,?,?,?)");
			//ps.setString(1, p.getUniqueId().toString());
			ps.setString(1, plot.getId().toString());
			//ps.setInt(3, c.getId());
			ps.setInt(2, g.getWord().getId());
			ps.setInt(3, g.getMap().getId());
			long date = System.currentTimeMillis();
			ps.setLong(4, date);
			ps.executeUpdate();
			int plot_id = -1;
			ResultSet rs = plugin.getDB().get("SELECT ID FROM plots WHERE date='" + date + "' AND map_id=" + g.getMap().getId());
			rs.first();
			plot_id = rs.getInt("ID");
			if (plot_id == -1) {
				System.out.println("ERROR, PLOTID IS -1");
				System.out.println(builders);
				System.out.println(g);
				System.out.println(plot);
				System.out.println(plot.getId());
				System.out.println(c);
				System.out.println(ps);
				System.out.println(rs);
				System.out.println(date);
				throw new Exception();
			} else {
				List<Player> players = builders.getPlayers();
				for (int i = 0; i<players.size(); i++) {
					Player p = players.get(i);
					PreparedStatement pr = plugin.getDB().getPrepare("INSERT INTO playerplots (UUID,plot_id,npc_id) VALUES(?,?,?)");
					pr.setString(1, p.getUniqueId().toString());
					pr.setInt(2, plot_id);
					pr.setInt(3, c.get(i).getId());
					pr.executeUpdate();
				}
				return plot_id;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}*/
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
