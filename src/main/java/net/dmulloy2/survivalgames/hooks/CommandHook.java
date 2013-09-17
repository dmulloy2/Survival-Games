package net.dmulloy2.survivalgames.hooks;

import net.dmulloy2.survivalgames.SurvivalGames;

public class CommandHook implements HookBase
{
	private final SurvivalGames plugin;
	public CommandHook(SurvivalGames plugin)
	{
		this.plugin = plugin;
	}
	
	@Override
	public void executeHook(String player, String[] args)
	{
		if (player.equalsIgnoreCase("console"))
		{
			plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), args[1]);
		}
		else
		{
			plugin.getServer().getPlayer(player).chat("/" + args[1]);
		}
	}
}