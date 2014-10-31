package net.dmulloy2.survivalgames.events;

import net.dmulloy2.survivalgames.SurvivalGames;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class BandageUse implements Listener {
    private final SurvivalGames plugin;

    public BandageUse(SurvivalGames plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBandageUse(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if (!plugin.getGameHandler().isPlayerActive(player))
            return;

        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack stack = player.getItemInHand();
            if (stack == null || stack.getType() == Material.AIR)
                return;

            if (stack.getType() == Material.PAPER) {
                player.getItemInHand().setAmount(stack.getAmount() - 1);
                if (player.getItemInHand().getAmount() == 0)
                    player.getInventory().removeItem(player.getItemInHand());
                player.updateInventory();

                double amountToGive = amountToGive(player);
                player.setHealth(player.getHealth() + amountToGive);
                player.sendMessage(ChatColor.GREEN + "You used a bandage and got " + (amountToGive / 2) + " hearts!");
            }
        }
    }

    private static final double HEALTH = 5.0D;

    private double amountToGive(Player player) {
        if (player.getHealth() + HEALTH > 20.0D)
            return 20.0D - HEALTH;

        return HEALTH;
    }
}
