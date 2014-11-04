package net.dmulloy2.survivalgames.handlers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import net.dmulloy2.survivalgames.SurvivalGames;
import net.dmulloy2.types.ItemParser;
import net.dmulloy2.util.Util;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class SettingsHandler {
    private FileConfiguration spawns;
    private FileConfiguration system;
    private FileConfiguration kits;
    private FileConfiguration messages;
    private FileConfiguration chest;

    private File spawnsFile;
    private File systemFile;
    private File kitsFile;
    private File messagesFile;
    private File chestsFile;

    private static final int KIT_VERSION = 1;
    private static final int MESSAGE_VERSION = 1;
    private static final int CHEST_VERSION = 0;
    private static final int SPAWN_VERSION = 0;
    private static final int SYSTEM_VERSION = 0;
    private static final int CONFIG_VERSION = 3;

    private final SurvivalGames plugin;
    public SettingsHandler(SurvivalGames plugin) {
        this.plugin = plugin;

        if (plugin.getConfig().getInt("config-version") != CONFIG_VERSION) {
            File config = new File(plugin.getDataFolder(), "config.yml");
            config.delete();
        }

        plugin.saveDefaultConfig();
        plugin.reloadConfig();

        spawnsFile = new File(plugin.getDataFolder(), "spawns.yml");
        systemFile = new File(plugin.getDataFolder(), "system.yml");
        kitsFile = new File(plugin.getDataFolder(), "kits.yml");
        messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        chestsFile = new File(plugin.getDataFolder(), "chest.yml");

        try {
            if (!spawnsFile.exists())
                spawnsFile.createNewFile();
            if (!systemFile.exists())
                systemFile.createNewFile();
            if (!kitsFile.exists())
                loadFile("kits.yml");
            if (!messagesFile.exists())
                loadFile("messages.yml");
            if (!chestsFile.exists())
                loadFile("chest.yml");
        } catch (Throwable ex) {
            plugin.log(Level.WARNING, Util.getUsefulStack(ex, "creating settings files"));
        }

        reloadSystem();
        saveSystemConfig();

        reloadSpawns();
        saveSpawns();

        reloadKits();
        // saveKits();

        reloadChest();

        reloadMessages();
        saveMessages();
    }

    public void set(String key, Object value) {
        plugin.getConfig().set(key, value);
    }

    public FileConfiguration getConfig() {
        return plugin.getConfig();
    }

    public FileConfiguration getSystemConfig() {
        return system;
    }

    public FileConfiguration getSpawns() {
        return spawns;
    }

    public FileConfiguration getKits() {
        return kits;
    }

    public FileConfiguration getChest() {
        return chest;
    }

    public FileConfiguration getMessageConfig() {
        return messages;
    }

    public World getGameWorld(int game) {
        if (system.getString("sg-system.arenas." + game + ".world") == null) {
            return null;
        }

        return plugin.getServer().getWorld(system.getString("sg-system.arenas." + game + ".world"));
    }

    public void reloadConfig() {
        plugin.reloadConfig();
    }

    public boolean moveFile(File ff) {
        plugin.log("Moving outdated config file. " + spawnsFile.getName());
        String name = ff.getName();
        File ff2 = new File(plugin.getDataFolder(), getNextName(name, 0));
        return ff.renameTo(ff2);
    }

    public String getNextName(String name, int n) {
        File ff = new File(plugin.getDataFolder(), name + ".old" + n);
        if (!ff.exists()) {
            return ff.getName();
        } else {
            return getNextName(name, n + 1);
        }
    }

    public void reloadSpawns() {
        spawns = YamlConfiguration.loadConfiguration(spawnsFile);
        if (spawns.getInt("version", 0) != SPAWN_VERSION) {
            moveFile(spawnsFile);
            reloadSpawns();
        }
        spawns.set("version", SPAWN_VERSION);
        saveSpawns();
    }

    public void reloadSystem() {
        system = YamlConfiguration.loadConfiguration(systemFile);
        if (system.getInt("version", 0) != SYSTEM_VERSION) {
            moveFile(systemFile);
            reloadSystem();
        }
        system.set("version", SYSTEM_VERSION);
        saveSystemConfig();
    }

    public void reloadKits() {
        kits = YamlConfiguration.loadConfiguration(kitsFile);
        if (kits.getInt("version", 0) != KIT_VERSION) {
            moveFile(kitsFile);
            loadFile("kits.yml");
            reloadKits();
        }
    }

    public void reloadMessages() {
        messages = YamlConfiguration.loadConfiguration(messagesFile);
        if (messages.getInt("version", 0) != MESSAGE_VERSION) {
            moveFile(messagesFile);
            loadFile("messages.yml");
            reloadMessages();
        }
        messages.set("version", MESSAGE_VERSION);
        saveMessages();
    }

    public void reloadChest() {
        chest = YamlConfiguration.loadConfiguration(chestsFile);
        if (chest.getInt("version", 0) != CHEST_VERSION) {
            moveFile(chestsFile);
            loadFile("chest.yml");
            reloadChest();
        }
    }

    public void saveSystemConfig() {
        try {
            system.save(systemFile);
        } catch (IOException ex) {
            plugin.log(Level.WARNING, Util.getUsefulStack(ex, "saving system config"));
        }
    }

    public void saveSpawns() {
        try {
            spawns.save(spawnsFile);
        } catch (IOException ex) {
            plugin.log(Level.WARNING, Util.getUsefulStack(ex, "saving spawns config"));
        }
    }

    public void saveKits() {
        try {
            kits.save(kitsFile);
        } catch (IOException ex) {
            plugin.log(Level.WARNING, Util.getUsefulStack(ex, "saving kits config"));
        }
    }

    public void saveMessages() {
        try {
            messages.save(messagesFile);
        } catch (IOException ex) {
            plugin.log(Level.WARNING, Util.getUsefulStack(ex, "saving system config"));
        }
    }

    public void saveChest() {
        try {
            chest.save(chestsFile);
        } catch (IOException ex) {
            plugin.log(Level.WARNING, Util.getUsefulStack(ex, "saving system config"));
        }
    }

    public int getSpawnCount(int gameid) {
        return spawns.getInt("spawns." + gameid + ".count");
    }

    public Map<String, Object> getGameFlags(int id) {
        Map<String, Object> flags = new HashMap<String, Object>();
        flags.put("AUTOSTART_PLAYERS", system.getInt("sg-system.arenas." + id + ".flags.autostart"));
        flags.put("AUTOSTART_VOTE", system.getInt("sg-system.arenas." + id + ".flags.vote"));
        flags.put("ENDGAME_ENABLED", system.getBoolean("sg-system.arenas." + id + ".flags.endgame-enabled"));
        flags.put("ENDGAME_PLAYERS", system.getInt("sg-system.arenas." + id + ".flags.endgame-players"));
        flags.put("ENDGAME_CHEST", system.getBoolean("sg-system.arenas." + id + ".flags.endgame-chest"));
        flags.put("ENDGAME_LIGHTNING", system.getBoolean("sg-system.arenas." + id + ".flags.endgame-lightning"));
        flags.put("DUEL_PLAYERS", system.getInt("sg-system.arenas." + id + ".flags.endgame-duel-players"));
        flags.put("DUEL_TIME", system.getInt("sg-system.arenas." + id + ".flags.endgame-duel-time"));
        flags.put("DUEL_ENABLED", system.getBoolean("sg-system.arenas." + id + ".flags.endgame-duel"));
        flags.put("ARENA_NAME", system.getString("sg-system.arenas." + id + ".flags.arena-name"));
        flags.put("ARENA_COST", system.getInt("sg-system.arenas." + id + ".flags.arena-cost"));
        flags.put("ARENA_REWARD", system.getInt("sg-system.arenas." + id + ".flags.arena-reward"));
        flags.put("ARENA_MAXTIME", system.getInt("sg-system.arenas." + id + ".flags.arena-maxtime"));
        flags.put("SPONSOR_ENABLED", system.getBoolean("sg-system.arenas." + id + ".flags.sponsor-enabled"));
        flags.put("SPONSOR_MODE", system.getInt("sg-system.arenas." + id + ".flags.sponsor-mode"));
        return flags;
    }

    public void saveGameFlags(Map<String, Object> flags, int id) {
        system.set("sg-system.arenas." + id + ".flags.autostart", flags.get("AUTOSTART_PLAYERS"));
        system.set("sg-system.arenas." + id + ".flags.vote", flags.get("AUTOSTART_VOTE"));
        system.set("sg-system.arenas." + id + ".flags.endgame-enabled", flags.get("ENDGAME_ENABLED"));
        system.set("sg-system.arenas." + id + ".flags.endgame-players", flags.get("ENDGAME_PLAYERS"));
        system.set("sg-system.arenas." + id + ".flags.endgame-chest", flags.get("ENDGAME_CHEST"));
        system.set("sg-system.arenas." + id + ".flags.endgame-lightning", flags.get("ENDGAME_LIGHTNING"));
        system.set("sg-system.arenas." + id + ".flags.endgame-duel-players", flags.get("DUEL_PLAYERS"));
        system.set("sg-system.arenas." + id + ".flags.endgame-duel-time", flags.get("DUEL_TIME"));
        system.set("sg-system.arenas." + id + ".flags.endgame-duel", flags.get("DUEL_ENABLED"));
        system.set("sg-system.arenas." + id + ".flags.arena-name", flags.get("ARENA_NAME"));
        system.set("sg-system.arenas." + id + ".flags.arena-cost", flags.get("ARENA_COST"));
        system.set("sg-system.arenas." + id + ".flags.arena-reward", flags.get("ARENA_REWARD"));
        system.set("sg-system.arenas." + id + ".flags.arena-maxtime", flags.get("ARENA_MAXTIME"));
        system.set("sg-system.arenas." + id + ".flags.sponsor-enabled", flags.get("SPONSOR_ENABLED"));
        system.set("sg-system.arenas." + id + ".flags.sponsor-mode", flags.get("SPONSOR_MODE"));

        saveSystemConfig();
    }

    public Location getLobbySpawn() {
        try {
            return new Location(plugin.getServer().getWorld(system.getString("sg-system.lobby.spawn.world")), system.getInt("sg-system.lobby.spawn.x"), system.getInt("sg-system.lobby.spawn.y"), system.getInt("sg-system.lobby.spawn.z"));
        } catch (Throwable ex) {
            plugin.getLogHandler().log(Level.WARNING, Util.getUsefulStack(ex, "getting lobby spawn"));
            return null;
        }
    }

    public Location getSpawnPoint(int gameid, int spawnid) {
        return new Location(getGameWorld(gameid), spawns.getInt("spawns." + gameid + "." + spawnid + ".x"), spawns.getInt("spawns." + gameid + "." + spawnid + ".y"), spawns.getInt("spawns." + gameid + "." + spawnid + ".z"));
    }

    public void setLobbySpawn(Location l) {
        system.set("sg-system.lobby.spawn.world", l.getWorld().getName());
        system.set("sg-system.lobby.spawn.x", l.getBlockX());
        system.set("sg-system.lobby.spawn.y", l.getBlockY());
        system.set("sg-system.lobby.spawn.z", l.getBlockZ());
        saveSystemConfig();
    }

    public void setSpawn(int gameid, int spawnid, Vector v) {
        spawns.set("spawns." + gameid + "." + spawnid + ".x", v.getBlockX());
        spawns.set("spawns." + gameid + "." + spawnid + ".y", v.getBlockY());
        spawns.set("spawns." + gameid + "." + spawnid + ".z", v.getBlockZ());
        if (spawnid > spawns.getInt("spawns." + gameid + ".count")) {
            spawns.set("spawns." + gameid + ".count", spawnid);
        }

        try {
            spawns.save(spawnsFile);
        } catch (IOException ex) {
            plugin.log(Level.WARNING, Util.getUsefulStack(ex, "saving spawns config"));
        }

        plugin.getGameHandler().getGame(gameid).addSpawn();
    }

    public String getSqlPrefix() {
        return getConfig().getString("sql.prefix");
    }

    public void loadFile(String file) {
        File t = new File(plugin.getDataFolder(), file);
        plugin.log("Writing new file: " + t.getAbsolutePath());

        try {
            t.createNewFile();
            FileWriter out = new FileWriter(t);
            plugin.debug(file);
            InputStream is = getClass().getResourceAsStream("/" + file);
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                out.write(line + "\n");
                plugin.debug(line);
            }
            out.flush();
            is.close();
            isr.close();
            br.close();
            out.close();
        } catch (IOException ex) {
            plugin.log(Level.WARNING, Util.getUsefulStack(ex, "writing " + t.getAbsolutePath()));
        }
    }

    private List<ItemStack> rewardItems;

    public List<ItemStack> getRewardItems() {
        if (rewardItems == null) {
            rewardItems = ItemParser.parse(plugin, getConfig().getStringList("reward.contents"));
        }

        return rewardItems;
    }
}
