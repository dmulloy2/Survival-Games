package net.dmulloy2.survivalgames.types;

import java.util.ArrayList;
import java.util.Collections;

import net.dmulloy2.survivalgames.SurvivalGames;
import net.dmulloy2.survivalgames.util.NameUtil;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class LobbyWall {
    private ArrayList<Sign> signs = new ArrayList<Sign>();
    private ArrayList<String> msgqueue = new ArrayList<String>();
    private int gameid;

    private final SurvivalGames plugin;

    public LobbyWall(SurvivalGames plugin, int gid) {
        this.plugin = plugin;
        this.gameid = gid;
    }

    public boolean loadSign(World w, int x1, int x2, int z1, int z2, int y1) {
        boolean usingx = (x1 == x2) ? false : true;
        plugin.debug(w + " " + x1 + " " + x2 + " " + z1 + " " + z2 + " " + y1 + " " + usingx);

        BlockState state = new Location(w, x1, y1, z1).getBlock().getState();

        if (usingx) {
            for (int a = Math.max(x1, x2); a >= Math.min(x1, x2); a--) {
                Location l = new Location(w, a, y1, z1);
                BlockState b = l.getBlock().getState();
                if (b instanceof Sign) {
                    signs.add((Sign) b);
                    plugin.getLobbyHandler().lobbychunks.add(b.getChunk());
                    plugin.debug("usingx - " + b.getLocation().toString());
                } else {
                    plugin.debug("Not a sign" + b.getType().toString());
                    return false;
                }
            }
        } else {
            for (int a = Math.min(z1, z2); a <= Math.max(z1, z2); a++) {
                plugin.debug(a);
                Location l = new Location(w, x1, y1, a);
                BlockState b = l.getBlock().getState();
                if (b instanceof Sign) {
                    signs.add((Sign) b);
                    plugin.getLobbyHandler().lobbychunks.add(b.getChunk());
                    plugin.debug("notx - " + b.getLocation().toString());
                } else {
                    plugin.debug("Not a sign" + b.getType().toString());
                    return false;
                }
            }
        }

        org.bukkit.material.Sign signDat = (org.bukkit.material.Sign) state.getData();

        BlockFace dir = signDat.getAttachedFace();
        if (dir == BlockFace.NORTH || dir == BlockFace.WEST) {
            Collections.reverse(signs);
        }

        addMsg("SurvivalGames");
        addMsg("dmulloy2");
        addMsg("ShadowvoltMC");
        addMsg("Game id: " + gameid);
        update();
        return true;
    }

    public void update() {
        if (msgqueue.size() > 0) {
            display();
            if (plugin.isDisabling()) {
                display();
                update();
            } else {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        display();
                        update();
                    }
                }.runTaskLater(plugin, 20L);
            }

            return;
        }

        clear();
        Game game = plugin.getGameHandler().getGame(gameid);
        Sign s0 = signs.get(0);
        Sign s1 = signs.get(1);

        // sign 0
        s0.setLine(0, "[SurvivalGames]");
        s0.setLine(1, "Click to join");
        s0.setLine(2, "Arena " + gameid);

        // sign 1
        s1.setLine(0, game.getName());
        s1.setLine(1, game.getMode() + "");
        s1.setLine(2, game.getActivePlayers() + "/" + ChatColor.GRAY + game.getInactivePlayers() + ChatColor.BLACK + "/" + plugin.getSettingsHandler().getSpawnCount(game.getID()));

        // live update line s1
        if (game.getMode() == Game.GameMode.STARTING) {
            s1.setLine(3, game.getCountdownTime() + "");
        } else if (game.getMode() == Game.GameMode.RESETTING || game.getMode() == Game.GameMode.FINISHING) {
            s1.setLine(3, game.getRBStatus());
            if (game.getRBPercent() > 100) {
                s1.setLine(1, "Saving Queue");
                s1.setLine(3, (int) game.getRBPercent() + " left");
            } else {
                s1.setLine(3, (int) game.getRBPercent() + "%");
            }
        } else {
            s1.setLine(3, "");
        }

        // live player data
        ArrayList<String> display = new ArrayList<String>();
        for (Player p : game.getAllPlayers()) {
            display.add((game.isPlayerActive(p) ? ChatColor.BLACK : ChatColor.GRAY) + NameUtil.stylize(p.getName(), !game.isPlayerActive(p)));
        }

        try {
            int no = 2;
            int line = 0;
            for (String s : display) {
                signs.get(no).setLine(line, s);
                line++;
                if (line >= 4) {
                    line = 0;
                    no++;
                }
            }
        } catch (Exception e) {
            //
        }

        for (Sign s : signs) {
            s.update();
        }
    }

    public void clear() {
        for (Sign s : signs) {
            for (int a = 0; a < 4; a++) {
                s.setLine(a, "");
            }
            s.update();
        }
    }

    public void addMsg(String s) {
        msgqueue.add(s);
    }

    int displaytid = 0;

    public void display() {
        int a = 0;
        while (msgqueue.size() > 0 && a < 4) {
            String s = msgqueue.get(0);
            for (int b = 0; b < s.length() / 16; b++) {
                try {
                    signs.get(b).setLine(a, s.substring(b * 16, (b + 1) * 16));

                    signs.get(b).update();
                } catch (Exception e) {
                    //
                }
            }
            a++;
            msgqueue.remove(0);
        }

    }
}
