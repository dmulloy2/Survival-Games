package org.mcsg.survivalgames.util;

import org.bukkit.Location;

public class LocationUtil {
	public static String locToString(Location loc) {
		StringBuilder line = new StringBuilder();
		line.append("World: " + loc.getWorld().getName());
		line.append(" X: " + loc.getBlockX());
		line.append(" Y: " + loc.getBlockY());
		line.append(" Z: " + loc.getBlockZ());
		return line.toString();
	}
}