package net.yzimroni.buildsomething2.command;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.UUID;

import net.yzimroni.buildsomething2.BuildSomethingPlugin;
import net.yzimroni.buildsomething2.utils.JsonBuilder;
import net.yzimroni.buildsomething2.utils.JsonBuilder.ClickAction;
import net.yzimroni.buildsomething2.utils.Utils;
import net.yzimroni.buildsomething2.utils.JsonBuilder.HoverAction;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.worldcretornica.plotme_core.Plot;
import com.worldcretornica.plotme_core.PlotMeCoreManager;
import com.worldcretornica.plotme_core.api.Vector;
import com.worldcretornica.plotme_core.bukkit.BukkitUtil;

public class PlotCommand implements CommandExecutor {
	
	private BuildSomethingPlugin plugin;
	private LinkedHashMap<Integer, String> cache;
	private HashMap<UUID, Long> times;
	private HashMap<UUID, AcceptType> accepts;
	
	public PlotCommand(BuildSomethingPlugin p) {
		plugin = p;
		cache = new LinkedHashMap<Integer, String>();
		times = new HashMap<UUID, Long>();
		accepts = new HashMap<UUID, AcceptType>();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (commandLabel.equalsIgnoreCase("plottp")) {
			return teleportPlot(sender, commandLabel, args);
		} else if (commandLabel.equalsIgnoreCase("plot")) {
			/*
			 * plot help
			 * plot info
			 * plot list
			 * plot sendbot V
			 * plot accept V
			 * plot moveplotbot V
			 * plot denyplotbot V
			 * plot botreqlist V
			 * 
			 */
			if (args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("help"))) {
				//Help
			}
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("sendbot")) {
					if (sender instanceof Player) {
						Player p = (Player) sender;
						Plot plot = getPlot(p, true);
						if (plot == null) {
							p.sendMessage(ChatColor.RED + "invalid plot");
							return false;
						}
						if (!p.getWorld().getName().equalsIgnoreCase("plotworld")) {
							p.sendMessage(ChatColor.RED + "You must be in the normal plot world");
							return false;
						}
						p.sendMessage(ChatColor.GREEN + "Bot build mean the build will build in the bot game in the server");
						p.sendMessage(ChatColor.YELLOW + "If you are sure you want to request to send your plot to the bot builds, write /plot accept");
						addAcceptList(p.getUniqueId(), AcceptType.SEND_REQ_BOT);
					}
				} else if (args[0].equalsIgnoreCase("accept")) {
					if (sender instanceof Player) {
						Player p = (Player) sender;
						if (accepts.containsKey(p.getUniqueId())) {
							AcceptType a = accepts.get(p.getUniqueId());
							accepts.remove(p.getUniqueId());
							if (a == AcceptType.SEND_REQ_BOT) {
								Plot plot = getPlot(p, true);
								if (plot == null) {
									p.sendMessage(ChatColor.RED + "invalid plot");
									return false;
								}
								if (!p.getWorld().getName().equalsIgnoreCase("plotworld")) {
									p.sendMessage(ChatColor.RED + "You must be in the normal plot world");
									return false;
								}
								setPlotBotRequest(plot.getId().toString(), true);
								p.sendMessage(ChatColor.GREEN + "You send your plot to the staff review");
							} else if (a == AcceptType.SEND_BOT) {
								if (!p.isOp()) return false;
								Plot plot = getPlot(p, false);
								if (plot == null) {
									p.sendMessage(ChatColor.RED + "invalid plot");
									return false;
								}
								if (!p.getWorld().getName().equalsIgnoreCase("plotworld")) {
									p.sendMessage(ChatColor.RED + "You must be in the normal plot world");
									return false;
								}
								plugin.getGameManager().getPlotManager().movePlotToBot(plot);
								p.sendMessage(ChatColor.GOLD + "Plot moved!");
							}
						}
					}
				} else if (args[0].equalsIgnoreCase("denyplotbot")) {
					if (sender instanceof Player) {
						Player p = (Player) sender;
						if (p.isOp()) {
							Plot plot = getPlot(p, false);
							if (plot == null) {
								p.sendMessage(ChatColor.RED + "invalid plot");
								return false;
							}
							if (!p.getWorld().getName().equalsIgnoreCase("plotworld")) {
								p.sendMessage(ChatColor.RED + "You must be in the normal plot world");
								return false;
							}
							setPlotBotRequest(plot.getId().toString(), false);
							p.sendMessage(ChatColor.RED + "You denied the plot from be bot plot");
						}
					}
				} else if (args[0].equalsIgnoreCase("moveplotbot")) {
					if (sender instanceof Player) {
						Player p = (Player) sender;
						if (p.isOp()) {
							Plot plot = getPlot(p, false);
							if (plot == null) {
								p.sendMessage(ChatColor.RED + "invalid plot");
								return false;
							}
							if (!p.getWorld().getName().equalsIgnoreCase("plotworld")) {
								p.sendMessage(ChatColor.RED + "You must be in the normal plot world");
								return false;
							}
							addAcceptList(p.getUniqueId(), AcceptType.SEND_BOT);
							p.sendMessage(ChatColor.YELLOW + "Are you sure you want to send this plot to be bot plot? /plot accept");
						}
					}
				} else if (args[0].equalsIgnoreCase("botreqlist")) {
					if (sender instanceof Player) {
						Player p = (Player) sender;
						if (p.isOp()) {
							try {
								ResultSet rs = plugin.getDB().get("SELECT * FROM games WHERE bot_request=1");
								while (rs.next()) {
									JsonBuilder jb = new JsonBuilder(rs.getInt("ID") + " | " + rs.getString("plot_id")).withColor(ChatColor.YELLOW).withHoverEvent(HoverAction.SHOW_TEXT, "Click here to teleport to plot #" + rs.getInt("ID")).withClickEvent(ClickAction.RUN_COMMAND, "/plottp " + rs.getInt("ID"));
									jb.sendJson(p);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
		return false;
	}
	
	private Plot getPlot(Player p, boolean check_p) {
		if (p.getWorld().getName().equalsIgnoreCase("plotworld")) {
			Plot plot = PlotMeCoreManager.getInstance().getPlot(BukkitUtil.adapt(p.getLocation()));
			if (plot != null) {
				if (plot.getOwnerId() != null) {
					if (check_p) {
						if (plot.getOwner().equals(p.getUniqueId()) || plot.isMember(p.getUniqueId()).isPresent()) {
							return plot;
						}
					} else {
						return plot;
					}
				}
			}
		}
		return null;
		
	}
	
	private void setPlotBotRequest(String plot_id, boolean mode) {
		plugin.getDB().set("UPDATE games SET bot_request=" + (mode ? "1" : "0") + " WHERE plot_id='" + plot_id + "'");
	}
	
	private boolean teleportPlot(CommandSender sender, String commandLabel, String[] args) {

		if (!(sender instanceof Player)) {
			return false;
		}
		Player p = (Player) sender;
		if (args.length != 1) {
			p.sendMessage(ChatColor.RED + "Usage: /" + commandLabel.toLowerCase() + " <plot_id>");
			return false;
		}
		if (plugin.getGameManager().checkPlayer(p)) {
			p.sendMessage(ChatColor.RED + "You can't teleport to a plot when in-game");
			return false;
		}
		if (Utils.isInt(args[0])) {
			int id = Utils.getInt(args[0]);
			if (id < 0) {
				p.sendMessage(ChatColor.RED + "id must be positive");
				return false;
			}
			String plot_id = null;
			if (cache.containsKey(id)) {
				plot_id = cache.get(id);
			} else {
				if (times.containsKey(p.getUniqueId())) {
					if (System.currentTimeMillis() - times.get(p.getUniqueId()) < (1 * 1000)) {
						p.sendMessage(ChatColor.RED + "Please wait");
						return false;
					} else {
						times.remove(p.getUniqueId());
					}
				}
				times.put(p.getUniqueId(), System.currentTimeMillis());
				plot_id = plugin.getGameManager().getPlotManager().getPlotId(id);					
				addPlotId(id, plot_id);
			}
			
			if (plot_id != null) {
				Plot pl = PlotMeCoreManager.getInstance().getPlotById(Utils.plotId(plot_id), PlotMeCoreManager.getInstance().getWorld("plotworld"));
				Vector lm = pl.getPlotTopLoc();
				Location l = new Location(Bukkit.getWorld("plotworld"), lm.getX(), 66, lm.getZ(), 145, 0);
				l.add(0.5, 0, 4.5);
				p.teleport(l);
				p.sendMessage(ChatColor.GREEN + "Teleported to plot number " + id);
				return true;
			} else {
				p.sendMessage(ChatColor.RED + "Plot not found");
			}
		} else {
			p.sendMessage(ChatColor.RED + "Invaild plot id '" + args[0] + "'");
		}
		return false;
	
	}
	
	private void addAcceptList(final UUID u, final AcceptType a) {
		accepts.put(u, a);
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				if (accepts.containsKey(u)) {
					if (accepts.get(u) == a) {
						accepts.remove(u);
					}
				}				
			}
		}, 60 * 20);
	}
	
	public void addPlotId(int i, String p) {
		if (cache.size() > 10000) {
			cache.clear();
			times.clear();
		} else if (cache.containsKey(i)) {
			cache.remove(i);
		}
		cache.put(i, p);
	}
	

}
enum AcceptType{
	SEND_REQ_BOT, SEND_BOT
}