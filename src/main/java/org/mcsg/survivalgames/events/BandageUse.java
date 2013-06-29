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
			ItemStack stack = player.getItemInHand();
			if (stack == null || stack.getType() == Material.AIR) {
				return;
			}
			
			if (stack.getType() == Material.PAPER) {
				player.getItemInHand().setAmount(stack.getAmount() - 1);
				
				int amountToGive = amountToGive(player);
				player.setHealth(player.getHealth() + amountToGive);
				player.sendMessage(ChatColor.GREEN + "You used a bandage and got " + amountToGive + " hearts!");
			}
		}
	}
	
	public int amountToGive(Player player) {
		if ((player.getHealth() + 5) > 20) {
			return (20 - player.getHealth());
		} else {
			return 5;
		}
	}
}