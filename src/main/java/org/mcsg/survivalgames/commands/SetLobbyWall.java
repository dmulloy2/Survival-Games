package org.mcsg.survivalgames.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.mcsg.survivalgames.SettingsManager;

public class SetLobbyWall implements SubCommand {

    @Override
    public boolean onCommand(Player player, String[] args) {
    	player.sendMessage(ChatColor.RED + "This command has been replaced by /sg addwall <arenaid>");
    	return true;
    }

    @Override
    public String help(Player p) {
        return "/sg addwall <id> - " + SettingsManager.getInstance().getMessageConfig().getString("messages.help.addwall", "Add a lobby stats wall for Arena <id>");
    }

	@Override
	public String permission() {
		return "sg.admin.addwall";
	}

    //TODO: TAKE A W.E SELECTION AND SET THE LOBBY. ALSO SET LOBBY WALL
}