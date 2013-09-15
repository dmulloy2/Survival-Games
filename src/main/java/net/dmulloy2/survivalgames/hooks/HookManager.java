package net.dmulloy2.survivalgames.hooks;

import java.util.HashMap;

import net.dmulloy2.survivalgames.SurvivalGames;
import net.dmulloy2.survivalgames.util.MessageUtil;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

public class HookManager
{
	private final SurvivalGames plugin;

	public HookManager(SurvivalGames plugin)
	{
		this.plugin = plugin;
	}

	private HashMap<String, HookBase> hooks = new HashMap<String, HookBase>();

	public void setup()
	{
		hooks.put("c", new CommandHook());
	}

	public void runHook(String hook, String... args)
	{
		// System.out.println("RUNNING HOOK");
		FileConfiguration c = plugin.getSettingsManager().getConfig();
		// System.out.println(c.getStringList("hooks."+hook));

		for (String str : c.getStringList("hooks." + hook))
		{
			// System.out.println(str);
			String[] split = str.split("!");
			String p = MessageUtil.replaceVars(split[0], args);
			String[] commands = MessageUtil.replaceVars(split[1], args).split(";");
			if (checkConditions(split[2], args))
			{
				if (p.equalsIgnoreCase("console") || (split.length == 4 && Bukkit.getPlayer(p).hasPermission(split[3]))
						|| (split.length == 3))
				{
					for (String s1 : commands)
					{
						// System.out.println(s1);
						String[] s2 = s1.split("#");
						// System.out.println("Executing "+s2[0]+" "+s2[1]);
						hooks.get(s2[0]).executehook(p, s2);
					}
				}
			}
		}
	}

	public boolean checkConditions(String str, String... args)
	{
		String[] C = { "<", ">", "=", ">=", "<=" };
		str = str.trim();
		if (str.equalsIgnoreCase("true"))
		{
			return true;
		}

		for (String split : MessageUtil.replaceVars(str, args).split(";"))
		{
			boolean flag = false;
			for (String c : C)
			{
				int i = split.indexOf(c);
				if (i != -1)
				{
					flag = true;
					break;
				}
			}
			if (!flag)
			{
				plugin.$("HookManager Condition does not contian a compare operator: " + split);
				return false;
			}
			try
			{
				// attempt to compare the "values", probably a better way to do
				// this
				if (split.contains(">"))
				{
					if (Double.parseDouble(split.substring(0, split.indexOf(">")).trim()) > Double.parseDouble(split.substring(
							split.indexOf(">")).trim()))
					{
						//
					}
					else
					{
						return false;
					}
				}
				else if (split.contains("<"))
				{
					if (Double.parseDouble(split.substring(0, split.indexOf("<")).trim()) < Double.parseDouble(split.substring(
							split.indexOf("<")).trim()))
					{
						//
					}
					else
					{
						return false;
					}
				}
				else if (split.contains("="))
				{
					if (Double.parseDouble(split.substring(0, split.indexOf("=")).trim()) == Double.parseDouble(split.substring(
							split.indexOf("=")).trim()))
					{
						//
					}
					else
					{
						return false;
					}
				}
				else if (split.contains(">="))
				{
					if (Double.parseDouble(split.substring(0, split.indexOf(">=")).trim()) >= Double.parseDouble(split.substring(
							split.indexOf(">=")).trim()))
					{
						//
					}
					else
					{
						return false;
					}
				}
				else if (split.contains("<="))
				{
					if (Double.parseDouble(split.substring(0, split.indexOf("<=")).trim()) <= Double.parseDouble(split.substring(
							split.indexOf("<=")).trim()))
					{
					}
					else
					{
						return false;
					}
				}
			}
			catch (Exception e)
			{
				plugin.$("HookManager: Error parsing value for: " + split);
				return false;
			}
		}

		return true;
	}
}