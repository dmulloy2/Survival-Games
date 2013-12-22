package net.dmulloy2.survivalgames.handlers;

import java.util.ArrayList;
import java.util.List;

import net.dmulloy2.survivalgames.SurvivalGames;
import net.dmulloy2.survivalgames.commands.CmdHelp;
import net.dmulloy2.survivalgames.commands.SurvivalGamesCommand;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * @author dmulloy2
 */

public class CommandHandler implements CommandExecutor
{
	private final SurvivalGames plugin;
	private String commandPrefix;
	private List<SurvivalGamesCommand> registeredCommands;

	public CommandHandler(final SurvivalGames plugin)
	{
		this.plugin = plugin;
		registeredCommands = new ArrayList<SurvivalGamesCommand>();
	}

	public void registerCommand(SurvivalGamesCommand command)
	{
		if (commandPrefix != null)
			registeredCommands.add(command);
	}

	public List<SurvivalGamesCommand> getRegisteredCommands()
	{
		return registeredCommands;
	}

	public String getCommandPrefix()
	{
		return commandPrefix;
	}

	public void setCommandPrefix(String commandPrefix)
	{
		this.commandPrefix = commandPrefix;
		plugin.getCommand(commandPrefix).setExecutor(this);
	}

	public boolean usesCommandPrefix()
	{
		return commandPrefix != null;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		List<String> argsList = new ArrayList<String>();

		if (args.length > 0)
		{
			String commandName = args[0];
			for (int i = 1; i < args.length; i++)
				argsList.add(args[i]);

			for (SurvivalGamesCommand command : registeredCommands)
			{
				if (commandName.equalsIgnoreCase(command.getName()) || command.getAliases().contains(commandName.toLowerCase()))
				{
					command.execute(sender, argsList.toArray(new String[0]));
					return true;
				}
			}

			sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "Unknown SurvivalGames command \"" + args[0] + "\". Try /hg help!");
		}
		else
		{
			new CmdHelp(plugin).execute(sender, args);
		}

		return true;
	}
}