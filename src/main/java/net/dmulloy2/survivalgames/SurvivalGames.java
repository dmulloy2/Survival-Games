package net.dmulloy2.survivalgames;

import java.io.IOException;
import java.util.logging.Level;

import lombok.Getter;
import net.dmulloy2.survivalgames.commands.CmdAddWall;
import net.dmulloy2.survivalgames.commands.CmdCreateArena;
import net.dmulloy2.survivalgames.commands.CmdDelArena;
import net.dmulloy2.survivalgames.commands.CmdDisable;
import net.dmulloy2.survivalgames.commands.CmdEnable;
import net.dmulloy2.survivalgames.commands.CmdForceStart;
import net.dmulloy2.survivalgames.commands.CmdHelp;
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
import net.dmulloy2.survivalgames.handlers.CommandHandler;
import net.dmulloy2.survivalgames.handlers.LogHandler;
import net.dmulloy2.survivalgames.handlers.PermissionHandler;
import net.dmulloy2.survivalgames.hooks.HookManager;
import net.dmulloy2.survivalgames.logging.LoggingManager;
import net.dmulloy2.survivalgames.logging.QueueManager;
import net.dmulloy2.survivalgames.managers.DatabaseManager;
import net.dmulloy2.survivalgames.managers.GameManager;
import net.dmulloy2.survivalgames.managers.LobbyManager;
import net.dmulloy2.survivalgames.managers.MessageManager;
import net.dmulloy2.survivalgames.managers.SettingsManager;
import net.dmulloy2.survivalgames.metrics.Metrics;
import net.dmulloy2.survivalgames.stats.StatsManager;
import net.dmulloy2.survivalgames.types.Game;
import net.dmulloy2.survivalgames.util.ChestRatioStorage;
import net.dmulloy2.survivalgames.util.FormatUtil;
import net.dmulloy2.survivalgames.util.MessageUtil;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;

public class SurvivalGames extends JavaPlugin
{
	private @Getter boolean disabling = false;
	private @Getter boolean dbcon = false;
	private @Getter boolean configUpToDate = false;
	private @Getter int configVersion = 3;

	private @Getter GameManager gameManager;
	private @Getter	HookManager hookManager;
	private @Getter	StatsManager statsManager;
	private @Getter	LobbyManager lobbyManager;
	private @Getter	QueueManager queueManager;
	private @Getter	LoggingManager loggingManager;
	private @Getter	MessageManager messageManager;
	private @Getter DatabaseManager databaseManager;
	private @Getter	SettingsManager settingsManager;
	private @Getter	ChestRatioStorage chestRatioStorage;

	private @Getter	PermissionHandler permissionHandler;
	private @Getter	CommandHandler commandHandler;
	private @Getter	LogHandler logHandler;
	
	private @Getter WorldEditPlugin worldEdit;

	private @Getter	String prefix = FormatUtil.format("&4[&6&lSG&4]&3 ");

	@Override
	public void onEnable()
	{
		long start = System.currentTimeMillis();

		settingsManager = new SettingsManager(this);
		messageManager = new MessageManager(this);
		statsManager = new StatsManager(this);
		gameManager = new GameManager(this);

		permissionHandler = new PermissionHandler(this);
		commandHandler = new CommandHandler(this);
		logHandler = new LogHandler(this);
		
		new MessageUtil(this); // Initialize message util

		commandHandler.setCommandPrefix("survivalgames");
		commandHandler.registerCommand(new CmdAddWall(this));
		commandHandler.registerCommand(new CmdCreateArena(this));
		commandHandler.registerCommand(new CmdDelArena(this));
		commandHandler.registerCommand(new CmdDisable(this));
		commandHandler.registerCommand(new CmdEnable(this));
//		commandHandler.registerCommand(new CmdFlag(this));
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
		
		worldEdit = hookIntoWorldEdit();
		if (worldEdit == null)
		{
			$(Level.WARNING, "WorldEdit plugin not found. You will not be able to create new arenas.");
		}

		// Try loading everything that uses SQL
		try
		{
			FileConfiguration c = settingsManager.getConfig();
			if (c.getBoolean("stats.enabled"))
				databaseManager = new DatabaseManager(this);

			queueManager = new QueueManager(this);

			statsManager.setup(c.getBoolean("stats.enabled"));
			dbcon = true;
		}
		catch (Exception e)
		{
			$(Level.SEVERE, "Could not connect to the database ({0}). Check your settings!", e.getMessage());
			
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		chestRatioStorage = new ChestRatioStorage(this);
		lobbyManager = new LobbyManager(this);
		hookManager = new HookManager(this);

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

		loggingManager = new LoggingManager(this);
		pm.registerEvents(loggingManager, this);
		pm.registerEvents(new SpectatorEvents(this), this);
		pm.registerEvents(new BandageUse(this), this);
		pm.registerEvents(new KitEvents(this), this);
		pm.registerEvents(new KeepLobbyLoadedEvent(this), this);

		for (Player pl : getServer().getOnlinePlayers())
		{
			if (gameManager.getBlockGameId(pl.getLocation()) != -1)
			{
				pl.teleport(settingsManager.getLobbySpawn());
			}
		}
		
		try
		{
			new Metrics(this).start();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		$("{0} has been enabled ({1}ms)", getDescription().getFullName(), System.currentTimeMillis() - start);
	}

	@Override
	public void onDisable()
	{
		long start = System.currentTimeMillis();

		this.disabling = true;

		settingsManager.saveSpawns();
		settingsManager.saveSystemConfig();
		
		for (Game g : gameManager.getGames())
		{
			g.disable();

			queueManager.rollback(g.getID(), true);
		}

		$("{0} has been disabled ({1}ms)", getDescription().getFullName(), System.currentTimeMillis() - start);
	}
	
	public WorldEditPlugin hookIntoWorldEdit()
	{
		PluginManager pm = getServer().getPluginManager();
		if (pm.isPluginEnabled("WorldEdit"))
		{
			Plugin worldEdit = pm.getPlugin("WorldEdit");
			if (worldEdit instanceof WorldEditPlugin)
			{
				return (WorldEditPlugin) worldEdit;
			}
		}
		
		return null;
	}
	
	public void $(Level l, String msg, Object... obj)
	{
		logHandler.log(l, msg, obj);
	}

	public void $(String msg, Object... obj)
	{
		logHandler.log(Level.INFO, msg, obj);
	}

	public void debug(String msg, Object... obj)
	{
		logHandler.debug(msg, obj);
	}

	public void debug(int a)
	{
		debug(String.valueOf(a));
	}

	public void setConfigUpToDate(boolean upToDate)
	{
		this.configUpToDate = upToDate;
	}
}