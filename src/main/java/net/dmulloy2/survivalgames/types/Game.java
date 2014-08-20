package net.dmulloy2.survivalgames.types;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import net.dmulloy2.survivalgames.SurvivalGames;
import net.dmulloy2.survivalgames.api.PlayerGameDeathEvent;
import net.dmulloy2.survivalgames.api.PlayerJoinArenaEvent;
import net.dmulloy2.survivalgames.api.PlayerWinEvent;
import net.dmulloy2.survivalgames.util.ItemReader;
import net.dmulloy2.survivalgames.util.Kit;
import net.dmulloy2.survivalgames.util.LocationUtil;
import net.dmulloy2.survivalgames.util.NameUtil;
import net.dmulloy2.survivalgames.util.SpectatorUtil;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Data container for a game
 */
public class Game
{
	public static enum GameMode
	{
		DISABLED, LOADING, INACTIVE, WAITING, STARTING, INGAME, FINISHING, RESETTING, ERROR
	}

	private GameMode mode = GameMode.DISABLED;
	private List<UUID> activePlayers = new ArrayList<>();
	private List<UUID> inactivePlayers = new ArrayList<>();
	private List<UUID> spectators = new ArrayList<>();
	private List<UUID> queue = new ArrayList<>();
	private Map<String, Object> flags = new HashMap<>();
	private Map<UUID, Integer> nextspec = new HashMap<>();
	private List<Integer> tasks = new ArrayList<>();

	private Arena arena;
	private int gameID;
	private int gcount = 0;
	private FileConfiguration config;
	private FileConfiguration system;
	private HashMap<Integer, UUID> spawns = new HashMap<>();
	private HashMap<UUID, ItemStack[][]> inv_store = new HashMap<>();
	private int spawnCount = 0;
	private int vote = 0;
	private boolean disabled = false;
	private int endgameTaskID = 0;
	private boolean endgameRunning = false;
	private double rbpercent = 0;
	private String rbstatus = "";
	private long startTime = 0;
	private boolean countdownRunning;

	private Map<String, String> hookvars = new HashMap<>();

	private final SurvivalGames plugin;
	public Game(SurvivalGames plugin, int gameID)
	{
		this.plugin = plugin;
		this.gameID = gameID;

		reloadConfig();
		setup();
	}

	public void reloadConfig()
	{
		config = plugin.getSettingsManager().getConfig();
		system = plugin.getSettingsManager().getSystemConfig();
	}

	public void $(String msg)
	{
		plugin.$(msg);
	}

	public void debug(String msg)
	{
		plugin.debug(msg);
	}

	// -------------------------//
	// Setup
	// -------------------------//
	public void setup()
	{
		mode = GameMode.LOADING;
		int x = system.getInt("sg-system.arenas." + gameID + ".x1");
		int y = system.getInt("sg-system.arenas." + gameID + ".y1");
		int z = system.getInt("sg-system.arenas." + gameID + ".z1");
		int x1 = system.getInt("sg-system.arenas." + gameID + ".x2");
		int y1 = system.getInt("sg-system.arenas." + gameID + ".y2");
		int z1 = system.getInt("sg-system.arenas." + gameID + ".z2");

		Location max = new Location(plugin.getSettingsManager().getGameWorld(gameID), Math.max(x, x1), Math.max(y, y1), Math.max(z, z1));
		debug("[Max] " + LocationUtil.locToString(max));
		Location min = new Location(plugin.getSettingsManager().getGameWorld(gameID), Math.min(x, x1), Math.min(y, y1), Math.min(z, z1));
		debug("[Min] " + LocationUtil.locToString(min));

		arena = new Arena(min, max);

		loadspawns();

		hookvars.put("arena", gameID + "");
		hookvars.put("maxplayers", spawnCount + "");
		hookvars.put("activeplayers", "0");

		mode = GameMode.WAITING;

		plugin.getLobbyManager().updateWall(gameID);
	}

	public void reloadFlags()
	{
		flags = plugin.getSettingsManager().getGameFlags(gameID);
	}

	public void saveFlags()
	{
		plugin.getSettingsManager().saveGameFlags(flags, gameID);
	}

	public void loadspawns()
	{
		for (int a = 1; a <= plugin.getSettingsManager().getSpawnCount(gameID); a++)
		{
			spawns.put(a, null);
			spawnCount = a;
		}
	}

	public void addSpawn()
	{
		spawnCount++;
		spawns.put(spawnCount, null);
	}

	public void setMode(GameMode m)
	{
		mode = m;
	}

	public GameMode getGameMode()
	{
		return mode;
	}

	public Arena getArena()
	{
		return arena;
	}

	// -------------------------//
	// Enable
	// -------------------------//
	public void enable()
	{
		mode = GameMode.WAITING;
		if (disabled)
		{
			plugin.getMessageHandler().broadcastFMessage(Prefix.INFO, "broadcast.gameenabled", "arena-" + gameID);
		}
		disabled = false;
		int b = (plugin.getSettingsManager().getSpawnCount(gameID) > queue.size()) ? queue.size() : plugin.getSettingsManager()
				.getSpawnCount(gameID);
		for (int a = 0; a < b; a++)
		{
			addPlayer(plugin.getServer().getPlayer(queue.remove(0)));
		}
		int c = 1;
		for (UUID id : queue)
		{
			Player player = plugin.getServer().getPlayer(id);
			plugin.getMessageHandler().sendMessage(Prefix.INFO, "You are now #" + c + " in line for arena " + gameID, player);
			c++;
		}

		plugin.getLobbyManager().updateWall(gameID);

		plugin.getMessageHandler().broadcastFMessage(Prefix.INFO, "broadcast.gamewaiting", "arena-" + gameID);
	}

	// -------------------------//
	// Add Player
	// -------------------------//
	public boolean addPlayer(Player p)
	{
		UUID id = p.getUniqueId();
		if (plugin.getSettingsManager().getLobbySpawn() == null)
		{
			plugin.getMessageHandler().sendFMessage(Prefix.WARNING, "error.nolobbyspawn", p);
			return false;
		}

		if (! canJoinArena(p, gameID))
		{
			debug("permission needed to join arena: " + "sg.arena.join." + gameID);
			plugin.getMessageHandler().sendFMessage(Prefix.WARNING, "game.nopermission", p, "arena-" + gameID);
			return false;
		}

		plugin.getHookManager().runHook("GAME_PRE_ADDPLAYER", "arena-" + gameID, "player-" + p.getName(), "maxplayers-" + spawns.size(),
				"players-" + activePlayers.size());

		plugin.getGameManager().removeFromOtherQueues(p, gameID);

		if (plugin.getGameManager().getPlayerGameId(p) != -1)
		{
			if (plugin.getGameManager().isPlayerActive(p))
			{
				plugin.getMessageHandler().sendMessage(Prefix.ERROR, "Cannot join multiple games!", p);
				return false;
			}
		}

		if (p.isInsideVehicle())
			p.leaveVehicle();

		if (spectators.contains(id))
			removeSpectator(p);

		if (mode == GameMode.WAITING || mode == GameMode.STARTING)
		{
			if (activePlayers.size() < plugin.getSettingsManager().getSpawnCount(gameID))
			{
				plugin.getMessageHandler().sendMessage(Prefix.INFO, "Joining Arena " + gameID, p);
				PlayerJoinArenaEvent joinarena = new PlayerJoinArenaEvent(p, plugin.getGameManager().getGame(gameID));
				plugin.getServer().getPluginManager().callEvent(joinarena);
				boolean placed = false;
				int spawnCount = plugin.getSettingsManager().getSpawnCount(gameID);

				for (int a = 1; a <= spawnCount; a++)
				{
					if (spawns.get(a) == null)
					{
						placed = true;
						spawns.put(a, id);
						p.setGameMode(org.bukkit.GameMode.SURVIVAL);

						p.teleport(plugin.getSettingsManager().getLobbySpawn());
						saveInv(p);
						clearInv(p);
						p.teleport(plugin.getSettingsManager().getSpawnPoint(gameID, a));

						p.setHealth(p.getMaxHealth());
						p.setFoodLevel(20);
						clearInv(p);

						activePlayers.add(id);
						plugin.getStatsManager().addPlayer(p, gameID);

						hookvars.put("activeplayers", activePlayers.size() + "");
						plugin.getLobbyManager().updateWall(gameID);
						showMenu(p);
						plugin.getHookManager().runHook("GAME_POST_ADDPLAYER", "activePlayers-" + activePlayers.size());

						if (spawnCount == activePlayers.size())
						{
							countdown(5);
						}

						break;
					}
				}

				if (! placed)
				{
					plugin.getMessageHandler().sendFMessage(Prefix.ERROR, "error.gamefull", p, "arena-" + gameID);
					return false;
				}

			}
			else if (plugin.getSettingsManager().getSpawnCount(gameID) == 0)
			{
				plugin.getMessageHandler().sendMessage(Prefix.WARNING, "No spawns set for Arena " + gameID + "!", p);
				return false;
			}
			else
			{
				plugin.getMessageHandler().sendFMessage(Prefix.WARNING, "error.gamefull", p, "arena-" + gameID);
				return false;
			}

			msgFall(Prefix.INFO, "game.playerjoingame", "player-" + p.getName(), "activeplayers-" + getActivePlayers(), "maxplayers-"
					+ plugin.getSettingsManager().getSpawnCount(gameID));
			if (activePlayers.size() >= config.getInt("auto-start-players") && ! countdownRunning)
				countdown(config.getInt("auto-start-time"));

			return true;
		}
		else
		{
			if (config.getBoolean("enable-player-queue"))
			{
				if (! queue.contains(id))
				{
					queue.add(id);
					plugin.getMessageHandler().sendFMessage(Prefix.INFO, "game.playerjoinqueue", p, "queuesize-" + queue.size());
				}
				int a = 1;
				for (UUID qId : queue)
				{
					Player qp = plugin.getServer().getPlayer(qId);
					if (qp == p)
					{
						plugin.getMessageHandler().sendFMessage(Prefix.INFO, "game.playercheckqueue", p, "queuepos-" + a);
						break;
					}
					a++;
				}
			}
		}

		if (mode == GameMode.INGAME)
		{
			plugin.getMessageHandler().sendFMessage(Prefix.WARNING, "error.alreadyingame", p);
		}
		else if (mode == GameMode.DISABLED)
		{
			plugin.getMessageHandler().sendFMessage(Prefix.WARNING, "error.gamedisabled", p, "arena-" + gameID);
		}
		else if (mode == GameMode.RESETTING)
		{
			plugin.getMessageHandler().sendFMessage(Prefix.WARNING, "error.gameresetting", p);
		}
		else
		{
			plugin.getMessageHandler().sendMessage(Prefix.INFO, "Cannot join game!", p);
		}

		plugin.getLobbyManager().updateWall(gameID);
		return false;
	}

	public boolean canJoinArena(Player p, int gameId)
	{
		return p.hasPermission("sg.arenas.join." + gameId) || p.hasPermission("sg.arenas.join.*") || p.isOp();
	}

	// -------------------------//
	// Kit Menu
	// -------------------------//
	public void showMenu(Player p)
	{
		plugin.getGameManager().openKitMenu(p);
		Inventory i = plugin.getServer().createInventory(p, 90, ChatColor.RED + "" + ChatColor.BOLD + "Kit Selection");

		int a = 0;
		int b = 0;

		List<Kit> kits = plugin.getGameManager().getKits(p);
		plugin.debug(kits + "");
		if (kits == null || kits.size() == 0 || ! plugin.getSettingsManager().getKits().getBoolean("enabled"))
		{
			plugin.getGameManager().leaveKitMenu(p);
			return;
		}

		for (Kit k : kits)
		{
			ItemStack i1 = k.getIcon();
			ItemMeta im = i1.getItemMeta();

			debug(k.getName() + " " + i1 + " " + im);

			im.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + k.getName());
			i1.setItemMeta(im);
			i.setItem((9 * a) + b, i1);
			a = 2;

			for (ItemStack s2 : k.getContents())
			{
				if (s2 != null)
				{
					i.setItem((9 * a) + b, s2);
					a++;
				}
			}

			a = 0;
			b++;
		}

		p.openInventory(i);
		debug("Showing menu");
	}

	// -------------------------//
	// Remove from Queue
	// -------------------------//
	public void removeFromQueue(Player p)
	{
		queue.remove(p);
	}

	// --------------------------//
	// Vote
	// -------------------------//
	private final List<UUID> voted = new ArrayList<>();

	public void vote(Player pl)
	{
		if (GameMode.STARTING == mode)
		{
			plugin.getMessageHandler().sendMessage(Prefix.WARNING, "Game already starting!", pl);
			return;
		}
		if (GameMode.WAITING != mode)
		{
			plugin.getMessageHandler().sendMessage(Prefix.WARNING, "Game already started!", pl);
			return;
		}
		if (voted.contains(pl.getUniqueId()))
		{
			plugin.getMessageHandler().sendMessage(Prefix.WARNING, "You already voted!", pl);
			return;
		}

		vote++;
		voted.add(pl.getUniqueId());

		for (UUID uuid : activePlayers)
		{
			Player player = plugin.getServer().getPlayer(uuid);
			plugin.getMessageHandler().sendFMessage(Prefix.INFO, "game.playervote", player, "player-" + pl.getName(), "voted-" + vote,
					"players-" + getActivePlayers());
		}

		plugin.getHookManager().runHook("PLAYER_VOTE", "player-" + pl.getName());

		if ((((vote + 0.0) / (getActivePlayers() + 0.0)) >= (config.getInt("auto-start-vote") + 0.0) / 100) && getActivePlayers() > 1)
		{
			countdown(config.getInt("auto-start-time"));
		}
	}

	// --------------------------//
	// Start the game
	// -------------------------//
	public void startGame()
	{
		if (mode == GameMode.INGAME)
		{
			return;
		}

		if (activePlayers.size() <= 1)
		{
			for (UUID id : activePlayers)
			{
				Player pl = plugin.getServer().getPlayer(id);
				plugin.getMessageHandler().sendMessage(Prefix.WARNING, "Not enough players!", pl);
				mode = GameMode.WAITING;
				plugin.getLobbyManager().updateWall(gameID);
			}

			return;
		}
		else
		{
			startTime = new Date().getTime();

			for (UUID id : activePlayers)
			{
				Player pl = plugin.getServer().getPlayer(id);
				pl.setHealth(20.0D);
				pl.setFoodLevel(20);
				pl.setFireTicks(0);

				plugin.getMessageHandler().sendFMessage(Prefix.INFO, "game.goodluck", pl);
			}

			if (config.getBoolean("restock-chest"))
			{
				plugin.getSettingsManager().getGameWorld(gameID).setTime(0);
				tasks.add(new NightChecker().runTaskLater(plugin, 14400).getTaskId());

				gcount++;
			}

			if (config.getInt("grace-period") != 0)
			{
				for (UUID id : activePlayers)
				{
					Player play = plugin.getServer().getPlayer(id);
					plugin.getMessageHandler().sendMessage(Prefix.INFO,
							"You have a " + config.getInt("grace-period") + " second grace period!", play);
				}

				new BukkitRunnable()
				{
					@Override
					public void run()
					{
						for (UUID id : activePlayers)
						{
							Player play = plugin.getServer().getPlayer(id);
							plugin.getMessageHandler().sendMessage(Prefix.INFO, "Grace period has ended!", play);
						}
					}
				}.runTaskLater(plugin, config.getInt("grace-period") * 20);
			}

			if (config.getBoolean("deathmatch.enabled"))
			{
				tasks.add(new DeathMatch().runTaskLater(plugin, config.getInt("deathmatch.time") * 20 * 60).getTaskId());
			}
		}

		mode = GameMode.INGAME;
		plugin.getLobbyManager().updateWall(gameID);
		plugin.getMessageHandler().broadcastFMessage(Prefix.INFO, "broadcast.gamestarted", "arena-" + gameID);
	}

	// -------------------------//
	// Countdowns
	// -------------------------//
	public int getCountdownTime()
	{
		return count;
	}

	int count = 20;
	int tid = 0;

	public void countdown(int time)
	{
		plugin.getMessageHandler().broadcastFMessage(Prefix.INFO, "broadcast.gamestarting", "arena-" + gameID, "t-" + time);
		countdownRunning = true;
		count = time;
		plugin.getServer().getScheduler().cancelTask(tid);

		if (mode == GameMode.WAITING || mode == GameMode.STARTING)
		{
			mode = GameMode.STARTING;
			tid = new BukkitRunnable()
			{
				@Override
				public void run()
				{
					if (count > 0)
					{
						if (count % 10 == 0)
						{
							msgFall(Prefix.INFO, "game.countdown", "t-" + count);
						}
						if (count < 6)
						{
							msgFall(Prefix.INFO, "game.countdown", "t-" + count);

						}
						count--;
						plugin.getLobbyManager().updateWall(gameID);
					}
					else
					{
						startGame();
						plugin.getServer().getScheduler().cancelTask(tid);
						countdownRunning = false;
					}
				}
			}.runTaskTimer(plugin, 0, 20).getTaskId();
		}
	}

	// -------------------------//
	// Remove a player
	// -------------------------//
	public void removePlayer(Player p, boolean left)
	{
		p.teleport(plugin.getSettingsManager().getLobbySpawn());
		if (mode == GameMode.INGAME)
		{
			killPlayer(p, left);
		}
		else
		{
			plugin.getStatsManager().removePlayer(p, gameID);
			restoreInv(p);
			activePlayers.remove(p);
			inactivePlayers.remove(p);

			for (Entry<Integer, UUID> entry : new HashMap<>(spawns).entrySet())
			{
				if (p.equals(entry.getValue()))
				{
					spawns.remove(entry.getKey());
				}
			}

			plugin.getLobbyManager().clearSigns(gameID);

			msgFall(Prefix.INFO, "game.playerleavegame", "player-" + p.getName());
		}

		plugin.getHookManager().runHook("PLAYER_REMOVED", "player-" + p.getName());
		plugin.getLobbyManager().updateWall(gameID);
	}

	// -------------------------//
	// Kill a player
	// -------------------------//
	public void killPlayer(Player p, boolean left)
	{
		clearInv(p);

		if (! left)
		{
			p.teleport(plugin.getSettingsManager().getLobbySpawn());
		}

		plugin.getStatsManager().playerDied(p, activePlayers.size(), gameID, new Date().getTime() - startTime);

		if (! activePlayers.contains(p))
			return;

		restoreInv(p);
		activePlayers.remove(p.getUniqueId());
		inactivePlayers.add(p.getUniqueId());

		if (left)
		{
			PlayerGameDeathEvent leavearena = new PlayerGameDeathEvent(p, p, this);
			plugin.getServer().getPluginManager().callEvent(leavearena);
			msgFall(Prefix.INFO, "game.playerleavegame", "player-" + p.getName());
		}
		else
		{
			if (mode != GameMode.WAITING && p.getLastDamageCause() != null && p.getLastDamageCause().getCause() != null)
			{
				switch (p.getLastDamageCause().getCause())
				{
					case ENTITY_ATTACK:
						if (p.getLastDamageCause().getEntityType() == EntityType.PLAYER)
						{
							Player killer = p.getKiller();
							PlayerGameDeathEvent leavearena = new PlayerGameDeathEvent(p, killer, this);
							plugin.getServer().getPluginManager().callEvent(leavearena);
							msgFall(Prefix.INFO,
									"death." + p.getLastDamageCause().getEntityType(),
									"player-" + (NameUtil.auth.contains(p.getName()) ? ChatColor.DARK_RED + "" + ChatColor.BOLD : "")
											+ p.getName(), "killer-"
											+ ((killer != null) ? (NameUtil.auth.contains(killer.getName()) ? ChatColor.DARK_RED + ""
													+ ChatColor.BOLD : "")
													+ killer.getName() : "Unknown"),
									"item-"
											+ ((killer != null) ? ItemReader.getFriendlyName(killer.getItemInHand().getType())
													: "Unknown Item"));
							if (killer != null && p != null)
								plugin.getStatsManager().addKill(killer, p, gameID);
						}
						else
						{
							msgFall(Prefix.INFO,
									"death." + p.getLastDamageCause().getEntityType(),
									"player-" + (NameUtil.auth.contains(p.getName()) ? ChatColor.DARK_RED + "" + ChatColor.BOLD : "")
											+ p.getName(), "killer-" + p.getLastDamageCause().getEntityType());
						}
						break;
					case FIRE:
					case FIRE_TICK:
					case MAGIC:
					case THORNS:
					case PROJECTILE:
						if (p.getKiller() != null)
						{
							Player killer = p.getKiller();
							PlayerGameDeathEvent leavearena = new PlayerGameDeathEvent(p, killer, this);
							plugin.getServer().getPluginManager().callEvent(leavearena);
							msgFall(Prefix.INFO,
									"death." + p.getLastDamageCause().getCause(),
									"player-" + (NameUtil.auth.contains(p.getName()) ? ChatColor.DARK_RED + "" + ChatColor.BOLD : "")
											+ p.getName(), "killer-" + p.getKiller().getName());
							if (killer != null && p != null)
								plugin.getStatsManager().addKill(killer, p, gameID);
						}
						else
						{
							msgFall(Prefix.INFO,
									"death." + p.getLastDamageCause().getCause(),
									"player-" + (NameUtil.auth.contains(p.getName()) ? ChatColor.DARK_RED + "" + ChatColor.BOLD : "")
											+ p.getName(), "killer-" + p.getLastDamageCause().getCause());
						}
						break;
					default:
						msgFall(Prefix.INFO, "death." + p.getLastDamageCause().getCause(), "player-"
								+ (NameUtil.auth.contains(p.getName()) ? ChatColor.DARK_RED + "" + ChatColor.BOLD : "") + p.getName(),
								"killer-" + p.getLastDamageCause().getCause());
						break;
				}

				if (getActivePlayers() > 1)
				{
					for (Player pl : getAllPlayers())
					{
						plugin.getMessageHandler().sendMessage(
								Prefix.INFO,
								ChatColor.DARK_AQUA + "There are " + ChatColor.YELLOW + "" + getActivePlayers() + ChatColor.DARK_AQUA
										+ " players remaining!", pl);
					}
				}
			}
		}

		for (UUID id : activePlayers)
		{
			Player pe = plugin.getServer().getPlayer(id);
			Location l = pe.getLocation().clone();
			l.setY(l.getWorld().getMaxHeight());
			l.getWorld().strikeLightningEffect(l);
		}

		if (getActivePlayers() <= config.getInt("endgame.players") && config.getBoolean("endgame.fire-lighting.enabled")
				&& ! endgameRunning)
		{
			tasks.add(new EndgameManager().runTaskTimer(plugin, 0, config.getInt("endgame.fire-lighting.interval") * 20).getTaskId());
		}

		if (activePlayers.size() < 2 && mode != GameMode.WAITING)
		{
			playerWin(p);
			endGame();
		}

		plugin.getLobbyManager().updateWall(gameID);
	}

	// -------------------------//
	// Player win
	// -------------------------//
	public void playerWin(Player victim)
	{
		if (GameMode.DISABLED == mode)
			return;

		UUID uuid = activePlayers.get(0);
		Player win = plugin.getServer().getPlayer(uuid);
		win.teleport(plugin.getSettingsManager().getLobbySpawn());
		restoreInv(win);
		plugin.getMessageHandler().broadcastFMessage(Prefix.INFO, "game.playerwin", "arena-" + gameID, "victim-" + victim.getName(),
				"player-" + win.getName());
		plugin.getLobbyManager().display(new String[]
		{
				win.getName(), "", "Won the ", "Survival Games!"
		}, gameID);

		mode = GameMode.FINISHING;
		if (config.getBoolean("reward.enabled", false))
		{
			List<String> items = config.getStringList("reward.contents");
			for (String s : items)
			{
				ItemStack item = ItemReader.read(s);
				win.getInventory().addItem(item);
			}
		}

		clearSpecs();
		win.setHealth(win.getMaxHealth());
		win.setFoodLevel(20);
		win.setFireTicks(0);
		win.setFallDistance(0);

		PlayerWinEvent winEvent = new PlayerWinEvent(win, victim, this);
		plugin.getServer().getPluginManager().callEvent(winEvent);

		plugin.getStatsManager().playerWin(win, gameID, new Date().getTime() - startTime);
		plugin.getStatsManager().saveGame(gameID, win, getActivePlayers() + getInactivePlayers(), new Date().getTime() - startTime);

		activePlayers.clear();
		inactivePlayers.clear();
		spawns.clear();

		loadspawns();
		plugin.getLobbyManager().updateWall(gameID);
		plugin.getMessageHandler().broadcastFMessage(Prefix.INFO, "broadcast.gameend", "arena-" + gameID);
	}

	// -------------------------//
	// End Game
	// -------------------------//
	public void endGame()
	{
		mode = GameMode.WAITING;

		resetArena();

		plugin.getLobbyManager().clearSigns(gameID);
		plugin.getLobbyManager().updateWall(gameID);
	}

	// -------------------------//
	// Disable
	// -------------------------//
	public void disable()
	{
		disabled = true;
		spawns.clear();

		// Alert the players
		for (UUID id : new ArrayList<>(activePlayers))
		{
			try
			{
				Player p = plugin.getServer().getPlayer(id);
				plugin.getMessageHandler().sendMessage(Prefix.WARNING, "Game disabled!", p);
				removePlayer(p, false);
			}
			catch (Throwable ex)
			{
				//
			}
		}

		for (UUID id : new ArrayList<>(inactivePlayers))
		{
			try
			{
				Player p = plugin.getServer().getPlayer(id);
				plugin.getMessageHandler().sendMessage(Prefix.WARNING, "Game disabled!", p);
			}
			catch (Throwable ex)
			{
				//
			}
		}

		// Clear lists
		activePlayers.clear();
		inactivePlayers.clear();

		clearSpecs();
		queue.clear();

		endGame();
		plugin.getLobbyManager().updateWall(gameID);
		plugin.getMessageHandler().broadcastFMessage(Prefix.INFO, "broadcast.gamedisabled", "arena-" + gameID);
	}

	// -------------------------//
	// Reset
	// -------------------------//
	public void resetArena()
	{
		for (Integer i : tasks)
		{
			plugin.getServer().getScheduler().cancelTask(i);
		}

		tasks.clear();
		vote = 0;
		voted.clear();

		mode = GameMode.RESETTING;
		endgameRunning = false;

		plugin.getServer().getScheduler().cancelTask(endgameTaskID);
		plugin.getGameManager().gameEndCallBack(gameID);
		plugin.getQueueManager().rollback(gameID);
		plugin.getLobbyManager().updateWall(gameID);
	}

	public void resetCallback()
	{
		if (! disabled)
		{
			enable();
		}
		else
		{
			mode = GameMode.DISABLED;
		}

		plugin.getLobbyManager().updateWall(gameID);
	}

	// -------------------------//
	// Save a player's inventory
	// -------------------------//
	public void saveInv(Player p)
	{
		ItemStack[][] store = new ItemStack[2][1];

		store[0] = p.getInventory().getContents();
		store[1] = p.getInventory().getArmorContents();

		inv_store.put(p.getUniqueId(), store);
	}

	public void restoreInvOffline(String p)
	{
		restoreInv(plugin.getServer().getPlayer(p));
	}

	// -------------------------//
	// Spectating
	// -------------------------//
	public void addSpectator(Player p)
	{
		if (mode != GameMode.INGAME)
		{
			plugin.getMessageHandler().sendMessage(Prefix.WARNING, "You can only spectate running games!", p);
			return;
		}

		saveInv(p);
		clearInv(p);
		p.teleport(plugin.getSettingsManager().getSpawnPoint(gameID, 1).add(0, 10, 0));

		plugin.getHookManager().runHook("PLAYER_SPECTATE", "player-" + p.getName());

		for (Player pl : plugin.getServer().getOnlinePlayers())
		{
			pl.hidePlayer(p);
		}

		p.setAllowFlight(true);
		p.setFlying(true);
		spectators.add(p.getUniqueId());

		addItem(p, new ItemStack(Material.COMPASS));

		SpectatorUtil.addSpectator(p, this);
	}

	public List<Player> getActivePlayerList()
	{
		List<Player> ret = new ArrayList<>();

		for (UUID id : activePlayers)
		{
			Player player = plugin.getServer().getPlayer(id);
			if (player != null)
				ret.add(player);
		}

		return ret;
	}

	public List<Player> getInactivePlayerList()
	{
		List<Player> ret = new ArrayList<>();

		for (UUID id : inactivePlayers)
		{
			Player player = plugin.getServer().getPlayer(id);
			if (player != null)
				ret.add(player);
		}

		return ret;
	}

	public void addItem(Player p, ItemStack stack)
	{
		p.getInventory().addItem(stack);
		p.updateInventory();
	}

	public void removeSpectator(Player player)
	{
		if (player.isOnline())
		{
			for (Player pl : plugin.getServer().getOnlinePlayers())
			{
				pl.showPlayer(player);
			}
		}

		clearInv(player);
		restoreInv(player);
		player.setAllowFlight(false);
		player.setFlying(false);
		player.setFallDistance(0);
		player.setHealth(player.getMaxHealth());
		player.setFoodLevel(20);
		player.setSaturation(20);
		player.teleport(plugin.getSettingsManager().getLobbySpawn());

		SpectatorUtil.removeSpectator(player);
	}

	public void clearSpecs()
	{
		for (UUID spectator : new ArrayList<>(spectators))
		{
			Player player = plugin.getServer().getPlayer(spectator);
			if (player != null)
				removeSpectator(player);
		}

		spectators.clear();
		nextspec.clear();
	}

	public Map<UUID, Integer> getNextSpec()
	{
		return nextspec;
	}

	// -------------------------//
	// Inventory Work
	// -------------------------//
	public void restoreInv(Player p)
	{
		try
		{
			clearInv(p);
			p.getInventory().setContents(inv_store.get(p)[0]);
			p.getInventory().setArmorContents(inv_store.get(p)[1]);
			inv_store.remove(p);
			p.updateInventory();
		} catch (Throwable ex) { }
	}

	public void clearInv(Player p)
	{
		ItemStack[] inv = p.getInventory().getContents();
		for (int i = 0; i < inv.length; i++)
		{
			inv[i] = null;
		}
		p.getInventory().setContents(inv);
		inv = p.getInventory().getArmorContents();
		for (int i = 0; i < inv.length; i++)
		{
			inv[i] = null;
		}
		p.getInventory().setArmorContents(inv);
		p.updateInventory();
	}

	// -------------------------//
	// Check for night
	// -------------------------//
	public class NightChecker extends BukkitRunnable
	{
		boolean reset = false;
		int tgc = gcount;

		@Override
		public void run()
		{
			if (plugin.getSettingsManager().getGameWorld(gameID).getTime() > 14000)
			{
				for (UUID id : new ArrayList<>(activePlayers))
				{
					Player pl = plugin.getServer().getPlayer(id);
					plugin.getMessageHandler().sendMessage(Prefix.INFO, "Chests restocked!", pl);
				}

				plugin.getGameManager().getOpenedChest().get(gameID).clear();
				reset = true;
			}
		}
	}

	// -------------------------//
	// Ending Games
	// -------------------------//
	public class EndgameManager extends BukkitRunnable
	{
		@Override
		public void run()
		{
			for (UUID uuid : new ArrayList<>(activePlayers))
			{
				Player player = plugin.getServer().getPlayer(uuid);
				Location l = player.getLocation();
				l.add(0, 5, 0);
				player.getWorld().strikeLightningEffect(l);
			}

		}
	}

	// -------------------------//
	// Death Match
	// -------------------------//
	public class DeathMatch extends BukkitRunnable
	{
		@Override
		public void run()
		{
			for (UUID id : new ArrayList<>(activePlayers))
			{
				for (Entry<Integer, UUID> entry : new HashMap<>(spawns).entrySet())
				{
					if (entry.getValue() == id)
					{
						Player player = plugin.getServer().getPlayer(id);
						player.teleport(plugin.getSettingsManager().getSpawnPoint(gameID, entry.getKey()));
						break;
					}
				}
			}

			tasks.add(new BukkitRunnable()
			{
				@Override
				public void run()
				{
					for (UUID id : new ArrayList<>(activePlayers))
					{
						Player p = plugin.getServer().getPlayer(id);
						p.getLocation().getWorld().strikeLightning(p.getLocation());
					}
				}
			}.runTaskLater(plugin, config.getInt("deathmatch.killtime") * 20 * 60).getTaskId());
		}
	}

	public boolean isBlockInArena(Location v)
	{
		return arena.containsBlock(v);
	}

	public boolean isProtectionOn()
	{
		long t = startTime / 1000;
		long l = config.getLong("grace-period");
		long d = new Date().getTime() / 1000;
		return ((d - t) < l);
	}

	public int getID()
	{
		return gameID;
	}

	public int getActivePlayers()
	{
		return activePlayers.size();
	}

	public int getInactivePlayers()
	{
		return inactivePlayers.size();
	}

	public Player[][] getPlayers()
	{
		return new Player[][]
		{
				getActivePlayerList().toArray(new Player[0]), getInactivePlayerList().toArray(new Player[0])
		};
	}

	public List<Player> getAllPlayers()
	{
		List<Player> all = new ArrayList<Player>();
		all.addAll(getActivePlayerList());
		all.addAll(getInactivePlayerList());
		return all;
	}

	public boolean isSpectator(Player p)
	{
		return spectators.contains(p.getUniqueId());
	}

	public boolean isInQueue(Player p)
	{
		return queue.contains(p.getUniqueId());
	}

	public boolean isPlayerActive(Player player)
	{
		return activePlayers.contains(player.getUniqueId());
	}

	public boolean isPlayerInactive(Player player)
	{
		return inactivePlayers.contains(player.getUniqueId());
	}

	public boolean hasPlayer(Player p)
	{
		return activePlayers.contains(p.getUniqueId()) || inactivePlayers.contains(p.getUniqueId());
	}

	public GameMode getMode()
	{
		return mode;
	}

	public synchronized void setRBPercent(double d)
	{
		rbpercent = d;
	}

	public double getRBPercent()
	{
		return rbpercent;
	}

	public void setRBStatus(String s)
	{
		rbstatus = s;
	}

	public String getRBStatus()
	{
		return rbstatus;
	}

	public String getName()
	{
		return "Arena " + gameID;
	}

	public void msgFall(Prefix type, String msg, String... vars)
	{
		for (Player p : getAllPlayers())
		{
			plugin.getMessageHandler().sendFMessage(type, msg, p, vars);
		}
	}
}