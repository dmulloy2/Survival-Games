package net.dmulloy2.survivalgames.logging;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import net.dmulloy2.survivalgames.SurvivalGames;
import net.dmulloy2.survivalgames.types.Game;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.NPC;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class QueueManager
{
	private ConcurrentHashMap<Integer, List<BlockData>> queue = new ConcurrentHashMap<Integer, List<BlockData>>();
	private File baseDir;

	private final SurvivalGames plugin;

	public QueueManager(SurvivalGames plugin)
	{
		this.plugin = plugin;
	}

	public void setup()
	{
		baseDir = new File(plugin.getDataFolder() + "/ArenaData/");
		try
		{
			if (!baseDir.exists())
			{
				baseDir.mkdirs();
			}
			for (Game g : plugin.getGameManager().getGames())
			{
				ensureFile(g.getID());
			}
		}
		catch (Exception e)
		{
			//
		}

		new DataDumper().runTaskTimer(plugin, 100, 100);
	}

	public void rollback(final int id, final boolean shutdown)
	{
		loadSave(id);
		if (!shutdown)
		{
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Rollback(id, shutdown, 0, 1, 0));
		}
		else
		{
			new Rollback(id, shutdown, 0, 1, 0).run();
		}

		List<Entity> removelist = new ArrayList<Entity>();

		for (Entity e : plugin.getSettingsManager().getGameWorld(id).getEntities())
		{
			if (!(e instanceof Player) && (!(e instanceof NPC)))
			{
				if (plugin.getGameManager().getBlockGameId(e.getLocation()) == id)
				{
					removelist.add(e);
				}
			}
		}

		if (!shutdown)
		{
			new EntityRemoveTask(removelist.iterator()).runTaskLater(plugin, 2);
		}
		else
		{
			new EntityRemoveTask(removelist.iterator()).run();
		}
	}

	public void add(BlockData data)
	{
		List<BlockData> dat = queue.get(data.getGameId());
		if (dat == null)
		{
			dat = new ArrayList<BlockData>();
			ensureFile(data.getGameId());
		}
		dat.add(data);
		queue.put(data.getGameId(), dat);

	}

	public void ensureFile(int id)
	{
		try
		{
			File f2 = new File(baseDir, "Arena" + id + ".dat");
			if (!f2.exists())
			{
				f2.createNewFile();
			}
		}
		catch (Exception e)
		{
			//
		}
	}

	public class DataDumper extends BukkitRunnable
	{
		@Override
		public void run()
		{
			for (int id : queue.keySet())
			{
				try
				{
					List<BlockData> data = queue.get(id);
					ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(new File(baseDir, "Arena" + id + ".dat")));

					out.writeObject(data);
					out.flush();
					out.close();

				}
				catch (Exception e)
				{
					//
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void loadSave(int id)
	{
		ensureFile(id);
		try
		{
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File(baseDir, "Arena" + id + ".dat")));

			List<BlockData> input = (ArrayList<BlockData>) in.readObject();

			List<BlockData> data = queue.get(id);
			if (data == null)
			{
				data = new ArrayList<BlockData>();
			}

			for (BlockData d : input)
			{
				if (!data.contains(d))
				{
					data.add(d);
				}
			}

			queue.put(id, data);
			in.close();
		}
		catch (Exception e)
		{
			//
		}
	}

	public class Rollback extends BukkitRunnable
	{
		int id, totalRollback, iteration;
		Game game;
		boolean shutdown;
		long time;

		public Rollback(int id, boolean shutdown, int trb, int it, long time)
		{
			this.id = id;
			this.totalRollback = trb;
			this.iteration = it;
			this.game = plugin.getGameManager().getGame(id);
			this.shutdown = shutdown;
			this.time = time;
		}

		@Override
		@SuppressWarnings("deprecation")
		public void run()
		{
			List<BlockData> data = queue.get(id);
			if (data != null)
			{
				int a = data.size() - 1;
				int rb = 0;
				long t1 = new Date().getTime();
				int pt = plugin.getSettingsManager().getConfig().getInt("rollback.per-tick", 100);
				while (a >= 0 && (rb < pt || shutdown))
				{
					plugin.debug("Resetting " + a);
					BlockData result = data.get(a);
					if (result.getGameId() == game.getID())
					{
						data.remove(a);
						Location l = new Location(Bukkit.getWorld(result.getWorld()), result.getX(), result.getY(), result.getZ());
						Block b = l.getBlock();
						b.setTypeIdAndData(result.getPrevid(), result.getPrevdata(), true);
						b.getState().update();
						rb++;
					}
					a--;
				}

				time += new Date().getTime() - t1;

				if (a != -1)
				{
					Bukkit.getScheduler().scheduleSyncDelayedTask(plugin.getGameManager().getPlugin(),
							new Rollback(id, shutdown, totalRollback + rb, iteration + 1, time), 1);
				}
				else
				{
					plugin.$("Arena " + id + " reset. Rolled back " + totalRollback + " blocks in " + iteration + " iterations (" + pt
							+ " blocks per iteration Total time spent rolling back was " + time + "ms");
					game.resetCallback();
				}
			}
			else
			{
				plugin.$("Arena " + id + " reset. Rolled back " + totalRollback + " blocks in " + iteration
						+ " iterations. Total time spent rolling back was " + time + "ms");
				game.resetCallback();
			}
		}
	}

	public class EntityRemoveTask extends BukkitRunnable
	{
		final Iterator<Entity> iter;

		public EntityRemoveTask(final Iterator<Entity> iter)
		{
			this.iter = iter;
		}

		@Override
		public void run()
		{
			while (iter.hasNext())
			{
				Entity e = iter.next();
				e.remove();
				iter.remove();
			}
		}
	}
}