package net.yzimroni.buildsomething2.command;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import net.yzimroni.buildsomething2.BuildSomethingPlugin;
import net.yzimroni.buildsomething2.utils.Utils;
import net.yzimroni.commandmanager.command.Command;
import net.yzimroni.commandmanager.command.SubCommand;
import net.yzimroni.commandmanager.command.args.ArgumentData;
import net.yzimroni.commandmanager.command.args.arguments.StringArgument;
import net.yzimroni.commandmanager.command.methodexecutor.MethodExecutor;
import net.yzimroni.commandmanager.command.methodexecutor.MethodExecutorClass;
import net.yzimroni.commandmanager.command.methodexecutor.MethodId;
import net.yzimroni.commandmanager.manager.CommandManager;

public class MoneyCommand implements MethodExecutorClass {
	private BuildSomethingPlugin plugin;
	
	public MoneyCommand(BuildSomethingPlugin p) {
		plugin = p;
	}
	
	protected void createCommands() {
		Command money = new Command("money", "Money command", MethodExecutor.createByMethodId(this, "money"));
		money.setAliases("m");
		
		SubCommand view = new SubCommand("view", "View the balance of a player", MethodExecutor.createByMethodId(this, "view"));
		view.addArgument(new StringArgument("player", true));
		view.setPermission("buildsomething2.command.money.view");
		money.addSubCommand(view);
		
		CommandManager.get().registerCommand(plugin, money);
	}
	
	@MethodId("money")
	public void moneyCommand(CommandSender sender, Command command, ArgumentData args) {
		if (sender instanceof Player) {
			sender.sendMessage("You have " + plugin.getPlayerManager().getEconomy().getBalance(((Player) sender).getUniqueId()) + "$");
		} else {
			sender.sendMessage("Only players can do this");
		}
	}

	@MethodId("view")
	public void viewCommand(CommandSender sender, Command command, ArgumentData args) {
		if (sender instanceof ConsoleCommandSender || ((Player) sender).isOp()) {
			String name = args.get("player", String.class);
			OfflinePlayer o = Utils.getOfflinePlayer(name);
			if (o != null && o.hasPlayedBefore() && o.getName() != null && !o.getName().isEmpty()) {
				sender.sendMessage(o.getName() + " Have " + plugin.getPlayerManager().getEconomy().getBalance(o.getUniqueId()) + "$");
			} else {
				sender.sendMessage("The player " + name + " not found");
			}
		}
	}

}
