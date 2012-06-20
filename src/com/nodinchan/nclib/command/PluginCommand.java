package com.nodinchan.nclib.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public final class PluginCommand extends Command {
	
	private final Plugin plugin;
	private CommandExecutor executor;

	protected PluginCommand(String name, Plugin plugin) {
		super(name);
		this.plugin = plugin;
		this.executor = plugin;
	}
	
	@Override
	public boolean execute(CommandSender sender, String label, String[] args) {
		boolean success = false;
		
		if (!plugin.isEnabled())
			return false;
		
		if (!testPermission(sender))
			return true;
		
		try {
			success = executor.onCommand(sender, this, label, args);
		} catch (Throwable ex) {
			throw new CommandException("Unhandled exception executing command '" + label + "' in plugin " + plugin.getDescription().getFullName(), ex);
		}
		
		if (!success && usageMessage.length() > 0) {
			for (String line : usageMessage.replace("<command>", label).split("\n"))
				sender.sendMessage(line);
		}
		
		return success;
	}
	
	public CommandExecutor getExecutor() {
		return executor;
	}
	
	public Plugin getPlugin() {
		return plugin;
	}
	
	public void setExecutor(CommandExecutor executor) {
		this.executor = executor;
	}
}