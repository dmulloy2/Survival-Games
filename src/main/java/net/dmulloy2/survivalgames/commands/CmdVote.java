package net.dmulloy2.survivalgames.commands;

import net.dmulloy2.survivalgames.SurvivalGames;
import net.dmulloy2.survivalgames.types.Game;
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
        Game game = gameHandler.getGame(player);
        if (game == null) {
            plugin.getMessageHandler().sendFMessage(Prefix.ERROR, "error.notinarena", player);
            return;
        }

        game.vote(player);
    }
}
