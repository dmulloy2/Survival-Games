package net.dmulloy2.survivalgames.commands;

import net.dmulloy2.survivalgames.SurvivalGames;
import net.dmulloy2.survivalgames.managers.MessageManager.PrefixType;
import net.dmulloy2.survivalgames.types.Game;
import net.dmulloy2.survivalgames.types.Permission;

/**
 * @author dmulloy2
 */

public class CmdForceStart extends SurvivalGamesCommand
{
	public CmdForceStart(SurvivalGames plugin)
	{
		super(plugin);
		this.name = "forcestart";
		this.aliases.add("fs");
		this.description = "Forces the game to start";
		
		this.permission = Permission.ADMIN_FORCESTART;
	}

	@Override
	public void perform()
	{
		int game = -1;
		int seconds = 10;

		if (args.length == 2)
		{
			seconds = Integer.parseInt(args[1]);
		}
		else if (args.length >= 1)
		{
			game = Integer.parseInt(args[0]);
		}
		else
		{
			game = gameManager.getPlayerGameId(player);
		}

		if (game == -1)
		{
			messageManager.sendFMessage(PrefixType.ERROR, "error.notingame", player);
			return;
		}

		if (gameManager.getGame(game).getActivePlayers() < 2)
		{
			messageManager.sendFMessage(PrefixType.ERROR, "error.notenoughtplayers", player);
			return;
		}

		Game g = gameManager.getGame(game);
		if (g.getMode() != Game.GameMode.WAITING && !player.hasPermission("sg.admin.restart"))
		{
			messageManager.sendFMessage(PrefixType.ERROR, "error.alreadyingame", player);
			return;
		}
		
		g.countdown(seconds);

		messageManager.sendFMessage(PrefixType.INFO, "game.started", player, "arena-" + game);
	}
}