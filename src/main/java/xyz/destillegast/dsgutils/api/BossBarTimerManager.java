package xyz.destillegast.dsgutils.api;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.TimeUnit;

public interface BossBarTimerManager {
    BossBarTimer createBossBarTimer(@Nonnull Player player, @Nonnull String message, long duration, @Nonnull TimeUnit timeUnit, @Nullable Runnable finishAction);

    BossBarTimer createBossBarTimer(@Nonnull Player player, @Nonnull String message, long duration, @Nonnull TimeUnit timeUnit, @Nonnull BarColor bossBarColor, @Nonnull BarStyle bossBarStyle, @Nullable Runnable finishAction);

    BarColor randomBarColor();

    BarStyle randomBarStyle();
}
