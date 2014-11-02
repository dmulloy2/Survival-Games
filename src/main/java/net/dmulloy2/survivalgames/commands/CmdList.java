package net.dmulloy2.survivalgames.commands;

import net.dmulloy2.survivalgames.SurvivalGames;
import net.dmulloy2.survivalgames.types.Game;
import net.dmulloy2.survivalgames.types.Permission;
import net.dmulloy2.survivalgames.types.Prefix;

/**
 * @author dmulloy2
 */

public class CmdList extends SurvivalGamesCommand {
    public CmdList(SurvivalGames plugin) {
        super(plugin);
        this.name = "list";
        this.description = "List all players in the arena you are playing in";

        this.permission = Permission.PLAYER_LIST;
    }

    @Override
    public void perform() {
        int id = 0;
        try {
            if (args.length == 0) {
                id = gameHandler.getGameId(player);
            } else {
                id = Integer.parseInt(args[0]);
            }

            Game game = gameHandler.getGame(id);
            for (String s : gameHandler.getStringList(game)) {
                player.sendMessage(s);
            }
        } catch (NumberFormatException ex) {
            plugin.getMessageHandler().sendFMessage(Prefix.ERROR, "error.notanumber", player, "input-Arena");
        } catch (NullPointerException ex) {
            plugin.getMessageHandler().sendFMessage(Prefix.ERROR, "error.gameDoesNotExist", player);
        }
    }
}
