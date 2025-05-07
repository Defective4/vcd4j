package io.github.defective4.dsp.vcd4j.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import io.github.defective4.dsp.vcd4j.data.ChangeEntry;
import io.github.defective4.dsp.vcd4j.data.TimeScale;
import io.github.defective4.dsp.vcd4j.data.TimeScale.TimeUnit;
import io.github.defective4.dsp.vcd4j.data.VCD;
import io.github.defective4.dsp.vcd4j.data.VariableDefinition;

public class VCDPlayer {
    private int index = 0;
    private final List<PlayerListener> listeners = new CopyOnWriteArrayList<>();
    private ScheduledExecutorService playerService;
    private long playerTime = 0;
    private final TimeScale timeScale;

    private final List<Map.Entry<Long, List<ChangeEntry<?>>>> valueChanges = new ArrayList<>();

    private final Map<String, VariableDefinition> variableDefinitions;

    public VCDPlayer(TimeScale timeScale, Map<Long, List<ChangeEntry<?>>> valueChanges,
            Map<String, VariableDefinition> variableDefinitions) {
        Objects.requireNonNull(timeScale);
        Objects.requireNonNull(valueChanges);
        Objects.requireNonNull(variableDefinitions);
        if (valueChanges.isEmpty()) throw new IllegalArgumentException("Value changes can't be empty");
        if (variableDefinitions.isEmpty()) throw new IllegalArgumentException("Variable definitions can't be empty");
        if (timeScale.getUnit() == TimeUnit.PICOSECOND) {
            throw new IllegalArgumentException(
                    "Picosecond time scale is not supported. Try using VCD#setTimeScaleUnit or VCD#multiply before passing it as an argument."); // TODO
                                                                                                                                                 // implement
                                                                                                                                                 // the
                                                                                                                                                 // methods
        }
        this.timeScale = timeScale;
        this.variableDefinitions = variableDefinitions;
        for (Map.Entry<Long, List<ChangeEntry<?>>> entry : valueChanges.entrySet())
            this.valueChanges.add(Map.entry(entry.getKey() * timeScale.getValue(), entry.getValue()));

        this.valueChanges.sort((e1, e2) -> (int) (e1.getKey() - e2.getKey()));
    }

    public VCDPlayer(VCD vcd) {
        this(vcd.getTimeScale(), vcd.getValueChanges(), vcd.getVariableDefinitions());
    }

    public boolean addListener(PlayerListener listener) {
        Objects.requireNonNull(listener);
        if (!listeners.contains(listener)) return listeners.add(listener);
        return false;
    }

    public List<PlayerListener> getListeners() {
        return Collections.unmodifiableList(listeners);
    }

    public boolean removeListener(PlayerListener listener) {
        Objects.requireNonNull(listener);
        return listeners.remove(listener);
    }

    public void start() {
        long start = System.currentTimeMillis();
        playerService = Executors.newScheduledThreadPool(10);
        playerService.scheduleAtFixedRate(() -> {
            if (index > valueChanges.size()) {
                stop();
                listeners.forEach(PlayerListener::playerStopped);
                return;
            }
            long prevTime = index > 0 ? valueChanges.get(index - 1).getKey() : 0;
            playerTime += 1;
            if (index == valueChanges.size()) {
                if (playerTime >= prevTime) index++;
                return;
            }
            List<ChangeEntry<?>> entries = valueChanges.get(index).getValue();
            if (playerTime >= prevTime) {
                index++;
                listeners.forEach(ls -> ls.valuesChanged(entries));
            }
            listeners.forEach(ls -> ls.playerTicked(playerTime));
        }, 1, 1, timeScale.getUnit().getTimeUnit());
    }

    public void stop() {
        playerService.shutdown();
        index = 0;
        playerTime = 0;
    }
}
