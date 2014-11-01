package net.dmulloy2.survivalgames.types;

import java.util.LinkedHashMap;
import java.util.Map;

import net.dmulloy2.util.MaterialUtil;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class BlockData implements ConfigurationSerializable {
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

    @Override
    @SuppressWarnings("deprecation")
    public Map<String, Object> serialize() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("world", world);
        data.put("prevmat", prevmat.name());
        data.put("newmat", newmat.name());
        data.put("prevdata", prevdata.getData());
        data.put("newdata", newdata.getData());
        data.put("x", x);
        data.put("y", y);
        data.put("z", z);
        data.put("gameid", gameid);
        data.put("items", items);
        return data;
    }

    @SuppressWarnings("deprecation")
    public BlockData(Map<String, Object> data) {
        this.world = (String) data.get("world");
        this.prevmat = MaterialUtil.getMaterial((String) data.get("prevmat"));
        this.newmat = MaterialUtil.getMaterial((String) data.get("newmat"));
        this.prevdata = new MaterialData(prevmat, (byte) data.get("prevdata"));
        this.newdata = new MaterialData(newmat, (byte) data.get("newdata"));
        this.x = (int) data.get("x");
        this.y = (int) data.get("y");
        this.z = (int) data.get("z");
        this.gameid = (int) data.get("gameid");
        this.items = (ItemStack[]) data.get("items");
    }
}
