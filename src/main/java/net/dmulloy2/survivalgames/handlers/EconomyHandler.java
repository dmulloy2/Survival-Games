package net.dmulloy2.survivalgames.handlers;

import net.dmulloy2.survivalgames.SurvivalGames;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.plugin.RegisteredServiceProvider;

public class EconomyHandler {
    private Economy economy;
    private boolean enabled;

    private final SurvivalGames plugin;

    public EconomyHandler(SurvivalGames plugin) {
        this.plugin = plugin;
        this.enabled = setupEconomy();
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null) {
            this.economy = economyProvider.getProvider();
        }

        return economy != null;
    }

    public Economy getEconomy() {
        return economy;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
