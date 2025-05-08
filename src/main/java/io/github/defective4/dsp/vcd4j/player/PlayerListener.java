package io.github.defective4.dsp.vcd4j.player;

import java.util.List;

import io.github.defective4.dsp.vcd4j.data.ChangeEntry;

public interface PlayerListener {
    void playerStopped(long playerTime, long realTimeNanos);

    void playerTicked(long playerTime, long realTimeNanos);

    void valuesChanged(List<ChangeEntry<?>> entries, long playerTime, long realTimeNanos);
}
