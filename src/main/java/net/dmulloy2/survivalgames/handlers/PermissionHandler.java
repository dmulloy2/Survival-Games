package net.dmulloy2.survivalgames.handlers;

import net.dmulloy2.survivalgames.types.Permission;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class PermissionHandler {
    public boolean hasPermission(CommandSender sender, Permission permission) {
        return permission == null || hasPermission(sender, getPermissionString(permission));
    }

    public boolean hasPermission(CommandSender sender, String permission) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            return player.hasPermission(permission) || player.isOp();
        }

        return true;
    }

    private String getPermissionString(Permission permission) {
        return "sg." + permission.getNode().toLowerCase();
    }
}
