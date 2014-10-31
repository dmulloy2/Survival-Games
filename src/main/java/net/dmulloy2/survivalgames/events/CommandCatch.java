package net.dmulloy2.survivalgames.events;

import net.dmulloy2.survivalgames.SurvivalGames;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandCatch implements Listener {
    private final SurvivalGames plugin;

    public CommandCatch(SurvivalGames plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        String m = event.getMessage();

        if (!plugin.getGameHandler().isPlayerActive(event.getPlayer()) && !plugin.getGameHandler().isPlayerInactive(event.getPlayer()) && !plugin.getGameHandler().isSpectator(event.getPlayer())) {
            return;
        }

        if (m.equalsIgnoreCase("/list")) {
            for (String s : plugin.getGameHandler().getStringList(plugin.getGameHandler().getPlayerGameId(event.getPlayer()))) {
                event.getPlayer().sendMessage(s);
            }
            return;
        }
        if (!plugin.getSettingsHandler().getConfig().getBoolean("disallow-commands")) {
            return;
        }

        if (event.getPlayer().isOp() || event.getPlayer().hasPermission("sg.staff.nocmdblock")) {
            return;
        } else if (m.startsWith("/sg") || m.startsWith("/survivalgames") || m.startsWith("/hg") || m.startsWith("/hungergames") || m.startsWith("/msg")) {
            return;
        } else if (plugin.getSettingsHandler().getConfig().getStringList("cmdwhitelist").contains(m)) {
            return;
        }

        event.setCancelled(true);
    }
}
