package net.dmulloy2.survivalgames.commands;

import net.dmulloy2.survivalgames.SurvivalGames;
import net.dmulloy2.survivalgames.types.Game;
import net.dmulloy2.survivalgames.types.Permission;
import net.dmulloy2.survivalgames.types.Prefix;

/**
 * @author dmulloy2
 */

public class CmdReload extends SurvivalGamesCommand {
    public CmdReload(SurvivalGames plugin) {
        super(plugin);
        this.name = "reload";
        this.aliases.add("rl");
        this.addOptionalArg("type");
        this.description = "reload SurvivalGames";
        this.permission = Permission.ADMIN_RELOAD;
        this.mustBePlayer = false;
    }

    @Override
    public void perform() {
        if (args.length != 1) {
            plugin.getMessageHandler().sendMessage(Prefix.INFO, "Valid reload types <Settings | Games | All>", sender);
            plugin.getMessageHandler().sendMessage(Prefix.INFO, "Settings will reload the settings configs and attempt to reapply them", sender);
            plugin.getMessageHandler().sendMessage(Prefix.INFO, "Games will reload all games currently running", sender);
            plugin.getMessageHandler().sendMessage(Prefix.INFO, "All will attempt to reload the entire plugin", sender);
            return;
        }

        if (args[0].equalsIgnoreCase("settings")) {
            plugin.getSettingsHandler().reloadChest();
            plugin.getSettingsHandler().reloadKits();
            plugin.getSettingsHandler().reloadMessages();
            plugin.getSettingsHandler().reloadSpawns();
            plugin.getSettingsHandler().reloadSystem();
            plugin.getSettingsHandler().reloadConfig();

            for (Game g : gameHandler.getGames()) {
                g.reloadConfig();
            }

            plugin.getMessageHandler().sendMessage(Prefix.INFO, "Settings Reloaded", sender);
        } else if (args[0].equalsIgnoreCase("games")) {
            for (Game g : gameHandler.getGames()) {
                plugin.getQueueHandler().rollback(g.getID());

                g.disable();
                g.enable();
            }

            plugin.getMessageHandler().sendMessage(Prefix.INFO, "Games Reloaded", sender);
        } else if (args[0].equalsIgnoreCase("all")) {
            plugin.reload();

            plugin.getMessageHandler().sendMessage(Prefix.INFO, "Plugin reloaded", sender);
        } else {
            plugin.getMessageHandler().sendMessage(Prefix.INFO, "Valid reload types <Settings | Games | All>", sender);
            plugin.getMessageHandler().sendMessage(Prefix.INFO, "Settings will reload the settings configs and attempt to reapply them", sender);
            plugin.getMessageHandler().sendMessage(Prefix.INFO, "Games will reload all games currently running", sender);
            plugin.getMessageHandler().sendMessage(Prefix.INFO, "All will attempt to reload the entire plugin", sender);
        }
    }
}
