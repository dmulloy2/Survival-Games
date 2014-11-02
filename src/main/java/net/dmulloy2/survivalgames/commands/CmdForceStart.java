package net.dmulloy2.survivalgames.commands;

import net.dmulloy2.survivalgames.SurvivalGames;
import net.dmulloy2.survivalgames.types.Game;
import net.dmulloy2.survivalgames.types.Permission;
import net.dmulloy2.survivalgames.types.Prefix;

/**
 * @author dmulloy2
 */

public class CmdForceStart extends SurvivalGamesCommand {
    public CmdForceStart(SurvivalGames plugin) {
        super(plugin);
        this.name = "forcestart";
        this.aliases.add("fs");
        this.description = "Forces the game to start";

        this.permission = Permission.ADMIN_FORCESTART;
    }

    @Override
    public void perform() {
        int gameId = -1;
        int seconds = 10;

        if (args.length == 2) {
            seconds = Integer.parseInt(args[1]);
        } else if (args.length >= 1) {
            gameId = Integer.parseInt(args[0]);
        } else {
            gameId = gameHandler.getGameId(player);
        }

        Game game = gameHandler.getGame(player);
        if (game == null) {
            plugin.getMessageHandler().sendFMessage(Prefix.ERROR, "error.notingame", player);
            return;
        }

        if (game.getActivePlayers() < 2) {
            plugin.getMessageHandler().sendFMessage(Prefix.ERROR, "error.notenoughtplayers", player);
            return;
        }

        if (game.getMode() != Game.GameMode.WAITING && !player.hasPermission("sg.admin.restart")) {
            plugin.getMessageHandler().sendFMessage(Prefix.ERROR, "error.alreadyingame", player);
            return;
        }

        game.countdown(seconds);
        plugin.getMessageHandler().sendFMessage(Prefix.INFO, "game.started", player, "arena-" + gameId);
    }
}
