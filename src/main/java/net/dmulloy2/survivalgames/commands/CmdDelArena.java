package net.dmulloy2.survivalgames.commands;

import net.dmulloy2.survivalgames.SurvivalGames;
import net.dmulloy2.survivalgames.types.Game;
import net.dmulloy2.survivalgames.types.Permission;
import net.dmulloy2.survivalgames.types.Prefix;

import org.bukkit.configuration.file.FileConfiguration;

/**
 * @author dmulloy2
 */

public class CmdDelArena extends SurvivalGamesCommand {
    public CmdDelArena(SurvivalGames plugin) {
        super(plugin);
        this.name = "delarena";
        this.description = "Delete an arena";

        this.permission = Permission.ADMIN_DELARENA;
    }

    @Override
    public void perform() {
        FileConfiguration s = plugin.getSettingsManager().getSystemConfig();

        int arena = Integer.parseInt(args[0]);
        Game g = gameManager.getGame(arena);

        if (g == null) {
            sendFMessage(Prefix.ERROR, "error.gameDoesNotExist", "arena-" + args[0]);
            return;
        }

        g.disable();
        s.set("sg-system.arenas." + arena + ".enabled", false);
        s.set("sg-system.arenano", s.getInt("sg-system.arenano") - 1);

        sendFMessage(Prefix.INFO, "info.deleted", "input-Arena");
        plugin.getSettingsManager().saveSystemConfig();
        gameManager.hotRemoveArena(arena);

        plugin.getLobbyManager().removeSignsForArena(arena);
    }
}
