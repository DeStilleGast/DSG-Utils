package xyz.destillegast.dsgutils.api;

import org.bukkit.entity.Player;
import xyz.destillegast.dsgutils.bossbartimer.AlreadyCompletedException;

public interface BossBarTimer {
    void cleanUp() throws AlreadyCompletedException;

    void setMessage(String message) throws AlreadyCompletedException;

    void setFinishAction(Runnable newAction) throws AlreadyCompletedException;

    void addPlayer(Player player) throws AlreadyCompletedException;

    void removePlayer(Player player) throws AlreadyCompletedException;
}
