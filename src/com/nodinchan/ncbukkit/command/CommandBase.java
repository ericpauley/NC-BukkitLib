package com.nodinchan.ncbukkit.command;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.nodinchan.ncbukkit.loader.Loadable;

public class CommandBase extends Loadable {
	
	protected final JavaPlugin plugin;
	
	protected String description;
	protected String permission;
	protected String usage;
	
	protected String[] aliases;
	
	public CommandBase(String name, JavaPlugin plugin) {
		super(name);
		this.plugin = plugin;
		this.description = "";
		this.usage = "/<command>";
		this.permission = "";
		this.aliases = new String[0];
	}
	
	public final String[] getAliases() {
		return aliases;
	}
	
	public final String getDescription() {
		return description;
	}
	
	public final String getPermission() {
		return permission;
	}
	
	public final String getUsage() {
		return usage;
	}
	
	public void commandNotFound(CommandSender sender, String[] args) {
		sender.sendMessage("[" + plugin.getName() + "] Command usage incorrect");
		sender.sendMessage("[" + plugin.getName() + "] Usage: " + usage);
	}
	
	public final void invalidSender(CommandSender sender) {
		if (sender instanceof Player)
			sender.sendMessage("[" + plugin.getName() + "] You cannot use this command as a player");
		
		if (sender instanceof ConsoleCommandSender)
			sender.sendMessage("[" + plugin.getName() + "] You cannot use this command from the console");
	}
	
	public final void noPermission(CommandSender sender) {
		sender.sendMessage("[" + plugin.getName() + "] You do not have permission");
	}
}