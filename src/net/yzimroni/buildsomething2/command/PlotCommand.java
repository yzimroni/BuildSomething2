package net.yzimroni.buildsomething2.command;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.worldcretornica.plotme_core.Plot;
import com.worldcretornica.plotme_core.PlotMeCoreManager;
import com.worldcretornica.plotme_core.api.Vector;
import com.worldcretornica.plotme_core.bukkit.BukkitUtil;

import net.yzimroni.buildsomething2.BuildSomethingPlugin;
import net.yzimroni.buildsomething2.utils.JsonBuilder;
import net.yzimroni.buildsomething2.utils.JsonBuilder.ClickAction;
import net.yzimroni.buildsomething2.utils.JsonBuilder.HoverAction;
import net.yzimroni.buildsomething2.utils.PlotNotFoundException;
import net.yzimroni.buildsomething2.utils.Utils;
import net.yzimroni.commandmanager.command.Command;
import net.yzimroni.commandmanager.command.CommandExecutor;
import net.yzimroni.commandmanager.command.CommandValidator;
import net.yzimroni.commandmanager.command.SubCommand;
import net.yzimroni.commandmanager.command.args.ArgumentData;
import net.yzimroni.commandmanager.command.args.ArgumentValidCheck;
import net.yzimroni.commandmanager.command.args.arguments.IntegerArgument;
import net.yzimroni.commandmanager.command.methodexecutor.MethodExecutor;
import net.yzimroni.commandmanager.command.methodexecutor.MethodExecutorClass;
import net.yzimroni.commandmanager.manager.CommandManager;

public class PlotCommand implements MethodExecutorClass {
	
	private BuildSomethingPlugin plugin;
	private Cache<Integer, String> plotIdCache = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.HOURS)
			.maximumSize(500).build();
	
	public PlotCommand(BuildSomethingPlugin p) {
		plugin = p;
	}
	
	protected void createCommands() {
		Command plottp = new Command("plottp", "Teleport to a plot", MethodExecutor.createByMethodName(this, "plotTp"));
		plottp.addArgument(new IntegerArgument("plotid", true, 1, null, false));
		plottp.setOnlyPlayer(true);
		plottp.setValidator(new CommandValidator() {
			
			@Override
			public ArgumentValidCheck validate(CommandSender sender, Command command, ArgumentData args) {
				Player p = (Player) sender; //Safe cast, the command only allow to execute from a player
				return ArgumentValidCheck.create(!plugin.getGameManager().checkPlayer(p), ChatColor.RED + "You can't teleport to a plot when in-game");
			}
		});
		
		Command plot = new Command("plot", "Plot command", new CommandExecutor() {
			
			@Override
			public void executeCommand(CommandSender sender, Command command, ArgumentData args) {
				command.printHelp(sender, 1);
			}
		});
		
		SubCommand send = new SubCommand("send", "Send your plot to bot-plot check", MethodExecutor.createByMethodName(this, "plotSend"));
		send.setOnlyPlayer(true);
		plot.addSubCommand(send);
		
		SubCommand admin = new SubCommand("admin", "Plot admin commands", new CommandExecutor() {
			
			@Override
			public void executeCommand(CommandSender sender, Command command, ArgumentData args) {
				command.printHelp(sender, -1);
			}
		});
		
		SubCommand list = new SubCommand("list", "List of all plots waiting for staff review", MethodExecutor.createByMethodName(this, "adminPlotList"));
		list.setOnlyPlayer(true);
		admin.addSubCommand(list);
		
		SubCommand accept = new SubCommand("accept", "Accept a plot to be a bot plot", MethodExecutor.createByMethodName(this, "adminPlotAccept"));
		accept.setOnlyPlayer(true);
		accept.addArgument(new IntegerArgument("plot", true, 0, null, false));
		admin.addSubCommand(accept);
		
		SubCommand deny = new SubCommand("deny", "Deny a plot to be a bot plot", MethodExecutor.createByMethodName(this, "adminPlotDeny"));
		deny.setOnlyPlayer(true);
		deny.addArgument(new IntegerArgument("plot", true, 0, null, false));
		admin.addSubCommand(deny);
		
		plot.addSubCommand(admin);
		
		CommandManager.get().registerCommand(plugin, plottp);
		CommandManager.get().registerCommand(plugin, plot);
	}
	
	public boolean plotTp(CommandSender sender, Command command, ArgumentData args) {

		Player p = (Player) sender; //Safe cast, the command only allowed to be executed from a player
		int id = args.get("plotid", Integer.class);

		String plot_id = getPlotId(id);

		if (plot_id != null) {
			// TODO botplot
			Plot pl = PlotMeCoreManager.getInstance().getPlotById(Utils.plotId(plot_id),
					PlotMeCoreManager.getInstance().getWorld("plotworld"));
			Vector lm = pl.getPlotTopLoc();
			Location l = new Location(Bukkit.getWorld("plotworld"), lm.getX(), 66, lm.getZ(), 145, 0);
			l.add(0.5, 0, 4.5);
			p.teleport(l);
			p.sendMessage(ChatColor.GREEN + "Teleported to plot number " + id);
			return true;
		} else {
			p.sendMessage(ChatColor.RED + "Plot not found");
		}
		return false;
	}
	
	public boolean plotSend(CommandSender sender, Command command, ArgumentData args) {
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
		setPlotBotRequest(plot.getId().toString(), true);
		p.sendMessage(ChatColor.GREEN + "You send your plot to staff review");
		//TODO send a message to online staff
		return true;
	}
	
	
	public boolean adminPlotList(CommandSender sender, Command command, ArgumentData args) {
		try {
			 ResultSet rs = plugin.getDB().get("SELECT * FROM games WHERE request_bot=1");
			 if (rs.next()) {
					JsonBuilder jb = new JsonBuilder(rs.getInt("ID") + " | " + rs.getString("plot_id")).withColor(ChatColor.YELLOW).withHoverEvent(HoverAction.SHOW_TEXT, "Click here to teleport to plot #" + rs.getInt("ID")).withClickEvent(ClickAction.RUN_COMMAND, "/plottp " + rs.getInt("ID"));
					jb.sendJson((Player) sender);
			 }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
	public boolean adminPlotAccept(CommandSender sender, Command command, ArgumentData args) {
		Player p = (Player) sender;
		int plotId = args.get("plot", Integer.class);
		//plugin.getGameManager().getPlotManager().movePlotToBot(plotId, p);
		return true;
	}
	
	public boolean adminPlotDeny(CommandSender sender, Command command, ArgumentData args) {
		int plotId = args.get("plot", Integer.class);
		String plotMePlotId = getPlotId(plotId);
		setPlotBotRequest(plotMePlotId, false);
		sender.sendMessage("You denied plot id " + plotId + " from the bot plots");
		return false;
	}
	

	private String getPlotId(int id) {
		try {
			return plotIdCache.get(id, () -> {
				String plotId = plugin.getGameManager().getPlotManager().getPlotId(id);
				if (plotId != null) {
					return plotId;
				} else {
					throw new PlotNotFoundException();
				}
			});
		} catch (ExecutionException e) {
			if (!(e.getCause() instanceof PlotNotFoundException)) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	private Plot getPlot(Player p, boolean check_p) {
		if (p.getWorld().getName().equalsIgnoreCase("plotworld")) {
			Plot plot = PlotMeCoreManager.getInstance().getPlot(BukkitUtil.adapt(p.getLocation()));
			if (plot != null) {
				if (plot.getOwnerId() != null) {
					if (check_p) {
						if (plot.getOwnerId().equals(p.getUniqueId()) || plot.isMember(p.getUniqueId()).isPresent()) {
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
		PreparedStatement pre = plugin.getDB().getPrepare("UPDATE games SET request_bot=? WHERE plot_id=?");
		try {
			pre.setBoolean(1, mode);
			pre.setString(2, plot_id);
			pre.executeUpdate();
			pre.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void addPlotId(int i, String p) {
		plotIdCache.put(i, p);
	}
	
}
