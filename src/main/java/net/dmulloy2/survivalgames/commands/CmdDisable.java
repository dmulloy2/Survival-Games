package net.dmulloy2.survivalgames.commands;

import net.dmulloy2.survivalgames.SurvivalGames;
import net.dmulloy2.survivalgames.types.Game;
import net.dmulloy2.survivalgames.types.Permission;
import net.dmulloy2.survivalgames.types.Prefix;

/**
 * @author dmulloy2
 */

public class CmdDisable extends SurvivalGamesCommand
{
	public CmdDisable(SurvivalGames plugin)
	{
		super(plugin);
		this.name = "disable";
		this.optionalArgs.add("id");
		this.description = "Disables arena <id>";
		
		this.permission = Permission.STAFF_DISABLE;
	}

	@Override
	public void perform()
	{
		try
		{
			if (args.length == 0)
			{
				for (Game g : gameManager.getGames())
				{
					g.disable();
				}
				
				plugin.getMessageHandler().sendFMessage(Prefix.INFO, "game.all", player, "input-disabled");
			}
			else
			{
				gameManager.disableGame(Integer.parseInt(args[0]));
				plugin.getMessageHandler().sendFMessage(Prefix.INFO, "game.state", player, "arena-" + args[0], "input-disabled");
			}
		}
		catch (NumberFormatException e)
		{
			plugin.getMessageHandler().sendFMessage(Prefix.ERROR, "error.notanumber", player, "input-Arena");
		}
		catch (NullPointerException e)
		{
			plugin.getMessageHandler().sendFMessage(Prefix.ERROR, "error.gamedoesntexist", player, "arena-" + args[0]);
		}
	}
}