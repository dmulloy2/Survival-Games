package net.dmulloy2.survivalgames.commands;

import net.dmulloy2.survivalgames.SurvivalGames;
import net.dmulloy2.survivalgames.types.Prefix;

/**
 * @author dmulloy2
 */

public class CmdLeave extends SurvivalGamesCommand
{
	public CmdLeave(SurvivalGames plugin)
	{
		super(plugin);
		this.name = "leave";
		this.description = "Leaves the game";
	}

	@Override
	public void perform()
	{
		if (gameManager.getPlayerGameId(player) == -1)
		{
			plugin.getMessageHandler().sendFMessage(Prefix.ERROR, "error.notingame", player);
			return;
		}

		gameManager.removePlayer(player, true);
	}
}