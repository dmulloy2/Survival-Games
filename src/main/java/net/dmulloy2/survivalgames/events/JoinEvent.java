package net.dmulloy2.survivalgames.events;

import net.dmulloy2.survivalgames.SurvivalGames;
import net.dmulloy2.survivalgames.util.UpdateChecker;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class JoinEvent implements Listener
{
	private SurvivalGames plugin;
	public JoinEvent(SurvivalGames plugin)
	{
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void PlayerJoin(PlayerJoinEvent e)
	{
		final Player p = e.getPlayer();

		if (plugin.getGameManager().getBlockGameId(p.getLocation()) != -1)
		{
			new TeleportTask(p).runTaskLater(plugin, 5L);
		}

		if ((p.isOp() || p.hasPermission("sg.admin.reload"))
				&& plugin.getSettingsManager().getConfig().getBoolean("check-for-update", true))
		{
			new UpdateNotifyTask(p).runTaskLater(plugin, 60L);
		}
	}

	public class TeleportTask extends BukkitRunnable
	{
		private Player player;

		public TeleportTask(Player player)
		{
			this.player = player;
		}

		@Override
		public void run()
		{
			player.teleport(plugin.getSettingsManager().getLobbySpawn());
		}
	}

	public class UpdateNotifyTask extends BukkitRunnable
	{
		private Player player;

		public UpdateNotifyTask(Player player)
		{
			this.player = player;
		}

		@Override
		public void run()
		{
			new UpdateChecker().check(player, plugin);
		}
	}
}
