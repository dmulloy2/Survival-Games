package net.dmulloy2.survivalgames.events;

import net.dmulloy2.survivalgames.SurvivalGames;
import net.dmulloy2.survivalgames.types.Game;
import net.dmulloy2.survivalgames.util.SpectatorUtil;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class SpectatorEvents implements Listener {
    private final SurvivalGames plugin;

    public SpectatorEvents(SurvivalGames plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (plugin.getGameManager().isSpectator(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockDamage(BlockDamageEvent event) {
        Player player = event.getPlayer();
        if (plugin.getGameManager().isSpectator(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (plugin.getGameManager().isSpectator(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerClickEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (SpectatorUtil.isSpectating(player)) {
            if (player.getItemInHand().getType() == Material.COMPASS) {
                Game game = SpectatorUtil.getGame(player);
                if (game != null) {
                    event.setCancelled(true);
                    SpectatorUtil.openInventory(player, game);
                } else {
                    SpectatorUtil.removeSpectator(player);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            if (SpectatorUtil.isBrowsingInventory(player)) {
                ItemStack stack = event.getCurrentItem();
                if (stack.getType() == Material.SKULL_ITEM) {
                    SkullMeta meta = (SkullMeta) stack.getItemMeta();
                    if (meta.hasOwner()) {
                        Player p = plugin.getServer().getPlayerExact(meta.getOwner());
                        if (p != null) {
                            if (plugin.getGameManager().isPlayerActive(p)) {
                                event.setCancelled(true);
                                player.teleport(p);
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player) {
            Player player = (Player) event.getPlayer();
            if (SpectatorUtil.isBrowsingInventory(player)) {
                SpectatorUtil.closeInventory(player);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSignChange(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        if (plugin.getGameManager().isSpectator(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            if (plugin.getGameManager().isSpectator(player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (plugin.getGameManager().isSpectator(player)) {
                event.setCancelled(true);
            }
        }
    }
}
