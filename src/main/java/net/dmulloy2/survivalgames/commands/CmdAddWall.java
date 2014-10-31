package net.dmulloy2.survivalgames.commands;

import net.dmulloy2.survivalgames.SurvivalGames;
import net.dmulloy2.survivalgames.types.Permission;

/**
 * @author dmulloy2
 */

public class CmdAddWall extends SurvivalGamesCommand {
    public CmdAddWall(SurvivalGames plugin) {
        super(plugin);
        this.name = "addwall";
        this.addRequiredArg("arena");
        this.description = "Adds a lobby stats wall for an Arena";

        this.permission = Permission.ADMIN_ADDWALL;
    }

    @Override
    public void perform() {
        lobbyHandler.setLobbySignsFromSelection(player, Integer.parseInt(args[0]));
    }
}
