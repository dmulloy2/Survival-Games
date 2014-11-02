package net.dmulloy2.survivalgames.events;

import net.dmulloy2.survivalgames.SurvivalGames;
import net.dmulloy2.survivalgames.types.Game;

import org.bukkit.entity.Player;
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
        if (!plugin.getSettingsHandler().getConfig().getBoolean("disallow-commands")) {
            return;
        }

        Player player = event.getPlayer();
        Game game = plugin.getGameHandler().getGame(player);
        if (game == null) {
            return;
        }

        String message = event.getMessage();
        if (message.toLowerCase().startsWith("/list") || message.toLowerCase().startsWith("/who")) {
            for (String s : plugin.getGameHandler().getStringList(game)) {
                player.sendMessage(s);
            }

            return;
        }


        if (event.getPlayer().isOp() || event.getPlayer().hasPermission("sg.staff.nocmdblock")) {
            return;
        } else if (message.startsWith("/sg") || message.startsWith("/survivalgames") || message.startsWith("/hg")
                || message.startsWith("/hungergames") || message.startsWith("/msg")) {
            return;
        } else if (plugin.getSettingsHandler().getConfig().getStringList("cmdwhitelist").contains(message)) {
            return;
        }

        event.setCancelled(true);
    }
}
