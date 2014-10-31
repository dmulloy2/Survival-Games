package net.dmulloy2.survivalgames.commands;

import net.dmulloy2.survivalgames.SurvivalGames;

/**
 * @author dmulloy2
 */

public class CmdLeaveQueue extends SurvivalGamesCommand {
    public CmdLeaveQueue(SurvivalGames plugin) {
        super(plugin);
        this.name = "leavequeue";
        this.aliases.add("lq");
        this.description = "Leave the queue for any queued games";
    }

    @Override
    public void perform() {
        gameHandler.removeFromOtherQueues(player, -1);
    }
}
