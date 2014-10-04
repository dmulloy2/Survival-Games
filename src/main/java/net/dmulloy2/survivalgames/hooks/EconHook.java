package net.dmulloy2.survivalgames.hooks;

import net.dmulloy2.survivalgames.SurvivalGames;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.entity.Player;

public class EconHook implements HookBase {
    private final SurvivalGames plugin;

    public EconHook(SurvivalGames plugin) {
        this.plugin = plugin;
    }

    @Override
    public void executeHook(String player, String[] args) {
        if (plugin.getEconomyManager().econPresent()) {
            Economy econ = plugin.getEconomyManager().getEcon();
            String split[] = args[1].split(" ");
            if (split.length == 3) {
                Player p = plugin.getServer().getPlayer(split[1]);
                int funds = Integer.parseInt(split[2]);
                if (split[0].equals("remove")) {
                    econ.withdrawPlayer(p, funds);
                } else if (split[1].equals("deposit")) {
                    econ.depositPlayer(p, funds);
                }
            }
        }
    }
}
