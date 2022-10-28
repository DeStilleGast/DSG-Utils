package xyz.destillegast.dsgutils.bossbartimer;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;
import xyz.destillegast.dsgutils.DSGUtils;
import xyz.destillegast.dsgutils.api.BossBarTimer;
import xyz.destillegast.dsgutils.api.BossBarTimerManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class BossBarTimerManagerImpl implements BossBarTimerManager {

    private final DSGUtils pluginHolder;
    private final Random random = new Random();

    public BossBarTimerManagerImpl(DSGUtils pluginHolder) {
        this.pluginHolder = pluginHolder;

        Bukkit.getServicesManager().register(BossBarTimerManager.class, this, pluginHolder, ServicePriority.Normal);
    }

    /**
     * Create a boss bar timer
     * Use %time% to display the time what is left (its human-readable)
     *
     * @param player Targeted player
     * @param message Message to display
     * @param duration duration of the bassbar timer
     * @param timeUnit In what time unit the duration is
     * @param finishAction when should be executed when the timer runs out
     * @return BossBarTimer object
     */
    @Override
    public BossBarTimer createBossBarTimer(@Nonnull Player player, @Nonnull String message, long duration, @Nonnull TimeUnit timeUnit, @Nullable Runnable finishAction){
        return this.createBossBarTimer(player, message, duration, timeUnit, randomBarColor() , randomBarStyle(), finishAction);
    }

    /**
     * Create a boss bar timer
     * Use %time% to display the time what is left (its human-readable)
     *
     * @param player Targeted player
     * @param message Message to display
     * @param duration duration of the bassbar timer
     * @param timeUnit In what time unit the duration is
     * @param bossBarColor Color of the boss bar
     * @param bossBarStyle Style of the boss abr
     * @param finishAction when should be executed when the timer runs out
     * @return BossBarTimer object
     */
    @Override
    public BossBarTimer createBossBarTimer(@Nonnull Player player, @Nonnull String message, long duration, @Nonnull TimeUnit timeUnit, @Nonnull BarColor bossBarColor, @Nonnull BarStyle bossBarStyle, @Nullable Runnable finishAction){
        return new BossBarTimerImpl(pluginHolder, player, message, duration, timeUnit, bossBarColor, bossBarStyle, finishAction);
    }

    @Override
    public BarColor randomBarColor(){
        return BarColor.values()[random.nextInt(BarColor.values().length)];
    }

    @Override
    public BarStyle randomBarStyle(){
        return BarStyle.values()[random.nextInt(BarStyle.values().length)];
    }

}
