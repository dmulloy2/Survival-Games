package net.dmulloy2.survivalgames.commands;

import java.util.logging.Level;

import net.dmulloy2.commands.Command;
import net.dmulloy2.survivalgames.SurvivalGames;
import net.dmulloy2.survivalgames.handlers.GameHandler;
import net.dmulloy2.survivalgames.handlers.LobbyHandler;
import net.dmulloy2.survivalgames.handlers.MessageHandler;
import net.dmulloy2.survivalgames.types.Prefix;

import org.apache.commons.lang.WordUtils;

/**
 * @author dmulloy2
 */

public abstract class SurvivalGamesCommand extends Command {
    protected final SurvivalGames plugin;

    protected MessageHandler messageHandler;
    protected LobbyHandler lobbyHandler;
    protected GameHandler gameHandler;

    public SurvivalGamesCommand(SurvivalGames plugin) {
        super(plugin);
        this.plugin = plugin;

        this.messageHandler = plugin.getMessageHandler();
        this.lobbyHandler = plugin.getLobbyHandler();
        this.gameHandler = plugin.getGameHandler();
    }

    protected final void sendMessage(Prefix prefix, String message) {
        messageHandler.sendMessage(prefix, message, player);
    }

    protected final void sendMessage(String message) {
        sendMessage(Prefix.INFO, message);
    }

    protected final void sendFMessage(Prefix prefix, String message, String... args) {
        messageHandler.sendFMessage(prefix, message, player, args);
    }

    protected final void sendFMessage(String message, String... args) {
        sendFMessage(Prefix.INFO, message, args);
    }

    protected final void err(String message) {
        sendMessage(Prefix.ERROR, message);
    }

    protected final void log(Level level, String string, Object... objects) {
        plugin.getLogHandler().log(level, string, objects);
    }

    protected final void log(String string, Object... objects) {
        plugin.getLogHandler().log(Level.INFO, string, objects);
    }

    protected final void debug(String string, Object... objects) {
        plugin.getLogHandler().debug(string, objects);
    }

    protected final String capitalize(String string) {
        return WordUtils.capitalize(string);
    }
}
