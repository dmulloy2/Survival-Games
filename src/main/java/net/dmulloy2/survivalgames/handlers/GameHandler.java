package net.dmulloy2.survivalgames.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Getter;
import net.dmulloy2.survivalgames.SurvivalGames;
import net.dmulloy2.survivalgames.types.Game;
import net.dmulloy2.survivalgames.types.Game.GameMode;
import net.dmulloy2.survivalgames.types.Prefix;
import net.dmulloy2.survivalgames.util.Kit;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;

public class GameHandler {
    private @Getter List<Kit> kits;
    private @Getter List<Game> games;
    private @Getter Set<String> kitsel;
    private @Getter Map<Integer, HashSet<Block>> openedChest;

    private MessageHandler messageHandler;

    private final SurvivalGames plugin;

    public GameHandler(SurvivalGames plugin) {
        this.plugin = plugin;
        this.messageHandler = plugin.getMessageHandler();

        this.games = new ArrayList<>();
        this.kits = new ArrayList<>();
        this.kitsel = new HashSet<>();
        this.openedChest = new HashMap<>();

        loadGames();
        loadKits();

        for (Game g : getGames()) {
            openedChest.put(g.getID(), new HashSet<Block>());
        }
    }

    public void reloadGames() {
        loadGames();
    }

    public void loadKits() {
        Set<String> kits1 = plugin.getSettingsHandler().getKits().getConfigurationSection("kits").getKeys(false);
        for (String s : kits1) {
            kits.add(new Kit(plugin, s));
        }
    }

    public void loadGames() {
        FileConfiguration c = plugin.getSettingsHandler().getSystemConfig();
        games.clear();
        int no = c.getInt("sg-system.arenano", 0);
        int loaded = 0;
        int a = 1;
        while (loaded < no) {
            if (c.isSet("sg-system.arenas." + a + ".x1")) {
                if (c.getBoolean("sg-system.arenas." + a + ".enabled")) {
                    loaded++;
                    games.add(new Game(plugin, a));
                    plugin.getStatsHandler().addArena(a);
                }
            }

            a++;
        }

        plugin.getLobbyHandler().clearAllSigns();

        plugin.log("Loaded " + loaded + " arenas!");
    }

    // ---- Game Getters

    public Game getGame(Player player) {
        for (Game game : games) {
            if (game.isPlayerActive(player))
                return game;
        }

        return null;
    }

    public Game getGame(Location loc) {
        for (Game game : games) {
            if (game.isBlockInArena(loc)) {
                return game;
            }
        }

        return null;
    }

    // ---- Game ID Getters

    public int getGameId(Player player) {
        for (Game g : games) {
            if (g.isPlayerActive(player))
                return g.getID();
        }

        return -1;
    }

    public int getGameId(Location loc) {
        for (Game g : games) {
            if (g.isBlockInArena(loc))
                return g.getID();
        }

        return -1;
    }

    // ---- Spectating

    public int getSpectateId(Player player) {
        for (Game game : games) {
            if (game.isSpectator(player))
                return game.getID();
        }

        return -1;
    }

    // ---- Activity

    public boolean isPlayerActive(Player player) {
        for (Game game : games) {
            if (game.isPlayerActive(player))
                return true;
        }

        return false;
    }

    public boolean isSpectator(Player player) {
        for (Game game : games) {
            if (game.isSpectator(player))
                return true;
        }

        return false;
    }

    public void removeFromOtherQueues(Player player, int id) {
        for (Game game : games) {
            if (game != null) {
                if (game.isInQueue(player) && game.getID() != id) {
                    game.removeFromQueue(player);
                    messageHandler.sendMessage(Prefix.INFO, "Removed from the queue in arena " + game.getID(), player);
                }
            }
        }
    }

    public boolean isInKitMenu(Player p) {
        return kitsel.contains(p.getName());
    }

    public void leaveKitMenu(Player p) {
        kitsel.remove(p.getName());
    }

    public void openKitMenu(Player p) {
        kitsel.add(p.getName());
    }

    public void selectKit(Player p, int i) {
        p.getInventory().clear();
        List<Kit> kits = getKits(p);
        if (i <= kits.size()) {
            Kit k = getKits(p).get(i);
            if (k != null) {
                ArrayList<ItemStack> var = k.getContents();
                p.getInventory().setContents(var.toArray(new ItemStack[var.size()]));
            }
        }

        p.updateInventory();
    }

    public int getGameCount() {
        return games.size();
    }

    public Game getGame(int  id) {
        for (Game g : games) {
            if (g.getID() == id) {
                return g;
            }
        }

        return null;
    }

    public void removePlayer(Player p, boolean b) {
        getGame(p).removePlayer(p, b);
    }

    public void removeSpectator(Player p) {
        getGame(getSpectateId(p)).removeSpectator(p);
    }

    public void disableGame(int id) {
        getGame(id).disable();
    }

    public void enableGame(int id) {
        getGame(id).enable();
    }

    public GameMode getGameMode(int a) {
        for (Game g : games) {
            if (g.getID() == a) {
                return g.getMode();
            }
        }

        return null;
    }

    public List<Kit> getKits(Player p) {
        List<Kit> k = new ArrayList<>();
        for (Kit kit : kits) {
            if (kit.canUse(p)) {
                k.add(kit);
            }
        }

        return k;
    }

    public void startGame(int a) {
        getGame(a).countdown(10);
    }

    public void addPlayer(Player p, int g) {
        Game game = getGame(g);
        if (game == null) {
            messageHandler.sendFMessage(Prefix.ERROR, "error.input", p, "message-No game by this ID exist!");
            return;
        }

        game.addPlayer(p);
    }

    public void autoAddPlayer(Player pl) {
        List<Game> qg = new ArrayList<>(5);
        for (Game g : games) {
            if (g.getMode() == Game.GameMode.WAITING)
                qg.add(g);
        }

        if (qg.isEmpty()) {
            messageHandler.sendMessage(Prefix.WARNING, "No games to join!", pl);
            return;
        }

        qg.get(0).addPlayer(pl);
    }

    public void createArenaFromSelection(Player pl) {
        WorldEditPlugin we = plugin.getWorldEdit();
        if (we == null) {
            messageHandler.sendMessage(Prefix.WARNING, "WorldEdit is not installed! Please install it!", pl);
            return;
        }

        Selection sel = we.getSelection(pl);
        if (sel == null) {
            messageHandler.sendMessage(Prefix.WARNING, "You must make a WorldEdit Selection first!", pl);
            return;
        }

        Location max = sel.getMaximumPoint();
        Location min = sel.getMinimumPoint();

        FileConfiguration c = plugin.getSettingsHandler().getSystemConfig();

        int no = c.getInt("sg-system.arenano") + 1;
        c.set("sg-system.arenano", no);
        if (games.size() == 0) {
            no = 1;
        } else {
            no = games.get(games.size() - 1).getID() + 1;
        }

        plugin.getSettingsHandler().getSpawns().set(("spawns." + no), null);
        c.set("sg-system.arenas." + no + ".world", max.getWorld().getName());
        c.set("sg-system.arenas." + no + ".x1", max.getBlockX());
        c.set("sg-system.arenas." + no + ".y1", max.getBlockY());
        c.set("sg-system.arenas." + no + ".z1", max.getBlockZ());
        c.set("sg-system.arenas." + no + ".x2", min.getBlockX());
        c.set("sg-system.arenas." + no + ".y2", min.getBlockY());
        c.set("sg-system.arenas." + no + ".z2", min.getBlockZ());
        c.set("sg-system.arenas." + no + ".enabled", true);

        plugin.getSettingsHandler().saveSystemConfig();
        hotAddArena(no);
        pl.sendMessage(ChatColor.GREEN + "Arena ID " + no + " Succesfully added");
    }

    private void hotAddArena(int no) {
        Game game = new Game(plugin, no);
        games.add(game);
        plugin.getStatsHandler().addArena(no);
    }

    public void hotRemoveArena(int no) {
        for (Game g : games.toArray(new Game[games.size()])) {
            if (g.getID() == no) {
                games.remove(getGame(no));
            }
        }
    }

    public void gameEndCallBack(int id) {
        getGame(id).setRBStatus("clearing chest");
        openedChest.put(id, new HashSet<Block>());
    }

    public List<String> getStringList(Game game) {
        Player[][] players = game.getPlayers();

        List<String> lines = new ArrayList<>();
        StringBuilder line = new StringBuilder();

        line.append(ChatColor.GREEN + "<---------------------[ Alive: ").append(players[0].length).append(" ]--------------------->").append(ChatColor.GREEN).append(" ");
        lines.add(line.toString());

        line = new StringBuilder();
        if (players[0].length == 0) {
            line.append(ChatColor.GREEN + "None");
        } else {
            for (Player p : players[0]) {
                line.append(ChatColor.GREEN).append(p.getName()).append(", ");
            }
            line.delete(line.lastIndexOf(","), line.lastIndexOf(" "));
        }

        line.append(".");
        lines.add(line.toString());

        line = new StringBuilder();
        line.append(ChatColor.RED + "<---------------------[ Dead: ").append(players[1].length).append(" ]--------------------->").append(ChatColor.RED).append(" ");
        lines.add(line.toString());

        line = new StringBuilder();
        if (players[1].length == 0) {
            line.append(ChatColor.RED + "None");
        } else {
            for (Player p : players[1]) {
                line.append(ChatColor.RED).append(p.getName()).append(", ");
            }
            line.delete(line.lastIndexOf(","), line.lastIndexOf(" "));
        }

        line.append(".");
        lines.add(line.toString());

        return lines;
    }
}
