package net.dmulloy2.survivalgames.commands;

import net.dmulloy2.survivalgames.SurvivalGames;
import net.dmulloy2.survivalgames.managers.MessageManager.PrefixType;
import net.dmulloy2.survivalgames.types.Game;
import net.dmulloy2.survivalgames.types.Permission;

/**
 * @author dmulloy2
 */

public class CmdReload extends SurvivalGamesCommand
{
	public CmdReload(SurvivalGames plugin)
	{
		super(plugin);
		this.name = "reload";
		this.aliases.add("rl");
		this.optionalArgs.add("type");
		this.description = "reload SurvivalGames";
		this.permission = Permission.ADMIN_RELOAD;

		this.mustBePlayer = false;
	}

	@Override
	public void perform()
	{
		if (args.length != 1)
		{
			messageManager.sendMessage(PrefixType.INFO, "Valid reload types <Settings | Games | All>", player);
			messageManager.sendMessage(PrefixType.INFO,
					"Settings will reload the settings configs and attempt to reapply them", player);
			messageManager.sendMessage(PrefixType.INFO, "Games will reload all games currently running", player);
			messageManager.sendMessage(PrefixType.INFO, "All will attempt to reload the entire plugin", player);
			return;
		}

		if (args[0].equalsIgnoreCase("settings"))
		{
			plugin.getSettingsManager().reloadChest();
			plugin.getSettingsManager().reloadKits();
			plugin.getSettingsManager().reloadMessages();
			plugin.getSettingsManager().reloadSpawns();
			plugin.getSettingsManager().reloadSystem();
			plugin.getSettingsManager().reloadConfig();
			
			for (Game g : gameManager.getGames())
			{
				g.reloadConfig();
			}
			
			messageManager.sendMessage(PrefixType.INFO, "Settings Reloaded", player);
			return;
		}
		else if (args[0].equalsIgnoreCase("games"))
		{
			for (Game g : gameManager.getGames())
			{
				plugin.getQueueManager().rollback(g.getID(), true);
				
				g.disable();
				g.enable();
			}
			
			messageManager.sendMessage(PrefixType.INFO, "Games Reloaded", player);
			return;
		}
		else if (args[0].equalsIgnoreCase("all"))
		{
			plugin.getServer().getPluginManager().disablePlugin(plugin);
			plugin.getServer().getPluginManager().enablePlugin(plugin);

			messageManager.sendMessage(PrefixType.INFO, "Plugin reloaded", player);
			return;
		}
		else
		{
			messageManager.sendMessage(PrefixType.INFO, "Valid reload types <Settings | Games |All>", player);
			messageManager.sendMessage(PrefixType.INFO,
					"Settings will reload the settings configs and attempt to reapply them", player);
			messageManager.sendMessage(PrefixType.INFO, "Games will reload all games currently running", player);
			messageManager.sendMessage(PrefixType.INFO, "All will attempt to reload the entire plugin", player);
			return;
		}
	}
}