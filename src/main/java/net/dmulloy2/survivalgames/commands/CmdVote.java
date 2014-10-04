package net.dmulloy2.survivalgames.commands;

import net.dmulloy2.survivalgames.SurvivalGames;
import net.dmulloy2.survivalgames.types.Permission;
import net.dmulloy2.survivalgames.types.Prefix;

/**
 * @author dmulloy2
 */

public class CmdVote extends SurvivalGamesCommand {
    public CmdVote(SurvivalGames plugin) {
        super(plugin);
        this.name = "vote";
        this.description = "Votes to start the game";

        this.permission = Permission.PLAYER_VOTE;
    }

    @Override
    public void perform() {
        int game = gameManager.getPlayerGameId(player);
        if (game == -1) {
            plugin.getMessageHandler().sendFMessage(Prefix.ERROR, "error.notinarena", player);
            return;
        }

        gameManager.getGame(gameManager.getPlayerGameId(player)).vote(player);
    }
}
