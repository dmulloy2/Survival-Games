package net.dmulloy2.survivalgames.events;

import lombok.AllArgsConstructor;
import net.dmulloy2.survivalgames.SurvivalGames;
import net.dmulloy2.survivalgames.types.Game;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

@AllArgsConstructor
public class LogoutEvent implements Listener {
    private final SurvivalGames plugin;

    @EventHandler(priority = EventPriority.MONITOR)
    public void PlayerLoggout(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (player == null)
            return;

        try {
            plugin.getGameHandler().removeFromOtherQueues(player, -1);
        } catch (Throwable ex) {
        }

        int id = plugin.getGameHandler().getPlayerGameId(player);
        if (plugin.getGameHandler().isSpectator(player)) {
            plugin.getGameHandler().removeSpectator(player);
        }

        if (id == -1)
            return;

        if (plugin.getGameHandler().getGameMode(id) == Game.GameMode.INGAME) {
            plugin.getGameHandler().getGame(id).killPlayer(player, true);
        } else {
            plugin.getGameHandler().getGame(id).removePlayer(player, true);
        }
    }
}
