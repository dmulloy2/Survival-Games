package net.dmulloy2.survivalgames.events;

import java.util.HashMap;
import java.util.Map;

import net.dmulloy2.survivalgames.SurvivalGames;
import net.dmulloy2.survivalgames.types.Game;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class MoveEvent implements Listener {
    private final Map<String, Vector> positions;
    private final SurvivalGames plugin;

    public MoveEvent(SurvivalGames plugin) {
        this.plugin = plugin;
        this.positions = new HashMap<>();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Game game = plugin.getGameHandler().getGame(player);
        if (game == null) {
            positions.remove(player.getName());
            return;
        }

        Game.GameMode mode = game.getGameMode();
        if (mode == Game.GameMode.INGAME) {
            return;
        }

        Location location = player.getLocation();
        if (positions.get(player.getName()) == null) {
            positions.put(player.getName(), location.toVector());
            return;
        }

        Vector vector = positions.get(player.getName());
        if (location.getBlockX() != vector.getBlockX() || location.getBlockZ() != vector.getBlockZ()) {
            location.setX(vector.getBlockX() + 0.5D);
            location.setZ(vector.getBlockZ() + 0.5D);
            player.teleport(location);
        }
    }
}
