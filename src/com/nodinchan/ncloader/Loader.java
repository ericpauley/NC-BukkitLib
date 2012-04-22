package com.nodinchan.ncloader;

/*     Copyright (C) 2012  Nodin Chan <nodinchan@live.com>
 * 
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

/**
 * Loader - Loader base for loading Loadables
 * 
 * @author NodinChan
 *
 * @param <T> A loadable class
 */
@SuppressWarnings("unchecked")
public class Loader<T extends Loadable> implements Listener {
	
	private final Plugin plugin;
	
	private final ClassLoader loader;
	
	private final Object[] paramTypes;
	
	private final List<Class<?>> ctorParams;
	private final List<File> files;
	private final List<T> loadables;
	
	public Loader(Plugin plugin, File dir, Object[] paramTypes) {
		this.plugin = plugin;
		this.paramTypes = paramTypes;
		this.ctorParams = new ArrayList<Class<?>>();
		this.files = new ArrayList<File>();
		this.loadables = new ArrayList<T>();
		
		for (Object paramType : paramTypes) { ctorParams.add(paramType.getClass()); }
		
		List<URL> urls = new ArrayList<URL>();
		
		for (String loadableFile : dir.list()) {
			if (loadableFile.endsWith(".jar")) {
				File file = new File(dir, loadableFile);
				files.add(file);
				
				try { urls.add(file.toURI().toURL()); } catch (MalformedURLException e) { e.printStackTrace(); }
			}
		}
		
		this.loader = URLClassLoader.newInstance(urls.toArray(new URL[urls.size()]), plugin.getClass().getClassLoader());
	}
	
	/**
	 * Gets the Logger
	 * 
	 * @return The Logger
	 */
	public Logger getLogger() {
		return plugin.getLogger();
	}
	
	/**
	 * Loads the Loadables
	 * 
	 * @return List of loaded loadables
	 */
	public List<T> load() {
		for (File file : files) {
			try {
				JarFile jarFile = new JarFile(file);
				String mainClass = null;
				
				if (jarFile.getEntry("path.yml") != null) {
					JarEntry element = jarFile.getJarEntry("path.yml");
					BufferedReader reader = new BufferedReader(new InputStreamReader(jarFile.getInputStream(element)));
					mainClass = reader.readLine().substring(12);
				}
				
				if (mainClass != null) {
					Class<?> clazz = Class.forName(mainClass, true, loader);
					Class<? extends Loadable> loadableClass = clazz.asSubclass(Loadable.class);
					Constructor<? extends Loadable> ctor = loadableClass.getConstructor(ctorParams.toArray(new Class<?>[0]));
					T loadable = (T) ctor.newInstance(paramTypes);
					
					LoadEvent event = new LoadEvent(plugin, loadable, jarFile);
					plugin.getServer().getPluginManager().callEvent(event);
					
					loadable.init();
					loadables.add(loadable);
					
				} else { throw new Exception(); }
				
			} catch (Exception e) {
				e.printStackTrace();
				getLogger().log(Level.WARNING, "The JAR file " + file.getName() + " failed to load");
			}
		}
		
		return loadables;
	}
	
	/**
	 * Registers the Event Listener
	 * 
	 * @param loader The Loader to register
	 */
	public void register(Loader<T> loader) {
		plugin.getServer().getPluginManager().registerEvents(loader, plugin);
	}
	
	/**
	 * Sorts a list of Loadables by name in alphabetical order
	 * 
	 * @param loadables The list of Loadables to sort
	 * 
	 * @return The sorted list of Loadables
	 */
	public List<T> sort(List<T> loadables) {
		List<T> sortedLoadables = new ArrayList<T>();
		List<String> names = new ArrayList<String>();
		
		for (T t : loadables) {
			names.add(t.getName());
		}
		
		Collections.sort(names);
		
		for (String name : names) {
			for (T t : loadables) {
				if (t.getName().equals(name))
					sortedLoadables.add(t);
			}
		}
		
		return sortedLoadables;
	}
}