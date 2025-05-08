package io.github.defective4.dsp.vcd4j.player;

import java.util.List;

import io.github.defective4.dsp.vcd4j.data.ChangeEntry;

/**
 * Interface used by {@link VCDPlayer}.<br>
 * It's used for listening to player events in real time
 */
public interface PlayerListener {

    /**
     * Called when the playback was stopped due to end of VCD
     *
     * @param playerTime    number of ticks since the player was started
     * @param realTimeNanos real nanosecond time passed since the player was started
     */
    void playerStopped(long playerTime, long realTimeNanos);

    /**
     * Called every player tick
     *
     * @param playerTime    number of ticks since the player was started
     * @param realTimeNanos real nanosecond time passed since the player was started
     */
    void playerTicked(long playerTime, long realTimeNanos);

    /**
     * Called when a value was changed
     *
     * @param entries       list containing all changes that happened at given time
     * @param playerTime    number of ticks since the player was started
     * @param realTimeNanos real nanosecond time passed since the player was started
     */
    void valuesChanged(List<ChangeEntry<?>> entries, long playerTime, long realTimeNanos);
}
