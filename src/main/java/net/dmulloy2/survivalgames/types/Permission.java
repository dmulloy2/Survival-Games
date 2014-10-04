package net.dmulloy2.survivalgames.types;

/**
 * @author dmulloy2
 */

public enum Permission {
    ADMIN_ADDWALL, ADMIN_CREATEARENA, ADMIN_FORCESTART, ADMIN_FLAG, ADMIN_DELARENA, ADMIN_RELOAD, ADMIN_RESETSPAWNS, ADMIN_SETLOBBYSPAWN, ADMIN_SETSPAWN,

    STAFF_ENABLE, STAFF_DISABLE, STAFF_TELEPORT,

    PLAYER_LIST, PLAYER_LISTARENAS, PLAYER_JOIN, PLAYER_JOIN_LOBBY, PLAYER_SPECTATE, PLAYER_VERSION, PLAYER_VOTE;

    public final String node;

    Permission() {
        this.node = toString().toLowerCase().replaceAll("_", ".");
    }

    public String getNode() {
        return this.node;
    }
}
