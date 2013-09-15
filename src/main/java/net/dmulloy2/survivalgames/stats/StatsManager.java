package net.dmulloy2.survivalgames.stats;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;

import net.dmulloy2.survivalgames.SurvivalGames;
import net.dmulloy2.survivalgames.managers.DatabaseManager;
import net.dmulloy2.survivalgames.managers.MessageManager;
import net.dmulloy2.survivalgames.managers.MessageManager.PrefixType;
import net.dmulloy2.survivalgames.types.Game;

import org.bukkit.entity.Player;

public class StatsManager
{
	private ArrayList<PreparedStatement> queue = new ArrayList<PreparedStatement>();
	private DatabaseDumper dumper = new DatabaseDumper();

	private HashMap<Integer, HashMap<Player, PlayerStatsSession>> arenas = new HashMap<Integer, HashMap<Player, PlayerStatsSession>>();

	private boolean enabled = true;

	private DatabaseManager dbman;
	private MessageManager msgmgr;

	private final SurvivalGames plugin;

	public StatsManager(SurvivalGames plugin)
	{
		this.plugin = plugin;

		this.msgmgr = plugin.getMessageManager();
		this.dbman = plugin.getDatabaseManager();
	}

	public void setup(boolean b)
	{
		enabled = b;
		if (b)
		{
			try
			{
				PreparedStatement s = dbman
						.createStatement(" CREATE TABLE "
								+ plugin.getSettingsManager().getSqlPrefix()
								+ "playerstats(id int NOT NULL AUTO_INCREMENT PRIMARY KEY, gameno int,arenaid int, player text, points int,position int,"
								+ " kills int, death int, killed text,time int, ks1 int, ks2 int,ks3 int, ks4 int, ks5 int)");

				PreparedStatement s1 = dbman.createStatement(" CREATE TABLE " + plugin.getSettingsManager().getSqlPrefix()
						+ "gamestats(gameno int NOT NULL AUTO_INCREMENT PRIMARY KEY, arenaid int, players int, winner text, time int )");

				DatabaseMetaData dbm = dbman.getMysqlConnection().getMetaData();
				ResultSet tables = dbm.getTables(null, null, plugin.getSettingsManager().getSqlPrefix() + "playerstats", null);
				ResultSet tables1 = dbm.getTables(null, null, plugin.getSettingsManager().getSqlPrefix() + "gamestats", null);

				if (!tables.next())
				{
					s.execute();
				}

				if (!tables1.next())
				{
					s1.execute();
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public void addArena(int arenaid)
	{
		arenas.put(arenaid, new HashMap<Player, PlayerStatsSession>());
	}

	public void addPlayer(Player p, int arenaid)
	{
		arenas.get(arenaid).put(p, new PlayerStatsSession(plugin, p, arenaid));
	}

	public void removePlayer(Player p, int id)
	{
		arenas.get(id).remove(p);
	}

	public void playerDied(Player p, int pos, int arenaid, long time)
	{
		arenas.get(arenaid).get(p).died(pos, time);
	}

	public void playerWin(Player p, int arenaid, long time)
	{
		arenas.get(arenaid).get(p).win(time);
	}

	public PlayerStatsSession getPlayerStatsSession(Player p, int arenaid)
	{
		return arenas.get(arenaid).get(p);
	}

	public void addKill(Player p, Player killed, int arenaid)
	{
		PlayerStatsSession s = arenas.get(arenaid).get(p);

		if (s == null)
			return;

		int kslevel = s.addKill(killed);
		if (kslevel > 3)
		{
			msgmgr.broadcastFMessage(PrefixType.INFO, "killstreak.level" + ((kslevel > 5) ? 5 : kslevel), "player-" + p.getName());
		}
		else if (kslevel > 0)
		{
			for (Player pl : plugin.getGameManager().getGame(arenaid).getAllPlayers())
			{
				msgmgr.sendFMessage(PrefixType.INFO, "killstreak.level" + ((kslevel > 5) ? 5 : kslevel), pl, "player-" + p.getName());
			}
		}
	}

	public void saveGame(int arenaid, Player winner, int players, long time)
	{
		if (!enabled)
			return;

		int gameno = 0;
		Game g = plugin.getGameManager().getGame(arenaid);

		try
		{
			long time1 = new Date().getTime();
			PreparedStatement s2 = dbman.createStatement("SELECT * FROM " + plugin.getSettingsManager().getSqlPrefix()
					+ "gamestats ORDER BY gameno DESC LIMIT 1");
			ResultSet rs = s2.executeQuery();
			rs.next();
			gameno = rs.getInt(1) + 1;

			if (time1 + 5000 < new Date().getTime())
			{
				plugin.$(Level.WARNING, "Your database took a long time to respond. Check the connection between the server and database");
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			g.setRBStatus("Error: getno");
		}

		addSQL("INSERT INTO " + plugin.getSettingsManager().getSqlPrefix() + "gamestats VALUES(NULL," + arenaid + "," + players + ",'"
				+ winner.getName() + "'," + time + ")");

		for (PlayerStatsSession s : arenas.get(arenaid).values())
		{
			s.setGameID(gameno);
			addSQL(s.createQuery());
		}
		arenas.get(arenaid).clear();
	}

	private void addSQL(String query)
	{
		addSQL(dbman.createStatement(query));
	}

	private void addSQL(PreparedStatement s)
	{
		queue.add(s);
		if (!dumper.isAlive())
		{
			dumper = new DatabaseDumper();
			dumper.start();
		}
	}

	class DatabaseDumper extends Thread
	{
		@Override
		public void run()
		{
			while (queue.size() > 0)
			{
				PreparedStatement s = queue.remove(0);
				try
				{
					s.execute();
				}
				catch (Exception e)
				{
					dbman.connect();
				}
			}
		}
	}
}