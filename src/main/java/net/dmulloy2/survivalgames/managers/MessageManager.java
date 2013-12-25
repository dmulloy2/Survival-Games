package net.dmulloy2.survivalgames.managers;

import java.util.HashMap;
import java.util.logging.Logger;

import net.dmulloy2.survivalgames.SurvivalGames;
import net.dmulloy2.survivalgames.util.MessageUtil;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class MessageManager
{
	public String pre = ChatColor.BLUE + "" + ChatColor.BOLD + "[" + ChatColor.GOLD + "" + ChatColor.BOLD + "SG" + ChatColor.BLUE + ""
			+ ChatColor.BOLD + "] " + ChatColor.RESET;
	private HashMap<PrefixType, String> prefix = new HashMap<PrefixType, String>();

	public enum PrefixType
	{
		MAIN, INFO, WARNING, ERROR;
	}

	private final SurvivalGames plugin;
	public MessageManager(SurvivalGames plugin)
	{
		this.plugin = plugin;

		FileConfiguration f = plugin.getSettingsManager().getMessageConfig();
		prefix.put(PrefixType.MAIN, MessageUtil.replaceColors(f.getString("prefix.main", "&6[&4&lSG&6]")));
		prefix.put(PrefixType.INFO, MessageUtil.replaceColors(f.getString("prefix.states.info", "")));
		prefix.put(PrefixType.WARNING, MessageUtil.replaceColors(f.getString("prefix.states.warning", "&c[Warning] ")));
		prefix.put(PrefixType.ERROR, MessageUtil.replaceColors(f.getString("prefix.states.error", "&4[Error] ")));
	}

	/**
	 * SendMessage
	 * 
	 * Loads a Message from messages.yml, converts its colors and replaces vars
	 * in the form of {$var} with its correct values, then sends to the player,
	 * adding the correct prefix
	 * 
	 * @param type
	 * @param input
	 * @param player
	 * @param vars
	 */
	public void sendFMessage(PrefixType type, String input, Player player, String... args)
	{
		String msg = plugin.getSettingsManager().getMessageConfig().getString("messages." + input);
		boolean enabled = plugin.getSettingsManager().getMessageConfig().getBoolean("messages." + input + "_enabled", true);
		if (msg == null)
		{
			player.sendMessage(ChatColor.RED + "Failed to load message for messages." + input);
		}
		if (!enabled)
		{
			return;
		}
		if (args != null && args.length != 0)
		{
			msg = MessageUtil.replaceVars(msg, args);
		}
		msg = MessageUtil.replaceColors(msg);
		player.sendMessage(prefix.get(PrefixType.MAIN) + " " + prefix.get(type) + msg);
	}

	/**
	 * SendMessage
	 * 
	 * Sends a pre formated message from the plugin to a player, adding correct
	 * prefix first
	 * 
	 * @param type
	 * @param msg
	 * @param p
	 */
	public void sendMessage(PrefixType type, String msg, Player player)
	{
		player.sendMessage(prefix.get(PrefixType.MAIN) + " " + prefix.get(type) + ChatColor.translateAlternateColorCodes('&', msg));
	}

	public void logMessage(PrefixType type, String msg)
	{
		Logger logger = plugin.getLogger();
		switch (type)
		{
			case INFO:
				logger.info(prefix.get(type) + msg);
				break;
			case WARNING:
				logger.warning(prefix.get(type) + msg);
				break;
			case ERROR:
				logger.severe(prefix.get(type) + msg);
				break;
			default:
				break;
		}
	}

	public void broadcastFMessage(PrefixType type, String input, String... args)
	{
		String msg = plugin.getSettingsManager().getMessageConfig().getString("messages." + input);
		boolean enabled = plugin.getSettingsManager().getMessageConfig().getBoolean("messages." + input + "_enabled", true);
		if (msg == null)
		{
			plugin.getServer().broadcastMessage(ChatColor.RED + "Failed to load message for messages." + input);
			return;
		}
		if (!enabled)
		{
			return;
		}
		if (args != null && args.length != 0)
		{
			msg = MessageUtil.replaceVars(msg, args);
		}
		msg = MessageUtil.replaceColors(msg);
		plugin.getServer().broadcastMessage(prefix.get(PrefixType.MAIN) + prefix.get(type) + " " + msg);
	}

	public void broadcastMessage(PrefixType type, String msg, Player player)
	{
		plugin.getServer().broadcastMessage(
				prefix.get(PrefixType.MAIN) + " " + prefix.get(type) + ChatColor.translateAlternateColorCodes('&', msg));
	}
}