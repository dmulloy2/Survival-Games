package net.dmulloy2.survivalgames.commands;

import net.dmulloy2.survivalgames.SurvivalGames;
import net.dmulloy2.survivalgames.managers.MessageManager;
import net.dmulloy2.survivalgames.types.Permission;

/**
 * @author dmulloy2
 */

public class CmdList extends SurvivalGamesCommand
{
	public CmdList(SurvivalGames plugin)
	{
		super(plugin);
		this.name = "list";
		this.description = "List all players in the arena you are playing in";
		
		this.permission = Permission.PLAYER_LIST;
	}

	@Override
	public void perform()
	{
		int gid = 0;
		try
		{
			if (args.length == 0)
			{
				gid = gameManager.getPlayerGameId(player);
			}
			else
			{
				gid = Integer.parseInt(args[0]);
			}

			for (String s : gameManager.getStringList(gid))
			{
				player.sendMessage(s);
			}
		}
		catch (NumberFormatException ex)
		{
			messageManager.sendFMessage(MessageManager.PrefixType.ERROR, "error.notanumber", player, "input-Arena");
		}
		catch (NullPointerException ex)
		{
			messageManager.sendFMessage(MessageManager.PrefixType.ERROR, "error.gamenotexist", player);
		}
	}
}