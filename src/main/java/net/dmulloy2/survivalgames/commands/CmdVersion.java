package net.dmulloy2.survivalgames.commands;

import net.dmulloy2.survivalgames.SurvivalGames;
import net.dmulloy2.survivalgames.types.Permission;

/**
 * @author dmulloy2
 */

public class CmdVersion extends SurvivalGamesCommand
{
	public CmdVersion(SurvivalGames plugin)
	{
		super(plugin);
		this.name = "version";
		this.aliases.add("v");
		this.description = "Displays version information";

		this.permission = Permission.PLAYER_VERSION;
	}

	@Override
	public void perform()
	{
		sendMessage("&3====[ &eSurvivalGames &3]====");
		sendMessage("&bAuthor: &edmulloy2");
		sendMessage("&bVersion: &e{0}", plugin.getDescription().getVersion());
	}
}