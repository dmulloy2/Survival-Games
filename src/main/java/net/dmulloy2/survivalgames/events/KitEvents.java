package net.dmulloy2.survivalgames.events;

import net.dmulloy2.survivalgames.SurvivalGames;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class KitEvents implements Listener {
    private final SurvivalGames plugin;

    public KitEvents(SurvivalGames plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void itemClick(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player) {
            Player p = (Player) e.getWhoClicked();
            if (plugin.getGameHandler().isInKitMenu(p)) {
                if (e.getRawSlot() == e.getSlot()) {
                    plugin.getGameHandler().selectKit(p, e.getRawSlot() % 9);
                }
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void InvClose(InventoryCloseEvent e) {
        plugin.getGameHandler().leaveKitMenu((Player) e.getPlayer());
    }
}
