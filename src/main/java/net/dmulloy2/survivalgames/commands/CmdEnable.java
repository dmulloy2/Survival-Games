package net.dmulloy2.survivalgames.commands;

import net.dmulloy2.survivalgames.SurvivalGames;
import net.dmulloy2.survivalgames.managers.MessageManager;
import net.dmulloy2.survivalgames.types.Game;
import net.dmulloy2.survivalgames.types.Game.GameMode;
import net.dmulloy2.survivalgames.types.Permission;

/**
 * @author dmulloy2
 */

public class CmdEnable extends SurvivalGamesCommand
{
	public CmdEnable(SurvivalGames plugin)
	{
		super(plugin);
		this.name = "enabled";
		this.optionalArgs.add("id");
		this.description = "Enables arena <id>";
		
		this.permission = Permission.STAFF_ENABLE;
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
					if (g.getMode() == GameMode.DISABLED)
						g.enable();
				}
				
				messageManager.sendFMessage(MessageManager.PrefixType.INFO, "game.all", player, "input-enabled");
			}
			else
			{
				gameManager.enableGame(Integer.parseInt(args[0]));
				messageManager.sendFMessage(MessageManager.PrefixType.INFO, "game.state", player, "arena-" + args[0], "input-enabled");
			}
		}
		catch (NumberFormatException e)
		{
			messageManager.sendFMessage(MessageManager.PrefixType.ERROR, "error.notanumber", player, "input-Arena");
		}
		catch (NullPointerException e)
		{
			messageManager.sendFMessage(MessageManager.PrefixType.ERROR, "error.gamedoesntexist", player, "arena-" + args[0]);
		}
	}
}