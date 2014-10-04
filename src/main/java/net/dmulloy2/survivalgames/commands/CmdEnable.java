package net.dmulloy2.survivalgames.commands;

import net.dmulloy2.survivalgames.SurvivalGames;
import net.dmulloy2.survivalgames.types.Game;
import net.dmulloy2.survivalgames.types.Game.GameMode;
import net.dmulloy2.survivalgames.types.Permission;
import net.dmulloy2.survivalgames.types.Prefix;

/**
 * @author dmulloy2
 */

public class CmdEnable extends SurvivalGamesCommand {
    public CmdEnable(SurvivalGames plugin) {
        super(plugin);
        this.name = "enable";
        this.optionalArgs.add("id");
        this.description = "Enables arena <id>";

        this.permission = Permission.STAFF_ENABLE;
    }

    @Override
    public void perform() {
        try {
            if (args.length == 0) {
                for (Game g : gameManager.getGames()) {
                    if (g.getMode() == GameMode.DISABLED)
                        g.enable();
                }

                plugin.getMessageHandler().sendFMessage(Prefix.INFO, "game.all", player, "input-enabled");
            } else {
                gameManager.enableGame(Integer.parseInt(args[0]));
                plugin.getMessageHandler().sendFMessage(Prefix.INFO, "game.state", player, "arena-" + args[0], "input-enabled");
            }
        } catch (NumberFormatException e) {
            plugin.getMessageHandler().sendFMessage(Prefix.ERROR, "error.notanumber", player, "input-Arena");
        } catch (NullPointerException e) {
            plugin.getMessageHandler().sendFMessage(Prefix.ERROR, "error.gameDoesNotExist", player, "arena-" + args[0]);
        }
    }
}
