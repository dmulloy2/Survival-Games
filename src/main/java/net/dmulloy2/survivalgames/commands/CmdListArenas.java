package net.dmulloy2.survivalgames.commands;

import java.util.ArrayList;
import java.util.List;

import net.dmulloy2.survivalgames.SurvivalGames;
import net.dmulloy2.survivalgames.types.Game;
import net.dmulloy2.survivalgames.types.Permission;
import net.dmulloy2.survivalgames.types.Prefix;

import org.bukkit.ChatColor;

/**
 * @author dmulloy2
 */

public class CmdListArenas extends SurvivalGamesCommand {
    public CmdListArenas(SurvivalGames plugin) {
        super(plugin);
        this.name = "listarenas";
        this.description = "List all available arenas";

        this.permission = Permission.PLAYER_LISTARENAS;
    }

    @Override
    public void perform() {
        List<Game> games = gameManager.getGames();
        if (games.isEmpty()) {
            plugin.getMessageHandler().sendFMessage(Prefix.WARNING, "error.noarenasexist", player);
            return;
        }

        List<String> lines = new ArrayList<String>();
        StringBuilder line = new StringBuilder();
        line.append(ChatColor.BLUE + "-----[ " + ChatColor.GOLD + "Available Arenas" + ChatColor.BLUE + " ]-----");
        lines.add(line.toString());

        for (Game game : games) {
            line = new StringBuilder();
            line.append(ChatColor.GREEN + game.getName() + "    ");
            if (game.getMode() == Game.GameMode.INGAME) {
                line.append(ChatColor.GREEN + "[INGAME]");
            } else if (game.getMode() == Game.GameMode.STARTING) {
                line.append(ChatColor.GREEN + "[STARTING]");
            } else if (game.getMode() == Game.GameMode.FINISHING) {
                line.append(ChatColor.GREEN + "[FINISHING]");
            } else if (game.getMode() == Game.GameMode.INACTIVE) {
                line.append(ChatColor.YELLOW + "[INACTIVE]");
            } else if (game.getMode() == Game.GameMode.WAITING) {
                line.append(ChatColor.YELLOW + "[WAITING]");
            } else {
                line.append(ChatColor.RED + "[" + game.getGameMode().toString() + "]");
            }

            line.append(ChatColor.YELLOW + "    [");
            line.append(game.getActivePlayers() + "/");
            line.append(game.getInactivePlayers() + "/");
            line.append(plugin.getSettingsManager().getSpawnCount(game.getID()));
            line.append("]");
            lines.add(line.toString());
        }

        for (String s : lines) {
            sendMessage(s);
        }
    }
}
