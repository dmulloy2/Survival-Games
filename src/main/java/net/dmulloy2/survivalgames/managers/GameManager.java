package net.dmulloy2.survivalgames.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import net.dmulloy2.survivalgames.SurvivalGames;
import net.dmulloy2.survivalgames.api.PlayerLeaveArenaEvent;
import net.dmulloy2.survivalgames.managers.MessageManager.PrefixType;
import net.dmulloy2.survivalgames.types.Game;
import net.dmulloy2.survivalgames.types.Game.GameMode;
import net.dmulloy2.survivalgames.util.Kit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;

public class GameManager
{
	private @Getter
	List<Kit> kits;
	private @Getter
	List<Game> games;

	private @Getter
	HashSet<Player> kitsel;

	public @Getter
	HashMap<Integer, HashSet<Block>> openedChest;

	private MessageManager msgmgr;

	private final SurvivalGames plugin;

	public GameManager(SurvivalGames plugin)
	{
		this.plugin = plugin;

		this.games = new ArrayList<Game>();
		this.kits = new ArrayList<Kit>();

		this.kitsel = new HashSet<Player>();

		this.openedChest = new HashMap<Integer, HashSet<Block>>();

		this.msgmgr = plugin.getMessageManager();

		LoadGames();
		LoadKits();
		for (Game g : getGames())
		{
			openedChest.put(g.getID(), new HashSet<Block>());
		}
	}

	public SurvivalGames getPlugin()
	{
		return plugin;
	}

	public void reloadGames()
	{
		LoadGames();
	}

	public void LoadKits()
	{
		Set<String> kits1 = plugin.getSettingsManager().getKits().getConfigurationSection("kits").getKeys(false);
		for (String s : kits1)
		{
			kits.add(new Kit(plugin, s));
		}
	}

	public void LoadGames()
	{
		FileConfiguration c = plugin.getSettingsManager().getSystemConfig();
		games.clear();
		int no = c.getInt("sg-system.arenano", 0);
		int loaded = 0;
		int a = 1;
		while (loaded < no)
		{
			if (c.isSet("sg-system.arenas." + a + ".x1"))
			{
				// c.set("sg-system.arenas."+a+".enabled",c.getBoolean("sg-system.arena."+a+".enabled",
				// true));
				if (c.getBoolean("sg-system.arenas." + a + ".enabled"))
				{
					// plugin.$(c.getString("sg-system.arenas."+a+".enabled"));
					// c.set("sg-system.arenas."+a+".vip",c.getBoolean("sg-system.arenas."+a+".vip",
					// false));
					plugin.$("Loading Arena: " + a);
					loaded++;
					games.add(new Game(this, a));
					plugin.getStatsManager().addArena(a);
				}
			}
			a++;
		}

		plugin.getLobbyManager().clearAllSigns();

		plugin.$("Loaded " + loaded + " arenas!");
	}

	public int getBlockGameId(Location v)
	{
		for (Game g : games)
		{
			if (g.isBlockInArena(v))
			{
				return g.getID();
			}
		}

		return -1;
	}

	public int getPlayerGameId(Player p)
	{
		for (Game g : games)
		{
			if (g.isPlayerActive(p))
			{
				return g.getID();
			}
		}

		return -1;
	}

	public int getPlayerSpectateId(Player p)
	{
		for (Game g : games)
		{
			if (g.isSpectator(p))
			{
				return g.getID();
			}
		}

		return -1;
	}

	public boolean isPlayerActive(Player player)
	{
		for (Game g : games)
		{
			if (g.isPlayerActive(player))
			{
				return true;
			}
		}

		return false;
	}

	public boolean isPlayerInactive(Player player)
	{
		for (Game g : games)
		{
			if (g.isPlayerActive(player))
			{
				return true;
			}
		}

		return false;
	}

	public boolean isSpectator(Player player)
	{
		for (Game g : games)
		{
			if (g.isSpectator(player))
			{
				return true;
			}
		}

		return false;
	}

	public void removeFromOtherQueues(Player p, int id)
	{
		for (Game g : getGames())
		{
			if (g.isInQueue(p) && g.getID() != id)
			{
				g.removeFromQueue(p);
				msgmgr.sendMessage(PrefixType.INFO, "Removed from the queue in arena " + g.getID(), p);
			}
		}
	}

	public boolean isInKitMenu(Player p)
	{
		return kitsel.contains(p);
	}

	public void leaveKitMenu(Player p)
	{
		kitsel.remove(p);
	}

	public void openKitMenu(Player p)
	{
		kitsel.add(p);
	}

	@SuppressWarnings("deprecation")
	public void selectKit(Player p, int i)
	{
		p.getInventory().clear();
		List<Kit> kits = getKits(p);
		if (i <= kits.size())
		{
			Kit k = getKits(p).get(i);
			if (k != null)
			{
				p.getInventory().setContents(k.getContents().toArray(new ItemStack[0]));
			}
		}

		p.updateInventory();
	}

	public int getGameCount()
	{
		return games.size();
	}

	public Game getGame(int a)
	{
		// int t = gamemap.get(a);
		for (Game g : games)
		{
			if (g.getID() == a)
			{
				return g;
			}
		}

		return null;
	}

	public void removePlayer(Player p, boolean b)
	{
		for (Game g : games)
		{
			if (g.getAllPlayers().contains(p))
			{
				PlayerLeaveArenaEvent leavearena = new PlayerLeaveArenaEvent(p, g);
				Bukkit.getServer().getPluginManager().callEvent(leavearena);
			}
		}

		getGame(getPlayerGameId(p)).removePlayer(p, b);
	}

	public void removeSpectator(Player p)
	{
		getGame(getPlayerSpectateId(p)).removeSpectator(p);
	}

	public void disableGame(int id)
	{
		getGame(id).disable();
	}

	public void enableGame(int id)
	{
		getGame(id).enable();
	}

	public GameMode getGameMode(int a)
	{
		for (Game g : games)
		{
			if (g.getID() == a)
			{
				return g.getMode();
			}
		}

		return null;
	}

	public List<Kit> getKits(Player p)
	{
		List<Kit> k = new ArrayList<Kit>();
		for (Kit kit : kits)
		{
			if (kit.canUse(p))
			{
				k.add(kit);
			}
		}

		return k;
	}

	// TODO: Actually make this countdown correctly
	public void startGame(int a)
	{
		getGame(a).countdown(10);
	}

	public void addPlayer(Player p, int g)
	{
		Game game = getGame(g);
		if (game == null)
		{
			plugin.getMessageManager().sendFMessage(PrefixType.ERROR, "error.input", p, "message-No game by this ID exist!");
			return;
		}

		getGame(g).addPlayer(p);
	}

	public void autoAddPlayer(Player pl)
	{
		List<Game> qg = new ArrayList<Game>(5);
		for (Game g : games)
		{
			if (g.getMode() == Game.GameMode.WAITING)
				qg.add(g);
		}

		// TODO: fancy auto balance algorithm
		if (qg.size() == 0)
		{
			pl.sendMessage(ChatColor.RED + "No games to join");
			msgmgr.sendMessage(PrefixType.WARNING, "No games to join!", pl);
			return;
		}

		qg.get(0).addPlayer(pl);
	}

	public WorldEditPlugin getWorldEdit()
	{
		return plugin.getWorldEdit();
	}

	public void createArenaFromSelection(Player pl)
	{
		FileConfiguration c = plugin.getSettingsManager().getSystemConfig();

		WorldEditPlugin we = getWorldEdit();
		Selection sel = we.getSelection(pl);
		if (sel == null)
		{
			msgmgr.sendMessage(PrefixType.WARNING, "You must make a WorldEdit Selection first!", pl);
			return;
		}

		Location max = sel.getMaximumPoint();
		Location min = sel.getMinimumPoint();

		int no = c.getInt("sg-system.arenano") + 1;
		c.set("sg-system.arenano", no);
		if (games.size() == 0)
		{
			no = 1;
		}
		else
		{
			no = games.get(games.size() - 1).getID() + 1;
		}

		plugin.getSettingsManager().getSpawns().set(("spawns." + no), null);
		c.set("sg-system.arenas." + no + ".world", max.getWorld().getName());
		c.set("sg-system.arenas." + no + ".x1", max.getBlockX());
		c.set("sg-system.arenas." + no + ".y1", max.getBlockY());
		c.set("sg-system.arenas." + no + ".z1", max.getBlockZ());
		c.set("sg-system.arenas." + no + ".x2", min.getBlockX());
		c.set("sg-system.arenas." + no + ".y2", min.getBlockY());
		c.set("sg-system.arenas." + no + ".z2", min.getBlockZ());
		c.set("sg-system.arenas." + no + ".enabled", true);

		plugin.getSettingsManager().saveSystemConfig();
		hotAddArena(no);
		pl.sendMessage(ChatColor.GREEN + "Arena ID " + no + " Succesfully added");
	}

	private void hotAddArena(int no)
	{
		Game game = new Game(this, no);
		games.add(game);
		plugin.getStatsManager().addArena(no);
	}

	public void hotRemoveArena(int no)
	{
		for (Game g : games.toArray(new Game[0]))
		{
			if (g.getID() == no)
			{
				games.remove(getGame(no));
			}
		}
	}

	public void gameEndCallBack(int id)
	{
		getGame(id).setRBStatus("clearing chest");
		openedChest.put(id, new HashSet<Block>());
	}

	public List<String> getStringList(int gid)
	{
		Game g = getGame(gid);
		Player[][] players = g.getPlayers();

		List<String> lines = new ArrayList<String>();
		StringBuilder line = new StringBuilder();

		line.append(ChatColor.GREEN + "<---------------------[ Alive: " + players[0].length + " ]--------------------->" + ChatColor.GREEN
				+ " ");
		lines.add(line.toString());

		line = new StringBuilder();
		if (players[0].length == 0)
		{
			line.append(ChatColor.GREEN + "None");
		}
		else
		{
			for (Player p : players[0])
			{
				line.append(ChatColor.GREEN + p.getName() + ", ");
			}
			line.delete(line.lastIndexOf(","), line.lastIndexOf(" "));
		}

		line.append(".");
		lines.add(line.toString());

		line = new StringBuilder();
		line.append(ChatColor.RED + "<---------------------[ Dead: " + players[1].length + " ]--------------------->" + ChatColor.RED + " ");
		lines.add(line.toString());

		line = new StringBuilder();
		if (players[1].length == 0)
		{
			line.append(ChatColor.RED + "None");
		}
		else
		{
			for (Player p : players[1])
			{
				line.append(ChatColor.RED + p.getName() + ", ");
			}
			line.delete(line.lastIndexOf(","), line.lastIndexOf(" "));
		}

		line.append(".");
		lines.add(line.toString());

		return lines;
	}
}