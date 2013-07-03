package org.mcsg.survivalgames.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.mcsg.survivalgames.Game;

public class SpectatorUtil {
	private static List< String > browsingInventory = new ArrayList< String >(); 
	private static HashMap< String, Game > spectating = new HashMap< String, Game >();

	public static void openInventory(Player p, Game g) {
		String name = ChatColor.RED + "" + ChatColor.BOLD + "" + ChatColor.ITALIC + "Tributes";
		Inventory inventory = Bukkit.createInventory(p, g.getActivePlayers(), name);
			
		for (Player pl : g.getActivePlayerList()) {
			ItemStack skull = new ItemStack(Material.SKULL_ITEM);
			SkullMeta meta = (SkullMeta)skull.getItemMeta();
			meta.setOwner(pl.getName());
			skull.setItemMeta(meta);
			inventory.addItem(skull);
		}
			
		p.openInventory(inventory);
		browsingInventory.add(p.getName());
	}
	
	public static void closeInventory(Player p) {
		if (browsingInventory.contains(p.getName())) {
			browsingInventory.remove(p.getName());
			p.closeInventory();
		}
	}
	
	public static boolean isBrowsingInventory(Player p) {
		return browsingInventory.contains(p.getName());
	}
	
	public static void addSpectator(Player p, Game g) {
		spectating.put(p.getName(), g);
	}
	
	public static void removeSpectator(Player p) {
		spectating.remove(p.getName());
		
		if (isBrowsingInventory(p)) {
			closeInventory(p);
		}
	}
	
	public static boolean isSpectating(Player p) {
		return spectating.containsKey(p.getName());
	}
	
	public static Game getGame(Player p) {
		if (isSpectating(p)) {
			return spectating.get(p.getName());
		}
		
		return null;
	}
}