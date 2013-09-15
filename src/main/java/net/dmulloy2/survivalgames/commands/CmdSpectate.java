package net.dmulloy2.survivalgames.commands;

import net.dmulloy2.survivalgames.SurvivalGames;
import net.dmulloy2.survivalgames.managers.MessageManager;
import net.dmulloy2.survivalgames.types.Permission;

/**
 * @author dmulloy2
 */

public class CmdSpectate extends SurvivalGamesCommand
{
	public CmdSpectate(SurvivalGames plugin)
	{
		super(plugin);
		this.name = "spectate";
		this.optionalArgs.add("id");
		this.description = "Spectate a running arena";
		
		this.permission = Permission.PLAYER_SPECTATE;
	}

	@Override
	public void perform()
	{
		if (args.length == 0)
		{
			if (gameManager.isSpectator(player))
			{
				gameManager.removeSpectator(player);
				return;
			}

			messageManager.sendFMessage(MessageManager.PrefixType.ERROR, "error.notspecified", player, "input-Game ID");
			return;
		}
		
		if (plugin.getSettingsManager().getSpawnCount(Integer.parseInt(args[0])) == 0)
		{
			messageManager.sendFMessage(MessageManager.PrefixType.ERROR, "error.nospawns", player);
			return;
		}
		
		if (gameManager.isPlayerActive(player))
		{
			messageManager.sendFMessage(MessageManager.PrefixType.ERROR, "error.specingame", player);
			return;
		}
		
		gameManager.getGame(Integer.parseInt(args[0])).addSpectator(player);
	}
}