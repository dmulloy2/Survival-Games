package net.dmulloy2.survivalgames.commands;

import net.dmulloy2.survivalgames.SurvivalGames;
import net.dmulloy2.survivalgames.types.Permission;

/**
 * @author dmulloy2
 */

public class CmdCreateArena extends SurvivalGamesCommand {
    public CmdCreateArena(SurvivalGames plugin) {
        super(plugin);
        this.name = "createarena";
        this.description = "Create a new arena with the current WorldEdit selection";

        this.permission = Permission.ADMIN_CREATEARENA;
    }

    @Override
    public void perform() {
        gameHandler.createArenaFromSelection(player);
    }
}
