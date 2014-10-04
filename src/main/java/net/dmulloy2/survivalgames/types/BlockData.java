package net.dmulloy2.survivalgames.types;

import java.io.Serializable;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class BlockData implements Serializable {
    private static final long serialVersionUID = 1L;
    private String world;
    private Material prevmat, newmat;
    private MaterialData prevdata, newdata;
    private int x, y, z;
    private int gameid;
    private ItemStack[] items;

    /**
     * Provides a new object for handling the data for block changes
     */
    public BlockData(int gameid, String world, Material prevmat, MaterialData prevdata, Material newmat, MaterialData newdata, int x, int y, int z, ItemStack[] items) {
        this.gameid = gameid;
        this.world = world;
        this.prevmat = prevmat;
        this.prevdata = prevdata;
        this.newmat = newmat;
        this.newdata = newdata;
        this.x = x;
        this.y = y;
        this.z = z;
        this.items = items;
    }

    public int getGameId() {
        return gameid;
    }

    public String getWorld() {
        return world;
    }

    public MaterialData getPrevdata() {
        return prevdata;
    }

    public MaterialData getNewdata() {
        return newdata;
    }

    public Material getPrevmat() {
        return prevmat;
    }

    public Material getNewmat() {
        return newmat;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public ItemStack[] getItems() {
        return items;
    }
}
