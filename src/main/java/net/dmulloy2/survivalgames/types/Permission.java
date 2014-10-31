package net.dmulloy2.survivalgames.types;

import lombok.Getter;
import net.dmulloy2.types.IPermission;

/**
 * @author dmulloy2
 */

@Getter
public enum Permission implements IPermission {
    ADMIN_ADDWALL,
    ADMIN_CREATEARENA,
    ADMIN_FORCESTART,
    ADMIN_FLAG,
    ADMIN_DELARENA,
    ADMIN_RELOAD,
    ADMIN_RESETSPAWNS,
    ADMIN_SETLOBBYSPAWN,
    ADMIN_SETSPAWN,

    STAFF_ENABLE,
    STAFF_DISABLE,
    STAFF_TELEPORT,

    PLAYER_LIST,
    PLAYER_LISTARENAS,
    PLAYER_JOIN,
    PLAYER_JOIN_LOBBY,
    PLAYER_SPECTATE,
    PLAYER_VERSION,
    PLAYER_VOTE;

    private final String node;

    private Permission() {
        this.node = toString().toLowerCase().replaceAll("_", ".");
    }
}
