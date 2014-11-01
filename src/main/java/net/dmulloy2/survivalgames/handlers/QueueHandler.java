package net.dmulloy2.survivalgames.handlers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;

import net.dmulloy2.survivalgames.SurvivalGames;
import net.dmulloy2.survivalgames.types.BlockData;
import net.dmulloy2.survivalgames.types.Game;
import net.dmulloy2.util.Util;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

public class QueueHandler {
    private final ConcurrentMap<Integer, List<BlockData>> queue;
    private final File baseDir;

    private final SurvivalGames plugin;
    public QueueHandler(SurvivalGames plugin) {
        this.plugin = plugin;
        this.queue = new ConcurrentHashMap<>();

        this.baseDir = new File(plugin.getDataFolder(), "ArenaData");
        if (!baseDir.exists())
            baseDir.mkdirs();

        for (Game g : plugin.getGameHandler().getGames()) {
            ensureFile(g.getID());
        }

        new DataDumper().runTaskTimer(plugin, 100, 100);
    }

    private static final List<EntityType> PERSISTENT = Arrays.asList(
            EntityType.PLAYER, EntityType.VILLAGER, EntityType.ITEM_FRAME, EntityType.PAINTING
    );

    public void rollback(final int id) {
        loadSave(id);

        if (plugin.isDisabling()) {
            rollback(id, true, 0, 1, 0);
        } else {
            new Rollback(id, false, 0, 1, 0).runTaskLater(plugin, 2L);
        }

        for (Entity entity : plugin.getSettingsHandler().getGameWorld(id).getEntities()) {
            if (entity != null && entity.isValid()) {
                if (!PERSISTENT.contains(entity.getType())) {
                    if (plugin.getGameHandler().getBlockGameId(entity.getLocation()) == id) {
                        if (entity instanceof LivingEntity) {
                            ((LivingEntity) entity).setHealth(0.0D);
                        }

                        entity.remove();
                    }
                }
            }
        }
    }

    public void add(BlockData data) {
        List<BlockData> dat = queue.get(data.getGameId());
        if (dat == null) {
            dat = new ArrayList<BlockData>();
            ensureFile(data.getGameId());
        }

        dat.add(data);
        queue.put(data.getGameId(), dat);
    }

    public void ensureFile(int id) {
        try {
            File file = new File(baseDir, "Arena" + id + ".dat");
            if (!file.exists())
                file.createNewFile();
        } catch (IOException ex) {
            plugin.log(Level.WARNING, Util.getUsefulStack(ex, "creating data file for game " + id));
        }
    }

    public class DataDumper extends BukkitRunnable {

        @Override
        public void run() {
            for (int id : queue.keySet()) {
                try {
                    List<BlockData> data = queue.get(id);
                    ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(new File(baseDir, "Arena" + id + ".dat")));

                    out.writeObject(data);
                    out.flush();
                    out.close();
                } catch (IOException ex) {
                    plugin.log(Level.WARNING, Util.getUsefulStack(ex, "dumping data for game " + id));
                }
            }
        }

    }

    public void loadSave(int id) {
        ensureFile(id);

        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File(baseDir, "Arena" + id + ".dat")));

            @SuppressWarnings("unchecked")
            List<BlockData> input = (List<BlockData>) in.readObject();

            List<BlockData> data = queue.get(id);
            if (data == null) {
                data = new ArrayList<BlockData>();
            }

            data.addAll(input);
            queue.put(id, data);
            in.close();
        } catch (Throwable ex) {
            plugin.log(Level.WARNING, Util.getUsefulStack(ex, "loading save for game " + id));
        }
    }

    public class Rollback extends BukkitRunnable {
        private int id, totalRollback, iteration;
        private boolean shutdown;
        private long time;

        public Rollback(int id, boolean shutdown, int trb, int it, long time) {
            this.id = id;
            this.totalRollback = trb;
            this.iteration = it;
            this.shutdown = shutdown;
            this.time = time;
        }

        @Override
        public void run() {
            rollback(id, shutdown, totalRollback, iteration, time);
        }
    }

    public final void rollback(int id, boolean shutdown, int totalRollback, int iteration, long time) {
        Game game = plugin.getGameHandler().getGame(id);
        List<BlockData> data = queue.get(id);
        if (data != null) {
            int a = data.size() - 1;
            int rb = 0;
            long t1 = new Date().getTime();
            int pt = plugin.getSettingsHandler().getConfig().getInt("rollback.per-tick", 100);
            while (a >= 0 && (rb < pt || shutdown)) {
                plugin.debug("Resetting " + a);
                BlockData result = data.get(a);
                if (result.getGameId() == game.getID()) {
                    data.remove(a);
                    Location l = new Location(plugin.getServer().getWorld(result.getWorld()), result.getX(), result.getY(), result.getZ());
                    Block b = l.getBlock();
                    if (result.getPrevmat() != null) {
                        b.setType(result.getPrevmat());
                        b.getState().setData(result.getPrevdata());
                        b.getState().update();
                        rb++;
                    }
                }
                a--;
            }

            time += new Date().getTime() - t1;

            if (a != -1) {
                if (!plugin.isDisabling()) {
                    new Rollback(id, shutdown, totalRollback + rb, iteration + 1, time).runTaskLater(plugin, 1);
                } else {
                    rollback(id, shutdown, totalRollback + rb, iteration + 1, time);
                }
            } else {
                plugin.log("Arena " + id + " reset. Rolled back " + totalRollback + " blocks in " + iteration + " iterations (" + pt + " blocks per iteration Total time spent rolling back was " + time + "ms");
                game.resetCallback();
            }
        } else {
            plugin.log("Arena " + id + " reset. Rolled back " + totalRollback + " blocks in " + iteration + " iterations. Total time spent rolling back was " + time + "ms");
            game.resetCallback();
        }
    }
}
