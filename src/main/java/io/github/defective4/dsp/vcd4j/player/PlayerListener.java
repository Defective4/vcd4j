package io.github.defective4.dsp.vcd4j.player;

import java.util.List;

import io.github.defective4.dsp.vcd4j.data.ChangeEntry;

public interface PlayerListener {
    void playerStopped();

    void playerTicked(long currentTicks);

    void valuesChanged(List<ChangeEntry<?>> entries);
}
