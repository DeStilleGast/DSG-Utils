package xyz.destillegast.dsgutils.bossbartimer;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import xyz.destillegast.dsgutils.DSGUtils;
import xyz.destillegast.dsgutils.api.BossBarTimer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BossBarTimerImpl implements Runnable, BossBarTimer {


    private final BossBar bossBar;
    private final long startTime = System.currentTimeMillis();
    private final long duration;

    private String message;
    private Runnable finishAction;

    private boolean isFinished = false;
    private final BukkitTask bukkitTask;


    BossBarTimerImpl(@Nonnull DSGUtils mainPlugin, @Nonnull List<Player> players, @Nonnull String message, long duration, @Nonnull TimeUnit timeUnit, @Nonnull BarColor bossBarColor, @Nonnull BarStyle bossBarStyle, @Nullable Runnable finishAction){
        this.message = message;
        this.finishAction = finishAction;
        this.duration = timeUnit.toMillis(duration);

        this.bossBar = Bukkit.createBossBar(
                message.replace("%time%", formatTime(this.duration)),
                bossBarColor,
                bossBarStyle,
                BarFlag.PLAY_BOSS_MUSIC
        );

        this.bossBar.setProgress(1);
        players.forEach(this.bossBar::addPlayer);

        bukkitTask = Bukkit.getScheduler().runTaskTimer(mainPlugin, this, 10L, 1L);
    }

    private String formatTime(long totaMilliSecs){
        long totalSecs = totaMilliSecs / 1000;
        long minutes = totalSecs % 3600 / 60;
        long seconds = totalSecs % 60;

        return String.format("%02d:%02d", minutes, seconds);
    }

    @Override
    public void run() {
        if(isFinished) {
            Bukkit.getScheduler().cancelTask(bukkitTask.getTaskId());
            return;
        }

        try{
            double perc = (startTime + duration - System.currentTimeMillis() * 1.0) / duration;

            if(perc >= 0){
                bossBar.setTitle(message.replace("%time%", formatTime(startTime + duration - System.currentTimeMillis())));
                bossBar.setProgress(perc);
            }else{
                cleanUp();

                if(finishAction != null) finishAction.run();

            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void cleanUp() throws AlreadyCompletedException {
        if(isFinished) throw new AlreadyCompletedException();

        bossBar.removeAll();
        isFinished = true;

        Bukkit.getScheduler().cancelTask(bukkitTask.getTaskId());
    }

    @Override
    public void setMessage(String message) throws AlreadyCompletedException {
        if(isFinished) throw new AlreadyCompletedException();
        this.message = message;
    }

    @Override
    public void setFinishAction(Runnable newAction) throws AlreadyCompletedException {
        if(isFinished) throw new AlreadyCompletedException();
        this.finishAction = newAction;
    }

    @Override
    public void addPlayer(Player player) throws AlreadyCompletedException {
        if(isFinished) throw new AlreadyCompletedException();
        this.bossBar.addPlayer(player);
    }

    @Override
    public void removePlayer(Player player) throws AlreadyCompletedException {
        if(isFinished) throw new AlreadyCompletedException();
        this.bossBar.removePlayer(player);
    }
}
