package org.mcsg.survivalgames.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.mcsg.survivalgames.Game;
import org.mcsg.survivalgames.GameManager;
import org.mcsg.survivalgames.MessageManager;
import org.mcsg.survivalgames.MessageManager.PrefixType;
import org.mcsg.survivalgames.SettingsManager;

public class ListArenas implements SubCommand {
	
	@Override
    public boolean onCommand(Player player, String[] args) {
		MessageManager msgmgr = MessageManager.getInstance();
		List<Game> games = GameManager.getInstance().getGames();
		if (games.isEmpty()) {
			msgmgr.sendFMessage(PrefixType.WARNING, "error.noarenasexist", player);
		} else {
			List<String> lines = new ArrayList<String>();
			StringBuilder line = new StringBuilder();
			line.append(ChatColor.BLUE + "-----[ " + ChatColor.GOLD + "Available Arenas" + ChatColor.BLUE + " ]-----");
			lines.add(line.toString());
			
			for (Game game : games) {
				line.append(ChatColor.GREEN + game.getName() + ", ");
			}
			
			line.delete(line.lastIndexOf(","), line.lastIndexOf(" "));
			line.append(".");
			lines.add(line.toString());
			
			for (String s : lines) {
				player.sendMessage(s);
			}
		}
		
		return true;
	}
    
    @Override
    public String help(Player p) {
        return "/sg listarenas - " + SettingsManager.getInstance().getMessageConfig().getString("messages.help.listarenas", "List all available arenas");
    }

	@Override
	public String permission() {
		return "sg.player.listarenas";
	}
}