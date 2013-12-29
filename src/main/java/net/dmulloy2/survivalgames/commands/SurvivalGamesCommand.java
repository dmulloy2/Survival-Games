package net.dmulloy2.survivalgames.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import net.dmulloy2.survivalgames.SurvivalGames;
import net.dmulloy2.survivalgames.handlers.MessageHandler;
import net.dmulloy2.survivalgames.managers.GameManager;
import net.dmulloy2.survivalgames.managers.LobbyManager;
import net.dmulloy2.survivalgames.types.Permission;
import net.dmulloy2.survivalgames.types.Prefix;
import net.dmulloy2.survivalgames.util.FormatUtil;
import net.dmulloy2.survivalgames.util.Util;

import org.apache.commons.lang.WordUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public abstract class SurvivalGamesCommand implements CommandExecutor
{
	protected final SurvivalGames plugin;

	protected CommandSender sender;
	protected Player player;
	protected String args[];

	protected String name;
	protected String description;

	protected Permission permission;

	protected boolean mustBePlayer = true;

	protected List<String> requiredArgs;
	protected List<String> optionalArgs;
	protected List<String> aliases;

	protected MessageHandler messageHandler;
	protected LobbyManager lobbyManager;
	protected GameManager gameManager;

	public SurvivalGamesCommand(SurvivalGames plugin)
	{
		this.plugin = plugin;
		
		this.messageHandler = plugin.getMessageHandler();
		this.lobbyManager = plugin.getLobbyManager();
		this.gameManager = plugin.getGameManager();

		this.requiredArgs = new ArrayList<String>(2);
		this.optionalArgs = new ArrayList<String>(2);
		this.aliases = new ArrayList<String>(2);
	}

	@Override
	public final boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		execute(sender, args);
		return true;
	}

	public final void execute(CommandSender sender, String[] args)
	{
		this.sender = sender;
		this.args = args;

		if (sender instanceof Player)
			player = (Player) sender;

		if (mustBePlayer && ! isPlayer())
		{
			err("You must be a player to execute this command!");
			return;
		}

		if (requiredArgs.size() > args.length)
		{
			invalidArgs();
			return;
		}

		if (! hasPermission())
		{
			err("You do not have permission to perform this command!");
			log(Level.WARNING, sender.getName() + " was denied access to a command!");
			return;
		}

		try
		{
			perform();
		}
		catch (Throwable e)
		{
			err("Encountered an exception executing this command: " + e.getMessage());
			plugin.getLogHandler().debug(Util.getUsefulStack(e, "executing command " + name));
		}
	}

	public abstract void perform();

	protected final boolean isPlayer()
	{
		return player != null;
	}

	private final boolean hasPermission()
	{
		return plugin.getPermissionHandler().hasPermission(sender, permission);
	}

	public final String getDescription()
	{
		return FormatUtil.format(description);
	}

	public final List<String> getAliases()
	{
		return aliases;
	}

	public final String getName()
	{
		return name;
	}

	public final String getUsageTemplate(final boolean displayHelp)
	{
		StringBuilder ret = new StringBuilder();
		ret.append("&b/hg ");

		ret.append(name);

		ret.append("&3 ");
		for (String s : requiredArgs)
			ret.append(String.format("<%s> ", s));

		for (String s : optionalArgs)
			ret.append(String.format("[%s] ", s));

		if (displayHelp)
			ret.append("&e" + description);

		return FormatUtil.format(ret.toString());
	}

	protected final void sendMessage(Prefix prefix, String message)
	{
		messageHandler.sendMessage(prefix, message, player);
	}

	protected final void sendMessage(String message)
	{
		sendMessage(Prefix.INFO, message);
	}

	protected final void sendFMessage(Prefix prefix, String message, String... args)
	{
		messageHandler.sendFMessage(prefix, message, player, args);
	}

	protected final void sendFMessage(String message, String... args)
	{
		sendFMessage(Prefix.INFO, message, args);
	}

	protected final void err(String message)
	{
		sendMessage(Prefix.ERROR, message);
	}

	protected final void invalidArgs()
	{
		err("Invalid arguments! Try: " + getUsageTemplate(false));
	}

	protected final void log(Level level, String string, Object... objects)
	{
		plugin.getLogHandler().log(level, string, objects);
	}

	protected final void log(String string, Object... objects)
	{
		plugin.getLogHandler().log(Level.INFO, string, objects);
	}

	protected final void debug(String string, Object... objects)
	{
		plugin.getLogHandler().debug(string, objects);
	}

	protected final String capitalize(String string)
	{
		return WordUtils.capitalize(string);
	}
}