package net.dmulloy2.survivalgames.events;

import net.dmulloy2.survivalgames.SurvivalGames;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class SignClickEvent implements Listener
{
	private final SurvivalGames plugin;
	public SignClickEvent(SurvivalGames plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void clickHandler(PlayerInteractEvent e)
	{

		if (!(e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_BLOCK))
		{
			return;
		}

		Block clickedBlock = e.getClickedBlock();
		if (!(clickedBlock.getType() == Material.SIGN || clickedBlock.getType() == Material.SIGN_POST || clickedBlock.getType() == Material.WALL_SIGN))
		{
			return;
		}

		Sign thisSign = (Sign) clickedBlock.getState();
		String[] lines = thisSign.getLines();
		if (lines.length < 3)
		{
			return;
		}
		if (lines[0].equalsIgnoreCase("[SurvivalGames]"))
		{
			e.setCancelled(true);
			try
			{
				if (lines[2].equalsIgnoreCase("Auto Assign"))
				{
					plugin.getGameManager().autoAddPlayer(e.getPlayer());
				}
				else
				{
					String game = lines[2].replace("Arena ", "");
					int gameno = Integer.parseInt(game);
					plugin.getGameManager().addPlayer(e.getPlayer(), gameno);
				}
			}
			catch (Exception ek)
			{
				//
			}
		}
	}
}