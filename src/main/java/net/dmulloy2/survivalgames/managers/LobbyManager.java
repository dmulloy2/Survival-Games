package net.dmulloy2.survivalgames.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import lombok.Getter;
import net.dmulloy2.survivalgames.SurvivalGames;
import net.dmulloy2.survivalgames.types.LobbyWall;
import net.dmulloy2.survivalgames.types.Prefix;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;

public class LobbyManager
{
	private HashMap<Integer, List<LobbyWall>> signs = new HashMap<Integer, List<LobbyWall>>();

	public @Getter HashSet<Chunk> lobbychunks = new HashSet<Chunk>();

	private FileConfiguration s;

	private final SurvivalGames plugin;

	public LobbyManager(SurvivalGames plugin)
	{
		this.plugin = plugin;

		this.s = plugin.getSettingsManager().getSystemConfig();

		for (int a = 1; a <= s.getInt("sg-system.lobby.signno"); a++)
		{
			loadSign(a);
		}
	}

	public void loadSign(int a)
	{
		try
		{
			World w = plugin.getServer().getWorld(s.getString("sg-system.lobby.signs." + a + ".world"));
			int x1 = s.getInt("sg-system.lobby.signs." + a + ".x1");
			int y1 = s.getInt("sg-system.lobby.signs." + a + ".y1");
			int z1 = s.getInt("sg-system.lobby.signs." + a + ".z1");
			int x2 = s.getInt("sg-system.lobby.signs." + a + ".x2");
			int z2 = s.getInt("sg-system.lobby.signs." + a + ".z2");
			int gameid = s.getInt("sg-system.lobby.signs." + a + ".id");

			LobbyWall ls = new LobbyWall(plugin, gameid);
			if (ls.loadSign(w, x1, x2, z1, z2, y1))
			{
				List<LobbyWall> t = signs.get(gameid);
				if (t == null)
				{
					t = new ArrayList<LobbyWall>();
					signs.put(gameid, t);
				}
				
				t.add(ls);
				ls.update();
			}
		}
		catch (Exception e)
		{
			s.set("sg-system.lobby.signs." + a, null);
			s.set("sg-system.lobby.signno", s.getInt("sg-system.lobby.signno") - 1);
		}
	}

	public void updateAll()
	{
		for (List<LobbyWall> lws : signs.values())
		{
			for (LobbyWall lw : lws)
			{
				lw.update();
			}
		}
	}

	public void updateWall(int a)
	{
		if (signs.get(a) != null)
		{
			for (LobbyWall lw : signs.get(a))
			{
				lw.update();
			}
		}
	}

	public void removeSignsForArena(int a)
	{
		clearAllSigns();
		signs.remove(a);
		updateAll();
	}

	public void clearSigns(int a)
	{
		if (signs.get(a) != null)
		{
			for (LobbyWall ls : signs.get(a))
			{
				ls.clear();
			}
		}
	}

	public void clearAllSigns()
	{
		for (List<LobbyWall> lws : signs.values())
		{
			for (LobbyWall lw : lws)
			{
				lw.clear();
			}
		}
	}

	public void display(String s, int a)
	{
		if (signs.get(a) != null)
		{
			for (LobbyWall ls : signs.get(a))
			{
				ls.addMsg(s);
			}
		}
	}

	public void display(String[] s, int a)
	{
		for (String s1 : s)
		{
			display(s1, a);
		}
	}

	public void setLobbySignsFromSelection(Player pl, int a)
	{
		WorldEditPlugin we = plugin.getWorldEdit();
		if (we == null)
		{
			plugin.getMessageHandler().sendMessage(Prefix.WARNING, "You must have WorldEdit installed to do this!", pl);
			return;
		}
		
		Selection sel = we.getSelection(pl);
		if (sel == null)
		{
			plugin.getMessageHandler().sendMessage(Prefix.WARNING, "You must make a WorldEdit Selection first", pl);
			return;
		}
		
		FileConfiguration c = plugin.getSettingsManager().getSystemConfig();
		SettingsManager s = plugin.getSettingsManager();
		if (! c.getBoolean("sg-system.lobby.sign.set", false))
		{
			c.set("sg-system.lobby.sign.set", true);
			s.saveSystemConfig();
		}

		if ((sel.getNativeMaximumPoint().getBlockX() - sel.getNativeMinimumPoint().getBlockX()) != 0
				&& (sel.getNativeMinimumPoint().getBlockZ() - sel.getNativeMaximumPoint().getBlockZ() != 0))
		{
			plugin.getMessageHandler().sendMessage(Prefix.WARNING, "Must be in a straight line!", pl);
			return;
		}
		
		Vector max = sel.getNativeMaximumPoint();
		Vector min = sel.getNativeMinimumPoint();
		int i = c.getInt("sg-system.lobby.signno", 0) + 1;
		c.set("sg-system.lobby.signno", i);
		c.set("sg-system.lobby.signs." + i + ".id", a);
		c.set("sg-system.lobby.signs." + i + ".world", pl.getWorld().getName());
		c.set("sg-system.lobby.signs." + i + ".x1", max.getBlockX());
		c.set("sg-system.lobby.signs." + i + ".y1", max.getBlockY());
		c.set("sg-system.lobby.signs." + i + ".z1", max.getBlockZ());
		c.set("sg-system.lobby.signs." + i + ".x2", min.getBlockX());
		c.set("sg-system.lobby.signs." + i + ".y2", min.getBlockY());
		c.set("sg-system.lobby.signs." + i + ".z2", min.getBlockZ());
		
		plugin.getMessageHandler().sendMessage(Prefix.INFO, "Added Lobby Wall", pl);
		
		s.saveSystemConfig();
		loadSign(i);
	}
}