package io.github.defective4.dsp.vcd4j.player;

import java.util.List;

import io.github.defective4.dsp.vcd4j.data.ChangeEntry;

public abstract class PlayerAdapter implements PlayerListener {

    @Override
    public void playerStopped() {}

    @Override
    public void playerTicked(long currentTicks) {}

    @Override
    public void valuesChanged(List<ChangeEntry<?>> entries) {}

}
