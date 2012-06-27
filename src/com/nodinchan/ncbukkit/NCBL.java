package com.nodinchan.ncbukkit;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.nodinchan.ncbukkit.metrics.Metrics;
import com.nodinchan.ncbukkit.metrics.Metrics.Graph;
import com.nodinchan.ncbukkit.metrics.Metrics.Plotter;

public final class NCBL extends JavaPlugin implements Listener {
	
	private final String NAME = "[" + ChatColor.GOLD + "NC-BukkitLib" + ChatColor.WHITE + "]";
	
	private static final Logger log = Logger.getLogger("TitanLog");
	
	private final double currentVer = Double.parseDouble(getDescription().getVersion().trim());
	private double newVer;
	
	private final List<String> plugins = new ArrayList<String>();
	
	public void hook(JavaPlugin plugin) {
		plugins.add(plugin.getName());
	}
	
	/**
	 * Initialises Metrics
	 * 
	 * @return True is Metrics is initialised
	 */
	private boolean initMetrics() {
		log(Level.INFO, "Hooking Metrics");
		
		try {
			Metrics metrics = new Metrics(this);
			
			if (metrics.isOptOut())
				return true;
			
			Graph plugins = metrics.createGraph("Hooked Plugins");
			
			for (String plugin : this.plugins) {
				plugins.addPlotter(new Plotter(plugin) {
					
					@Override
					public int getValue() {
						return 1;
					}
				});
			}
			
			metrics.addGraph(plugins);
			
			return metrics.start();
			
		} catch (Exception e) { return false; }
	}
	
	public void log(Level level, String msg) {
		log.log(level, "[" + this + "]" + msg);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("ncbl")) {
			if (args.length > 0 && args[0].equalsIgnoreCase("update") && sender.hasPermission("NCBL.update")) {
				try {
					URL url = new URL("http://dev.bukkit.org/server-mods/nc-bukkitlib/files.rss");
					
					Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(url.openConnection().getInputStream());
					doc.getDocumentElement().normalize();
					
					Node node = doc.getElementsByTagName("item").item(0);
					
					if (node.getNodeType() == 1) {
						Element element = (Element) node;
						Element name = (Element) element.getElementsByTagName("title").item(0);
						this.newVer = Double.valueOf(name.getChildNodes().item(0).getNodeValue().split(" ")[1].trim().substring(1));
						
					} else { this.newVer = Double.valueOf(getDescription().getVersion().trim()); }
					
				} catch (Exception e) { this.newVer = Double.valueOf(getDescription().getVersion().trim()); }
				
				String message = ChatColor.GOLD + "%new" + ChatColor.DARK_PURPLE + " is out! You are running " + ChatColor.GOLD + "%current";
				sender.sendMessage(NAME + " " + message.replace("%new", newVer + "").replace("%current", currentVer + ""));
				
			} else { sender.sendMessage(NAME + " " + ChatColor.DARK_PURPLE + "You are running v" + getDescription().getVersion()); }
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public void onDisable() {
		plugins.clear();
		log(Level.INFO, "is now disabled");
	}
	
	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		
		if (!initMetrics())
			log(Level.WARNING, "Failed to hook into Metrics");
		
		log(Level.INFO, "is now enabled");
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (event.getPlayer().hasPermission("NCBL.update")) {
			if (newVer > currentVer) {
				String message = ChatColor.GOLD + "%new" + ChatColor.DARK_PURPLE + " is out! You are running " + ChatColor.GOLD + "%current";
				event.getPlayer().sendMessage(message.replace("%new", newVer + "").replace("%current", currentVer + ""));
			}
		}
	}
}