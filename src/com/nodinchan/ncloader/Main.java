package com.nodinchan.ncloader;

import java.lang.reflect.Method;

import org.bukkit.plugin.Plugin;

import com.nodinchan.ncloader.metrics.Metrics;
import com.nodinchan.ncloader.metrics.Metrics.Graph;
import com.nodinchan.ncloader.metrics.Metrics.Plotter;
import com.nodinchan.ncloader.metrics.MetricsLite;

public class Main {
	
	private final Plugin plugin;
	
	public Main(Plugin plugin) {
		this.plugin = plugin;
	}
	
	public MetricsHook getMetricsHook() {
		return new MetricsHook(plugin);
	}
	
	public MetricsLiteHook getMetricsLiteHook() {
		return new MetricsLiteHook(plugin);
	}
	
	public static final class MetricsHook extends MetricsLiteHook {
		
		private Metrics metrics;
		
		public MetricsHook(Plugin plugin) {
			super(plugin);
			try { this.metrics = new Metrics(plugin);
			} catch (Exception e) { e.printStackTrace(); }
		}
		
		public void addCustomData(Plotter plotter) {
			if (metrics != null)
				metrics.addCustomData(plotter);
		}
		
		public Graph createGraph(String name) {
			if (metrics == null)
				return null;
			
			return metrics.createGraph(name);
		}
		
		public Plotter createPlotter(String name, final String mthdName, final Object mthdLoc, final Object[] mthdParams) {
			if (metrics == null)
				return null;
			
			return new Plotter(name) {
				
				@Override
				public int getValue() {
					try {
						Class<?>[] params = new Class<?>[mthdParams.length];
						for (int param = 0; param < mthdParams.length; param++) { params[param] = mthdParams[param].getClass(); }
						
						Method method = mthdLoc.getClass().getDeclaredMethod(mthdName, params);
						method.setAccessible(true);
						return (Integer) method.invoke(mthdLoc, mthdParams);
						
					} catch (Exception e) { return 0; }
				}
			};
		}
	}
	
	public static class MetricsLiteHook {
		
		private MetricsLite metrics;
		
		public MetricsLiteHook(Plugin plugin) {
			try { metrics = new MetricsLite(plugin);
			} catch (Exception e) { e.printStackTrace(); }
		}
		
		public void enable() {
			if (metrics != null) {
				try { metrics.enable();
				} catch (Exception e) { e.printStackTrace(); }
			}
		}
		
		public void disable() {
			if (metrics != null) {
				try { metrics.disable();
				} catch (Exception e) { e.printStackTrace(); }
			}
		}
		
		public boolean isOptOut() {
			if (metrics == null)
				return true;
			
			return metrics.isOptOut();
		}
		
		public boolean start() {
			if (metrics == null)
				return false;
			
			return metrics.start();
		}
		
	}
}