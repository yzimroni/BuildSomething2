package net.yzimroni.buildsomething2.command;

import net.yzimroni.buildsomething2.BuildSomethingPlugin;
import net.yzimroni.buildsomething2.player.PlayerData;
import net.yzimroni.buildsomething2.player.stats.GameTypeStats;
import net.yzimroni.buildsomething2.utils.Utils;

import org.bukkit.ChatColor;
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
					showStats(sender, (Player) sender);
				} else {
					sender.sendMessage("Usage: /" + commandLabel.toLowerCase() + " <name>");
				}
			} else if (args.length == 1) {
				String name = args[0];
				OfflinePlayer op = Utils.getOfflinePlayer(name);
				showStats(sender, op);
			}
		}
		return false;
	}

	private boolean showStats(CommandSender sender, OfflinePlayer player) {
		if (!player.hasPlayedBefore()) {
			sender.sendMessage("Player not found");
			return false;
		}
		PlayerData data = plugin.getPlayerManager().getData(player.getUniqueId());
		sender.sendMessage("");
		sender.sendMessage(player.getName() + "'s Stats:");
		sender.sendMessage("Coins: " + plugin.getPlayerManager().getEconomy().getBalance(player.getUniqueId()));
		sender.sendMessage("Blocks: " + data.getBlocks().size());
		for (GameTypeStats type : data.getStats().values()) {
			sender.sendMessage(ChatColor.GREEN + type.getGameType().getDisplayName() + " Game:");
			sender.sendMessage(ChatColor.BLUE + "    " + "Total games: " + type.getTotalGames());
			sender.sendMessage(ChatColor.BLUE + "    " + "Builder: " + type.getBuilder());
			sender.sendMessage(ChatColor.BLUE + "    " + "Normal: " + type.getNormal());
			sender.sendMessage(ChatColor.BLUE + "    " + "Know the word: " + type.getKnow());
			sender.sendMessage(ChatColor.BLUE + "    " + "Know first: " + type.getKnowFirst());

		}

		return true;
	}

}
