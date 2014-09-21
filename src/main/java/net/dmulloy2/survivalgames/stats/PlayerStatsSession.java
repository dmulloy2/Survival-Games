package net.dmulloy2.survivalgames.stats;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import net.dmulloy2.survivalgames.SurvivalGames;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class PlayerStatsSession
{
	public Player player;
	public int kills = 0, death = 0, gameno, arenaid, points = 0;
	public int finish = 0;
	long time = 0;
	int ksbon = 0;
	long lastkill = 0;
	int kslevel = 0;
	int score = 0;
	int position = 0;
	int pppoints = 0;

	private List<String> killed = new ArrayList<>();

	private HashMap<Integer, Integer> kslist = new HashMap<Integer, Integer>();

	private final SurvivalGames plugin;
	public PlayerStatsSession(SurvivalGames plugin, Player p, int arenaid)
	{
		this.plugin = plugin;

		this.player = p;
		this.arenaid = arenaid;

		kslist.put(1, 0);
		kslist.put(2, 0);
		kslist.put(3, 0);
		kslist.put(4, 0);
		kslist.put(5, 0);
	}

	public void setGameID(int gameid)
	{
		this.gameno = gameid;
	}

	public int addKill(Player p)
	{
		killed.add(p.getName());
		kills++;
		checkKS();
		lastkill = new Date().getTime();
		return kslevel;
	}

	public void win(long time)
	{
		position = 1;
		this.time = time;
	}

	public void died(int pos, long time)
	{
		this.time = time;
		death = 1;
		position = pos;
		pppoints = plugin.getGameManager().getGame(arenaid).getInactivePlayers();
	}

	public void setTime(long time)
	{
		this.time = time;
	}

	public void addkillStreak(int ks)
	{
		ksbon = ksbon
				+ (plugin.getSettingsManager().getConfig().getInt("stats.points.killstreak.base") * (plugin.getSettingsManager()
						.getConfig().getInt("stats.points.killstreak.multiplier") + ks));
		int level = ks;
		if (level > 5)
			level = 5;
		kslist.put(level, kslist.get(level) + 1);
		lastkill = new Date().getTime();
	}

	public void calcPoints()
	{
		FileConfiguration c = plugin.getSettingsManager().getConfig();
		int kpoints = kills * c.getInt("stats.points.kill");
		int ppoints = pppoints * c.getInt("stats.points.position");
		int kspoints = ksbon;

		points = kpoints + ppoints + kspoints + ksbon;
		// System.out.println(player+"  "+kpoints +" "+ppoints+" "+kspoints);

		if (position == 1)
		{
			points = points + c.getInt("stats.points.win");
		}

	}

	public boolean checkKS()
	{
		if (15000 > new Date().getTime() - lastkill)
		{
			kslevel++;
			addkillStreak(kslevel);

			return true;
		}

		kslevel = 0;
		return false;
	}

	public String createQuery()
	{
		calcPoints();
		String query = "INSERT INTO " + plugin.getSettingsManager().getSqlPrefix() + "playerstats VALUES(NULL,";
		query = query + gameno + "," + arenaid + ",'" + player.getName() + "'," + points + "," + position + "," + kills + "," + death
				+ ",";
		String killeds = "'";

		for (String killedPlayer : killed)
		{
			killeds = killeds + ((killeds.length() > 2) ? ":" : "") + killedPlayer;
		}

		query = query + killeds + "'," + time;
		query = query + "," + kslist.get(1) + "," + kslist.get(2) + "," + kslist.get(3) + "," + kslist.get(4) + "," + kslist.get(5) + ")";

		return query;
	}
}