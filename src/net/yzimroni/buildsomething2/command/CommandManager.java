package net.yzimroni.buildsomething2.command;

import net.yzimroni.buildsomething2.BuildSomethingPlugin;

public class CommandManager {

	private BuildSomethingPlugin plugin;
	private MoneyCommand money;
	private GameCommand game;
	private StatsCommand stats;
	private PlotCommand plot;
	
	public CommandManager(BuildSomethingPlugin p) {
		plugin = p;
		money = new MoneyCommand(plugin);
		game = new GameCommand(plugin);
		stats = new StatsCommand(plugin);
		plot = new PlotCommand(plugin);
		
		plugin.getCommand("money").setExecutor(money);
		plugin.getCommand("m").setExecutor(money);

		game.createCommands();
		
		plugin.getCommand("stats").setExecutor(stats);
		
		plugin.getCommand("plot").setExecutor(plot);
		plugin.getCommand("plottp").setExecutor(plot);
	}
	
	public void addPlotId(int i, String p) {
		plot.addPlotId(i, p);
	}
	
	public void onDisable() {
		money = null;
		game = null;
		stats = null;
		plot = null;
	}
	
}
