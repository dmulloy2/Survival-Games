package net.dmulloy2.survivalgames.commands;

import java.util.HashMap;

import net.dmulloy2.survivalgames.SurvivalGames;
import net.dmulloy2.survivalgames.types.Game;
import net.dmulloy2.survivalgames.types.Permission;
import net.dmulloy2.survivalgames.types.Prefix;

import org.bukkit.Location;

/**
 * @author dmulloy2
 */

public class CmdSetSpawn extends SurvivalGamesCommand {
    public CmdSetSpawn(SurvivalGames plugin) {
        super(plugin);
        this.name = "setspawn";
        this.addRequiredArg("id");
        this.description = "Sets a spawn for the arena you are located in";

        this.permission = Permission.ADMIN_SETSPAWN;
    }

    @Override
    public void perform() {
        loadNextSpawn();

        Location l = player.getLocation();
        int game = gameHandler.getGameId(l);

        if (game == -1) {
            plugin.getMessageHandler().sendFMessage(Prefix.ERROR, "error.notinarena", player);
            return;
        }

        int i = 0;
        if (args[0].equalsIgnoreCase("next")) {
            i = next.get(game);
            next.put(game, next.get(game) + 1);
        } else {
            try {
                i = Integer.parseInt(args[0]);
                if (i > next.get(game) + 1 || i < 1) {
                    plugin.getMessageHandler().sendFMessage(Prefix.ERROR, "error.between", player, "num-" + next.get(game));
                    return;
                }
                if (i == next.get(game)) {
                    next.put(game, next.get(game) + 1);
                }
            } catch (Exception e) {
                plugin.getMessageHandler().sendFMessage(Prefix.ERROR, "error.badinput", player);
                return;
            }
        }

        if (i == -1) {
            plugin.getMessageHandler().sendFMessage(Prefix.ERROR, "error.notinside", player);
            return;
        }

        plugin.getSettingsHandler().setSpawn(game, i, l.toVector());
        plugin.getMessageHandler().sendFMessage(Prefix.INFO, "info.spawnset", player, "num-" + i, "arena-" + game);
    }

    private HashMap<Integer, Integer> next = new HashMap<>();

    public void loadNextSpawn() {
        // Avoid Concurrency problems
        java.util.List<Game> var = gameHandler.getGames();
        for (Game g : var.toArray(new Game[var.size()])) {
            next.put(g.getID(), plugin.getSettingsHandler().getSpawnCount(g.getID()) + 1);
        }
    }
}
