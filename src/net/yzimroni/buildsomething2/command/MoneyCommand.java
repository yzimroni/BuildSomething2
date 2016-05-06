package net.yzimroni.buildsomething2.command;

import net.yzimroni.buildsomething2.BuildSomethingPlugin;
import net.yzimroni.buildsomething2.utils.Utils;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class MoneyCommand implements CommandExecutor {
	private BuildSomethingPlugin plugin;
	
	public MoneyCommand(BuildSomethingPlugin p) {
		plugin = p;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (commandLabel.equalsIgnoreCase("money") || commandLabel.equalsIgnoreCase("m")) {
			if (args.length == 0) {
				if (sender instanceof Player) {
					sender.sendMessage("You have " + plugin.getPlayerManager().getEconomy().getBalance(((Player) sender).getUniqueId()) + "$");
				} else {
					sender.sendMessage("Only players can do this");
				}
			} else if (args.length == 1) {
				if (sender instanceof ConsoleCommandSender || ((Player) sender).isOp()) {
					OfflinePlayer o = Utils.getOfflinePlayer(args[0]);
					if (o != null && o.hasPlayedBefore() && o.getName() != null && !o.getName().isEmpty()) {
						sender.sendMessage(o.getName() + " Have " + plugin.getPlayerManager().getEconomy().getBalance(o.getUniqueId()) + "$");
					} else {
						sender.sendMessage("The player " + args[0] + " not found");
					}
				}
			}
		}
		return false;
	}

}
