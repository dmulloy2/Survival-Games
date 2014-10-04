package net.dmulloy2.survivalgames.events;

import net.dmulloy2.survivalgames.SurvivalGames;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class TeleportEvent implements Listener {
    private final SurvivalGames plugin;

    public TeleportEvent(SurvivalGames plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void playerTeleport(PlayerTeleportEvent event) {
        Player p = event.getPlayer();
        int id = plugin.getGameManager().getPlayerGameId(p);
        if (id == -1) {
            return;
        }
        if (plugin.getGameManager().getGame(id).isPlayerActive(p) && event.getCause() == PlayerTeleportEvent.TeleportCause.COMMAND) {
            p.sendMessage(ChatColor.RED + " Cannot teleport while ingame!");
            event.setCancelled(true);
        }
    }
}
