package net.dmulloy2.survivalgames.commands;

import java.util.HashMap;

import net.dmulloy2.survivalgames.SurvivalGames;
import net.dmulloy2.survivalgames.types.Game;
import net.dmulloy2.survivalgames.types.Permission;
import net.dmulloy2.survivalgames.types.Prefix;

/**
 * @author dmulloy2
 */

public class CmdFlag extends SurvivalGamesCommand {
    public CmdFlag(SurvivalGames plugin) {
        super(plugin);
        this.name = "flag";
        this.addRequiredArg("id");
        this.addRequiredArg("flag");
        this.addRequiredArg("value");
        this.description = "Modifies an arena-specific setting";

        this.permission = Permission.ADMIN_FLAG;
    }

    @Override
    public void perform() {
        Game g = gameManager.getGame(Integer.parseInt(args[0]));
        if (g == null) {
            plugin.getMessageHandler().sendFMessage(Prefix.ERROR, "error.gameDoesNotExist", player, "arena-" + args[0]);
            return;
        }

        HashMap<String, Object> z = plugin.getSettingsManager().getGameFlags(g.getID());
        z.put(args[1].toUpperCase(), args[2]);
        plugin.getSettingsManager().saveGameFlags(z, g.getID());
    }
}
