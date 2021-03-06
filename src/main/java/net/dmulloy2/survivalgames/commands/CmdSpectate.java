package net.dmulloy2.survivalgames.commands;

import net.dmulloy2.survivalgames.SurvivalGames;
import net.dmulloy2.survivalgames.types.Permission;
import net.dmulloy2.survivalgames.types.Prefix;

/**
 * @author dmulloy2
 */

public class CmdSpectate extends SurvivalGamesCommand {
    public CmdSpectate(SurvivalGames plugin) {
        super(plugin);
        this.name = "spectate";
        this.addOptionalArg("id");
        this.description = "Spectate a running arena";

        this.permission = Permission.PLAYER_SPECTATE;
    }

    @Override
    public void perform() {
        if (args.length == 0) {
            if (gameHandler.isSpectator(player)) {
                gameHandler.removeSpectator(player);
                return;
            }

            plugin.getMessageHandler().sendFMessage(Prefix.ERROR, "error.notspecified", player, "input-Game ID");
            return;
        }

        if (plugin.getSettingsHandler().getSpawnCount(Integer.parseInt(args[0])) == 0) {
            plugin.getMessageHandler().sendFMessage(Prefix.ERROR, "error.nospawns", player);
            return;
        }

        if (gameHandler.isPlayerActive(player)) {
            plugin.getMessageHandler().sendFMessage(Prefix.ERROR, "error.specingame", player);
            return;
        }

        gameHandler.getGame(Integer.parseInt(args[0])).addSpectator(player);
    }
}
