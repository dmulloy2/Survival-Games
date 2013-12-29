package net.dmulloy2.survivalgames.commands;

import net.dmulloy2.survivalgames.SurvivalGames;
import net.dmulloy2.survivalgames.types.Permission;
import net.dmulloy2.survivalgames.types.Prefix;

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
			plugin.getMessageHandler().sendMessage(Prefix.INFO, "&aSpawns reset for arena &e" + args[0], player);
		}
		catch (NumberFormatException e)
		{
			plugin.getMessageHandler().sendFMessage(Prefix.ERROR, "error.notanumber", player, "input-Arena");
		}
		catch (NullPointerException e)
		{
			plugin.getMessageHandler().sendMessage(Prefix.ERROR, "error.gamenotexist", player);
		}
	}
}