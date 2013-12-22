package net.dmulloy2.survivalgames.managers;

import net.dmulloy2.survivalgames.SurvivalGames;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.plugin.RegisteredServiceProvider;

public class EconomyManager
{
	private Economy economy;
	private boolean enabled;

	private final SurvivalGames plugin;
	public EconomyManager(SurvivalGames plugin)
	{
		this.plugin = plugin;
		this.enabled = setupEconomy();
	}

	private boolean setupEconomy()
	{
		RegisteredServiceProvider<Economy> economyProvider = plugin.getServer().getServicesManager().getRegistration(Economy.class);
		if (economyProvider != null)
		{
			this.economy = economyProvider.getProvider();
		}

		return economy != null;
	}

	public Economy getEcon()
	{
		return economy;
	}

	public boolean econPresent()
	{
		return enabled;
	}
}