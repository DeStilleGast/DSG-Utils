package xyz.destillegast.dsgutils.cooldown;

import java.util.concurrent.TimeUnit;

/**
 * Created by DeStilleGast 23-5-2021
 */
public final class CooldownHelper { 
    private long previousTime = System.nanoTime();

    /**
     * Check if time has passed a certain amout on time in milliseconds
     * @param time
     * @return if x milliseconds has passed
     */
    public boolean hasReached(long time){
        return hasReached(time, TimeUnit.MILLISECONDS);
    }

    /**
     * Check if time has passed a certain amount of time
     * @param time
     * @param timeUnit
     * @return if it has passed the given time
     */
    public boolean hasReached(long time, TimeUnit timeUnit){
        return timeUnit.convert(System.nanoTime() - this.previousTime, TimeUnit.NANOSECONDS) >= time;
    }

    public long getPreviousTime(){
        return this.previousTime;
    }

    /**
     * Get how long the cooldown currently is in milliseconds
     * @return current counter in given milliseconds
     */
    public long getTime() {
        return getTime(TimeUnit.MILLISECONDS);
    }

    /**
     * Get how long the cooldown currently is
     * @param timeUnit
     * @return current counter in given TimeUnit
     */
    public long getTime(final TimeUnit timeUnit) {
        return timeUnit.convert(System.nanoTime() - this.previousTime, TimeUnit.NANOSECONDS);
    }

    public void reset() {
        this.previousTime = System.nanoTime();
    }
}
