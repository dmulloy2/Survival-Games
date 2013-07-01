package org.mcsg.survivalgames.commands;

import org.bukkit.entity.Player;
import org.mcsg.survivalgames.GameManager;
import org.mcsg.survivalgames.MessageManager;
import org.mcsg.survivalgames.SettingsManager;

public class Leave implements SubCommand {
	
	@Override
    public boolean onCommand(Player player, String[] args) {
        if (GameManager.getInstance().getPlayerGameId(player) == -1) {
//          MessageManager.getInstance().sendFMessage(MessageManager.PrefixType.ERROR, "error.nopermission", player);
        	MessageManager.getInstance().sendFMessage(MessageManager.PrefixType.ERROR, "error.notingame", player);
        } else {
            GameManager.getInstance().removePlayer(player, true);
        }
        return true;
    }

    @Override
    public String help(Player p) {
        return "/sg leave - " + SettingsManager.getInstance().getMessageConfig().getString("messages.help.leave", "Leaves the game");
    }

	@Override
	public String permission() {
		return null;
	}
}