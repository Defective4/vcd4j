package io.github.defective4.dsp.vcd4j.player;

import java.util.List;

import io.github.defective4.dsp.vcd4j.data.ChangeEntry;

/**
 * An adapter class for {@link PlayerListener}
 */
public abstract class PlayerAdapter implements PlayerListener {

    @Override
    public void playerStopped(long playerTime, long realTimeNanos) {}

    @Override
    public void playerTicked(long playerTime, long realTimeNanos) {}

    @Override
    public void valuesChanged(List<ChangeEntry<?>> entries, long playerTime, long realTimeNanos) {}

}
