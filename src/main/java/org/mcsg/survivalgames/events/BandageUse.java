package org.mcsg.survivalgames.events;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class BandageUse implements Listener {

	@EventHandler
	public void onBandageUse(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (player.getItemInHand() == new ItemStack(Material.PAPER)) {
				player.getInventory().removeItem(new ItemStack(Material.PAPER, 1));
				player.setHealth(e.getPlayer().getHealth() + 10);
				player.sendMessage(ChatColor.GREEN + "You used a bandage and got 5 hearts.");
			}
		}
	}
}