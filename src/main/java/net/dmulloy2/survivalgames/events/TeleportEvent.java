package net.dmulloy2.survivalgames.events;

import net.dmulloy2.survivalgames.SurvivalGames;
import net.dmulloy2.survivalgames.types.Game;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class TeleportEvent implements Listener {
    private final SurvivalGames plugin;

    public TeleportEvent(SurvivalGames plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void playerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        Game game = plugin.getGameHandler().getGame(player);
        if (game == null) {
            return;
        }

        if (event.getCause() == TeleportCause.COMMAND) {
            player.sendMessage(ChatColor.RED + " Cannot teleport while ingame!");
            event.setCancelled(true);
        }
    }
}
