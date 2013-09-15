package net.dmulloy2.survivalgames.commands;

import net.dmulloy2.survivalgames.SurvivalGames;
import net.dmulloy2.survivalgames.managers.MessageManager;
import net.dmulloy2.survivalgames.types.Permission;

/**
 * @author dmulloy2
 */

public class CmdSetLobbySpawn extends SurvivalGamesCommand
{
	public CmdSetLobbySpawn(SurvivalGames plugin)
	{
		super(plugin);
		this.name = "setlobbyspawn";
		this.description = "Set the lobby spawnpoint";
		
		this.permission = Permission.ADMIN_SETLOBBYSPAWN;
	}

	@Override
	public void perform()
	{
		plugin.getSettingsManager().setLobbySpawn(player.getLocation());
		messageManager.sendFMessage(MessageManager.PrefixType.INFO, "info.lobbyspawn", player);
	}
}
