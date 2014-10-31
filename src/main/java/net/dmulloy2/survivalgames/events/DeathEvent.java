package net.dmulloy2.survivalgames.events;

import lombok.AllArgsConstructor;
import net.dmulloy2.survivalgames.SurvivalGames;
import net.dmulloy2.survivalgames.types.Game;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

@AllArgsConstructor
public class DeathEvent implements Listener {
    private final SurvivalGames plugin;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(EntityDamageEvent event) {
        if (event.getEntityType() != EntityType.PLAYER)
            return;

        Player player = (Player) event.getEntity();
        int gameId = plugin.getGameHandler().getPlayerGameId(player);
        if (gameId == -1)
            return;

        if (!plugin.getGameHandler().isPlayerActive(player))
            return;

        Game game = plugin.getGameHandler().getGame(gameId);
        if (game.getMode() != Game.GameMode.INGAME) {
            event.setCancelled(true);
            return;
        }

        if (game.isProtectionOn()) {
            event.setCancelled(true);
            return;
        }

        if (player.getHealth() <= event.getDamage()) {
            event.setCancelled(true);
            player.setHealth(player.getMaxHealth());
            player.setFoodLevel(20);
            player.setFireTicks(0);
            PlayerInventory inv = player.getInventory();
            Location l = player.getLocation();

            for (ItemStack i : inv.getContents()) {
                if (i != null) {
                    l.getWorld().dropItemNaturally(l, i);
                }
            }

            for (ItemStack i : inv.getArmorContents()) {
                if (i != null && i.getType() != Material.AIR) {
                    l.getWorld().dropItemNaturally(l, i);
                }
            }

            game.killPlayer(player, false);
        }
    }
}
