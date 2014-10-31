package net.dmulloy2.survivalgames.commands;

import java.util.Map;

import net.dmulloy2.survivalgames.SurvivalGames;
import net.dmulloy2.survivalgames.types.Game;
import net.dmulloy2.survivalgames.types.Permission;
import net.dmulloy2.survivalgames.types.Prefix;
import net.dmulloy2.util.NumberUtil;

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
        int id = NumberUtil.toInt(args[0]);
        if (id == -1) {
            err("Invalid number: {0}", args[0]);
            return;
        }

        Game game = gameHandler.getGame(id);
        if (game == null) {
            plugin.getMessageHandler().sendFMessage(Prefix.ERROR, "error.gameDoesNotExist", player, "arena-" + id);
            return;
        }

        Map<String, Object> flags = plugin.getSettingsHandler().getGameFlags(id);
        flags.put(args[1].toLowerCase(), args[2]);
        plugin.getSettingsHandler().saveGameFlags(flags, id);
    }
}
