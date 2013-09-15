package net.dmulloy2.survivalgames.hooks;

import net.dmulloy2.survivalgames.managers.EconomyManager;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class EconHook implements HookBase
{

	@Override
	public void executehook(String player, String[] s2)
	{
		if (EconomyManager.getInstance().econPresent())
		{
			Economy econ = EconomyManager.getInstance().getEcon();
			String split[] = s2[1].split(" ");
			if (split.length == 3)
			{
				Player p = Bukkit.getServer().getPlayer(split[1]);
				int funds = Integer.parseInt(split[2]);
				if (split[0].equals("remove"))
				{
					econ.bankWithdraw(p.getName(), funds);
				}
			}
		}
	}
}