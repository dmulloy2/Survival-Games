package net.dmulloy2.survivalgames.managers;

import net.dmulloy2.survivalgames.SurvivalGames;
import net.dmulloy2.survivalgames.types.Game;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

// TODO: I feel like this should be used for *something*
public class ThirstManager implements Listener
{
	private final SurvivalGames plugin;
	public ThirstManager(SurvivalGames plugin)
	{
		this.plugin = plugin;
	}
	
	public void startThirst()
	{
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				for (Game g : plugin.getGameManager().getGames())
				{
					for (Player p : g.getAllPlayers())
					{
						removeThirst(p, 1);
					}
				}
			}

		}.runTaskTimer(plugin, 60L, 200L);
	}

	public void removeThirst(Player p, int amount)
	{
		p.setLevel(p.getLevel() - amount);
	}

	public void addThirst(Player p, int amount)
	{
		p.setLevel(p.getLevel() + amount);
	}

	public void startThirst(Player p)
	{
		p.setLevel(30);
	}

	@EventHandler
	public void onPlayerDrinkWater(PlayerInteractEvent e)
	{
		if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)
		{
			if (e.getPlayer().getItemInHand() == new ItemStack(Material.POTION))
			{
				e.getPlayer().getInventory().removeItem(new ItemStack(Material.POTION, 1));
				addThirst(e.getPlayer(), 5);
				e.getPlayer().sendMessage(ChatColor.GREEN + "You drank water.");
			}
		}
	}
}