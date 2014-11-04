package net.dmulloy2.survivalgames.commands;

import net.dmulloy2.survivalgames.SurvivalGames;
import net.dmulloy2.survivalgames.types.Game;
import net.dmulloy2.survivalgames.types.Permission;
import net.dmulloy2.survivalgames.types.Prefix;
import net.dmulloy2.util.NumberUtil;

import org.bukkit.configuration.file.FileConfiguration;

/**
 * @author dmulloy2
 */

public class CmdDelArena extends SurvivalGamesCommand {
    public CmdDelArena(SurvivalGames plugin) {
        super(plugin);
        this.name = "delarena";
        this.aliases.add("delete");
        this.addRequiredArg("id");
        this.description = "Delete an arena";
        this.permission = Permission.ADMIN_DELARENA;
    }

    @Override
    public void perform() {
        int id = NumberUtil.toInt(args[0]);
        if (id == -1) {
            err("&c{0} &4is not a number!", args[0]);
            return;
        }

        Game game = gameHandler.getGame(id);
        if (game == null) {
            sendFMessage(Prefix.ERROR, "error.gameDoesNotExist", "arena-" + args[0]);
            return;
        }

        game.disable();

        FileConfiguration s = plugin.getSettingsHandler().getSystemConfig();
        s.set("sg-system.arenas." + id + ".enabled", false);
        s.set("sg-system.arenano", s.getInt("sg-system.arenano") - 1);

        plugin.getSettingsHandler().saveSystemConfig();
        gameHandler.hotRemoveArena(id);

        plugin.getLobbyHandler().removeSignsForArena(id);

        sendFMessage(Prefix.INFO, "info.deleted", "input-Arena");
    }
}
