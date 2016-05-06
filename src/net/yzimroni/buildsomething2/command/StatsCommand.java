package net.yzimroni.buildsomething2.command;

import net.yzimroni.buildsomething2.BuildSomethingPlugin;
import net.yzimroni.buildsomething2.player.PlayerData;
import net.yzimroni.buildsomething2.utils.Utils;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatsCommand implements CommandExecutor {
	private BuildSomethingPlugin plugin;
	
	public StatsCommand(BuildSomethingPlugin p) {
		plugin = p;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (commandLabel.equalsIgnoreCase("stats")) {
			if (args.length == 0) {
				if (sender instanceof Player) {
					((Player) sender).performCommand(commandLabel + " " + sender.getName());
				} else {
					sender.sendMessage("Usage: /" + commandLabel.toLowerCase() + " <name>");
				}
			} else if (args.length == 1) {
				String name = args[0];
				OfflinePlayer op = Utils.getOfflinePlayer(name);
				if (!op.hasPlayedBefore()) {
					sender.sendMessage("Player not found");
					return false;
				}
				PlayerData data = plugin.getPlayerManager().getData(op.getUniqueId());
				sender.sendMessage("");
				sender.sendMessage(op.getName() + "'s Stats:");
				sender.sendMessage("Coins: " + plugin.getPlayerManager().getEconomy().getBalance(op.getUniqueId()));
				sender.sendMessage("Blocks: " + data.getBlocks().size());
				sender.sendMessage("Total games: " + data.getTotalGames());
				sender.sendMessage("Builder: " + data.getBuilder());
				sender.sendMessage("Normal player: " + data.getNormalPlayer());
				sender.sendMessage("Know the word: " + data.getKnow());
				sender.sendMessage("Know the word first: " + data.getKnowFirst());
			}
		}
		return false;
	}

}
