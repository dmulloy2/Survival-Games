package net.dmulloy2.survivalgames.commands;

import net.dmulloy2.survivalgames.SurvivalGames;
import net.dmulloy2.survivalgames.managers.MessageManager;
import net.dmulloy2.survivalgames.types.Permission;

/**
 * @author dmulloy2
 */

public class CmdResetSpawns extends SurvivalGamesCommand
{
	public CmdResetSpawns(SurvivalGames plugin)
	{
		super(plugin);
		this.name = "resetspawns";
		this.requiredArgs.add("id");
		this.description = "Resets spawns for Arena <id>";
		
		this.permission = Permission.ADMIN_RESETSPAWNS;
	}

	@Override
	public void perform()
	{
		try
		{
			plugin.getSettingsManager().getSpawns().set("spawns." + Integer.parseInt(args[0]), null);
			messageManager.sendMessage(MessageManager.PrefixType.INFO, "&aSpawns reset for arena &e" + args[0], player);
		}
		catch (NumberFormatException e)
		{
			messageManager.sendFMessage(MessageManager.PrefixType.ERROR, "error.notanumber", player, "input-Arena");
		}
		catch (NullPointerException e)
		{
			messageManager.sendMessage(MessageManager.PrefixType.ERROR, "error.gamenotexist", player);
		}
	}
}