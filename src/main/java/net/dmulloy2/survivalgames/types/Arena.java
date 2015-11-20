package net.dmulloy2.survivalgames.types;

import org.bukkit.Location;

public class Arena {
    private Location min;
    private Location max;

    public Arena(Location min, Location max) {
        this.max = max;
        this.min = min;
    }

    public boolean containsBlock(Location v) {
        if (v.getWorld() != min.getWorld())
            return false;
        double x = v.getX();
        double y = v.getY();
        double z = v.getZ();
        return x >= min.getBlockX() && x < max.getBlockX() + 1 && y >= min.getBlockY() && y < max.getBlockY() + 1 && z >= min.getBlockZ() && z < max.getBlockZ() + 1;
    }

    public Location getMax() {
        return max;
    }

    public Location getMin() {
        return min;
    }
}
