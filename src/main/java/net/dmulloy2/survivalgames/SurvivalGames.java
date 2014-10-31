package net.dmulloy2.survivalgames;

import java.util.logging.Level;

import lombok.Getter;
import net.dmulloy2.SwornPlugin;
import net.dmulloy2.commands.CmdHelp;
import net.dmulloy2.handlers.CommandHandler;
import net.dmulloy2.handlers.LogHandler;
import net.dmulloy2.handlers.PermissionHandler;
import net.dmulloy2.survivalgames.commands.CmdAddWall;
import net.dmulloy2.survivalgames.commands.CmdCreateArena;
import net.dmulloy2.survivalgames.commands.CmdDelArena;
import net.dmulloy2.survivalgames.commands.CmdDisable;
import net.dmulloy2.survivalgames.commands.CmdEnable;
import net.dmulloy2.survivalgames.commands.CmdForceStart;
import net.dmulloy2.survivalgames.commands.CmdJoin;
import net.dmulloy2.survivalgames.commands.CmdLeave;
import net.dmulloy2.survivalgames.commands.CmdLeaveQueue;
import net.dmulloy2.survivalgames.commands.CmdList;
import net.dmulloy2.survivalgames.commands.CmdListArenas;
import net.dmulloy2.survivalgames.commands.CmdReload;
import net.dmulloy2.survivalgames.commands.CmdResetSpawns;
import net.dmulloy2.survivalgames.commands.CmdSetLobbySpawn;
import net.dmulloy2.survivalgames.commands.CmdSetSpawn;
import net.dmulloy2.survivalgames.commands.CmdSpectate;
import net.dmulloy2.survivalgames.commands.CmdTeleport;
import net.dmulloy2.survivalgames.commands.CmdVersion;
import net.dmulloy2.survivalgames.commands.CmdVote;
import net.dmulloy2.survivalgames.events.BandageUse;
import net.dmulloy2.survivalgames.events.BreakEvent;
import net.dmulloy2.survivalgames.events.ChestReplaceEvent;
import net.dmulloy2.survivalgames.events.CommandCatch;
import net.dmulloy2.survivalgames.events.DeathEvent;
import net.dmulloy2.survivalgames.events.JoinEvent;
import net.dmulloy2.survivalgames.events.KeepLobbyLoadedEvent;
import net.dmulloy2.survivalgames.events.KitEvents;
import net.dmulloy2.survivalgames.events.LogoutEvent;
import net.dmulloy2.survivalgames.events.MoveEvent;
import net.dmulloy2.survivalgames.events.PlaceEvent;
import net.dmulloy2.survivalgames.events.SignClickEvent;
import net.dmulloy2.survivalgames.events.SpectatorEvents;
import net.dmulloy2.survivalgames.events.TeleportEvent;
import net.dmulloy2.survivalgames.handlers.DatabaseHandler;
import net.dmulloy2.survivalgames.handlers.EconomyHandler;
import net.dmulloy2.survivalgames.handlers.GameHandler;
import net.dmulloy2.survivalgames.handlers.LobbyHandler;
import net.dmulloy2.survivalgames.handlers.LoggingHandler;
import net.dmulloy2.survivalgames.handlers.MessageHandler;
import net.dmulloy2.survivalgames.handlers.QueueHandler;
import net.dmulloy2.survivalgames.handlers.SettingsHandler;
import net.dmulloy2.survivalgames.hooks.HookHandler;
import net.dmulloy2.survivalgames.stats.StatsHandler;
import net.dmulloy2.survivalgames.types.Game;
import net.dmulloy2.survivalgames.util.ChestRatioStorage;
import net.dmulloy2.types.Reloadable;
import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.Util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;

/**
 * This plugin was forked from Double0Negative's original SurvivalGames plugin:
 * https://github.com/Double0negative/Survival-Games
 * <p>
 * The goal of this SurvivalGames fork is to fix a multitude of bugs, massive
 * code cleanup and optimization, and provide support for future Minecraft and
 * Bukkit updates.
 * <p>
 * At the time of the fork, there was no license attached to the GitHub
 * repository linked above.
 * <p>
 * Currently maintained by dmulloy2
 */

public class SurvivalGames extends SwornPlugin implements Reloadable {
    private @Getter GameHandler gameHandler;
    private @Getter HookHandler hookHandler;
    private @Getter StatsHandler statsHandler;
    private @Getter LobbyHandler lobbyHandler;
    private @Getter QueueHandler queueHandler;
    private @Getter MessageHandler messageHandler;
    private @Getter EconomyHandler economyHandler;
    private @Getter LoggingHandler loggingHandler;
    private @Getter DatabaseHandler databaseHandler;
    private @Getter SettingsHandler settingsHandler;
    private @Getter ChestRatioStorage chestRatioStorage;

    private @Getter WorldEditPlugin worldEdit;

    private @Getter boolean disabling;
    private @Getter String prefix = FormatUtil.format("&4[&6&lSG&4]&3 ");

    @Override
    public void onEnable() {
        long start = System.currentTimeMillis();

        // Register log handler first
        logHandler = new LogHandler(this);

        // Register some Handlers
        settingsHandler = new SettingsHandler(this);
        statsHandler = new StatsHandler(this);
        lobbyHandler = new LobbyHandler(this);
        gameHandler = new GameHandler(this);

        permissionHandler = new PermissionHandler(this);
        commandHandler = new CommandHandler(this);
        messageHandler = new MessageHandler(this);

        // Register commands
        commandHandler.setCommandPrefix("survivalgames");
        commandHandler.registerCommand(new CmdAddWall(this));
        commandHandler.registerCommand(new CmdCreateArena(this));
        commandHandler.registerCommand(new CmdDelArena(this));
        commandHandler.registerCommand(new CmdDisable(this));
        commandHandler.registerCommand(new CmdEnable(this));
        commandHandler.registerCommand(new CmdForceStart(this));
        commandHandler.registerCommand(new CmdHelp(this));
        commandHandler.registerCommand(new CmdJoin(this));
        commandHandler.registerCommand(new CmdLeave(this));
        commandHandler.registerCommand(new CmdLeaveQueue(this));
        commandHandler.registerCommand(new CmdList(this));
        commandHandler.registerCommand(new CmdListArenas(this));
        commandHandler.registerCommand(new CmdReload(this));
        commandHandler.registerCommand(new CmdResetSpawns(this));
        commandHandler.registerCommand(new CmdSetLobbySpawn(this));
        commandHandler.registerCommand(new CmdSetSpawn(this));
        commandHandler.registerCommand(new CmdSpectate(this));
        commandHandler.registerCommand(new CmdTeleport(this));
        commandHandler.registerCommand(new CmdVersion(this));
        commandHandler.registerCommand(new CmdVote(this));

        // Hook into WorldEdit
        worldEdit = hookIntoWorldEdit();
        if (worldEdit == null) {
            log(Level.WARNING, "WorldEdit plugin not found. You will not be able to create new arenas.");
        }

        // Try loading everything that uses SQL

        try {
            FileConfiguration c = settingsHandler.getConfig();
            if (c.getBoolean("stats.enabled"))
                databaseHandler = new DatabaseHandler(this);

            queueHandler = new QueueHandler(this);

            statsHandler.setup(c.getBoolean("stats.enabled"));
        } catch (Throwable ex) {
            log(Level.SEVERE, Util.getUsefulStack(ex, "connecting to the database. Check your settings!"));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        chestRatioStorage = new ChestRatioStorage(this);
        economyHandler = new EconomyHandler(this);
        hookHandler = new HookHandler(this);

        // Register events
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlaceEvent(this), this);
        pm.registerEvents(new BreakEvent(this), this);
        pm.registerEvents(new DeathEvent(this), this);
        pm.registerEvents(new MoveEvent(this), this);
        pm.registerEvents(new CommandCatch(this), this);
        pm.registerEvents(new SignClickEvent(this), this);
        pm.registerEvents(new ChestReplaceEvent(this), this);
        pm.registerEvents(new LogoutEvent(this), this);
        pm.registerEvents(new JoinEvent(this), this);
        pm.registerEvents(new TeleportEvent(this), this);

        loggingHandler = new LoggingHandler(this);
        pm.registerEvents(loggingHandler, this);

        pm.registerEvents(new SpectatorEvents(this), this);
        pm.registerEvents(new BandageUse(this), this);
        pm.registerEvents(new KitEvents(this), this);
        pm.registerEvents(new KeepLobbyLoadedEvent(this), this);

        for (Player pl : getServer().getOnlinePlayers()) {
            if (gameHandler.getBlockGameId(pl.getLocation()) != -1) {
                pl.teleport(settingsHandler.getLobbySpawn());
            }
        }

        log("{0} has been enabled. Took {1} ms.", getDescription().getFullName(), System.currentTimeMillis() - start);
    }

    @Override
    public void onDisable() {
        long start = System.currentTimeMillis();

        disabling = true;

        getServer().getScheduler().cancelTasks(this);

        settingsHandler.saveSpawns();
        settingsHandler.saveSystemConfig();

        for (Game g : gameHandler.getGames()) {
            g.disable();
        }

        log("{0} has been disabled. Took {1} ms.", getDescription().getFullName(), System.currentTimeMillis() - start);
    }

    public WorldEditPlugin hookIntoWorldEdit() {
        PluginManager pm = getServer().getPluginManager();
        if (pm.isPluginEnabled("WorldEdit")) {
            Plugin worldEdit = pm.getPlugin("WorldEdit");
            if (worldEdit instanceof WorldEditPlugin) {
                return (WorldEditPlugin) worldEdit;
            }
        }

        return null;
    }

    public void log(Level level, String msg, Object... obj) {
        logHandler.log(level, msg, obj);
    }

    public void log(String msg, Object... obj) {
        logHandler.log(Level.INFO, msg, obj);
    }

    public void debug(String msg, Object... obj) {
        logHandler.debug(msg, obj);
    }

    public void debug(int a) {
        debug(String.valueOf(a));
    }

    @Override
    public void reload() {
        // TODO: Reload properly
        PluginManager pm = getServer().getPluginManager();
        pm.disablePlugin(this);
        pm.enablePlugin(this);
    }
}
