package net.dmulloy2.survivalgames.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;

import net.dmulloy2.survivalgames.SurvivalGames;

import org.bukkit.ChatColor;

public class MessageUtil
{	
	private static HashMap<String, String> varcache = new HashMap<String, String>();

	private static SurvivalGames plugin;
	public MessageUtil(SurvivalGames plugin)
	{
		MessageUtil.plugin = plugin;
	}
	
	public static String replaceColors(String s)
	{
		return ChatColor.translateAlternateColorCodes('&', s);
	}

	public static String replaceVars(String msg, HashMap<String, String> vars)
	{
		boolean error = false;
		for (String s : vars.keySet())
		{
			try
			{
				msg.replace("{$" + s + "}", vars.get(s));
			}
			catch (Exception e)
			{
				plugin.$(Level.WARNING, "Failed to replace string vars. Error on " + s);
				error = true;
			}
		}
		if (error)
		{
			plugin.$(Level.SEVERE, "Error replacing vars in message: " + msg);
			plugin.$(Level.SEVERE, "Vars: " + vars.toString());
			plugin.$(Level.SEVERE, "Vars Cache: " + varcache.toString());
		}
		return msg;
	}

	public static String replaceVars(String msg, String[] vars)
	{
		for (String str : vars)
		{
			String[] s = str.split("-");
			varcache.put(s[0], s[1]);
		}
		boolean error = false;
		for (String str : varcache.keySet())
		{
			try
			{
				msg = msg.replace("{$" + str + "}", varcache.get(str));
			}
			catch (Exception e)
			{
				plugin.$(Level.WARNING, "Failed to replace string vars. Error on " + str);
				error = true;
			}
		}
		if (error)
		{
			plugin.$(Level.SEVERE, "Error replacing vars in message: " + msg);
			plugin.$(Level.SEVERE, "Vars: " + Arrays.toString(vars));
			plugin.$(Level.SEVERE, "Vars Cache: " + varcache.toString());
		}

		return msg;
	}
}