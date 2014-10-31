package net.dmulloy2.survivalgames.commands;

import net.dmulloy2.survivalgames.SurvivalGames;
import net.dmulloy2.survivalgames.types.Permission;
import net.dmulloy2.survivalgames.types.Prefix;

/**
 * @author dmulloy2
 */

public class CmdJoin extends SurvivalGamesCommand {
    public CmdJoin(SurvivalGames plugin) {
        super(plugin);
        this.name = "join";
        this.aliases.add("j");
        this.addOptionalArg("arena");
        this.description = "Join the lobby";

        this.permission = Permission.PLAYER_JOIN;
    }

    @Override
    public void perform() {
        if (args.length == 1) {
            try {
                int a = Integer.parseInt(args[0]);
                gameManager.addPlayer(player, a);
            } catch (NumberFormatException e) {
                plugin.getMessageHandler().sendFMessage(Prefix.ERROR, "error.notanumber", player, "input-" + args[0]);
            }
        } else {
            if (plugin.getPermissionHandler().hasPermission(player, Permission.PLAYER_JOIN_LOBBY)) {
                if (gameManager.getPlayerGameId(player) != -1) {
                    plugin.getMessageHandler().sendFMessage(Prefix.ERROR, "error.alreadyingame", player);
                    return;
                }

                player.teleport(plugin.getSettingsManager().getLobbySpawn());
            } else {
                plugin.getMessageHandler().sendFMessage(Prefix.WARNING, "error.nopermission", player);
            }
        }
    }
}
