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
import io.github.defective4.dsp.vcd4j.data.TimeScale.TimeScaleUnit;
import io.github.defective4.dsp.vcd4j.data.VCD;
import io.github.defective4.dsp.vcd4j.data.VariableDefinition;

/**
 * Provides functionality to play back VCD objects.
 *
 * This class simulates the timing and state changes of signals as described in
 * a VCD file, and allows the addition of listeners to observe playback events.
 */
public class VCDPlayer {
    private int index = 0;
    private final List<PlayerListener> listeners = new CopyOnWriteArrayList<>();
    private ScheduledExecutorService playerService;
    private long playerTime = 0;
    private int speedMultiplier = 1;
    private boolean started;

    private final TimeScale timeScale;

    private final List<Map.Entry<Long, List<ChangeEntry<?>>>> valueChanges = new ArrayList<>();

    private final Map<String, VariableDefinition> variableDefinitions;

    /**
     * Constructs a new VCD player
     *
     * @param  timeScale                time scale used for playing the VCD
     * @param  valueChanges             list of value change entries
     * @param  variableDefinitions      list of variable definitions
     *
     * @throws IllegalArgumentException if valueChanges or variableDefinitions is
     *                                  empty, or if the time scale unit is
     *                                  {@link TimeScaleUnit#PICOSECOND}
     */
    public VCDPlayer(TimeScale timeScale, Map<Long, List<ChangeEntry<?>>> valueChanges,
            Map<String, VariableDefinition> variableDefinitions) {
        Objects.requireNonNull(timeScale);
        Objects.requireNonNull(valueChanges);
        Objects.requireNonNull(variableDefinitions);
        if (valueChanges.isEmpty()) throw new IllegalArgumentException("Value changes can't be empty");
        if (variableDefinitions.isEmpty()) throw new IllegalArgumentException("Variable definitions can't be empty");
        if (timeScale.getUnit().getTimeUnit() == null) {
            throw new IllegalArgumentException(timeScale.getUnit().name() + " time scale is not supported.");
        }
        this.timeScale = timeScale;
        this.variableDefinitions = variableDefinitions;
        for (Map.Entry<Long, List<ChangeEntry<?>>> entry : valueChanges.entrySet())
            this.valueChanges.add(Map.entry(entry.getKey() * timeScale.getResolution(), entry.getValue()));

        this.valueChanges.sort((e1, e2) -> (int) (e1.getKey() - e2.getKey()));
    }

    /**
     * Constructs a new VCD player.<br>
     * This is a convenience constructor accepting a single VCD object.
     *
     * @param  vcd                      the VCD to play
     * @throws IllegalArgumentException if the time scale unit is
     *                                  {@link TimeScaleUnit#PICOSECOND}
     */
    public VCDPlayer(VCD vcd) {
        this(vcd.getTimeScale().getUnit().getTimeUnit() == null ? new TimeScale(TimeScaleUnit.SECOND, 1)
                : vcd.getTimeScale(), vcd.getValueChanges(), vcd.getVariableDefinitions());
        if (vcd.getTimeScale().getUnit().getTimeUnit() == null) {
            throw new IllegalArgumentException(vcd.getTimeScale().getUnit().name()
                    + " time scale is not supported. Try using VCD#setTimeScaleUnit before passing it as an argument.");
        }
    }

    public boolean addListener(PlayerListener listener) {
        Objects.requireNonNull(listener);
        if (!listeners.contains(listener)) return listeners.add(listener);
        return false;
    }

    public List<PlayerListener> getListeners() {
        return Collections.unmodifiableList(listeners);
    }

    public int getSpeedMultiplier() {
        return speedMultiplier;
    }

    public boolean isStarted() {
        return started;
    }

    public boolean removeListener(PlayerListener listener) {
        Objects.requireNonNull(listener);
        return listeners.remove(listener);
    }

    /**
     * Sets playback speed of this player.<br>
     * For example a value of <code>2</code> will result in a 2x faster playback
     *
     * @param speedMultiplier new playback speed
     */
    public void setSpeedMultiplier(int speedMultiplier) {
        if (started) throw new IllegalStateException("Speed multiplier can't be changed when the player is started.");
        this.speedMultiplier = speedMultiplier;
    }

    /**
     * Starts the player
     *
     * @throws IllegalStateException if the player is already started
     */
    public void start() {
        if (isStarted()) throw new IllegalStateException("Player already started");
        started = true;
        long start = System.nanoTime();
        playerService = Executors.newScheduledThreadPool(10);
        playerService.scheduleAtFixedRate(() -> {
            try {
                if (index > valueChanges.size()) {
                    stop();
                    listeners.forEach(ls -> ls.playerStopped(playerTime, System.nanoTime() - start));
                    return;
                }
                long prevTime = index > 0 ? valueChanges.get(index - 1).getKey() : 0;
                playerTime += timeScale.getResolution() * speedMultiplier;
                if (index == valueChanges.size()) {
                    if (playerTime >= prevTime) index++;
                    return;
                }
                long realTime = System.nanoTime() - start;
                List<ChangeEntry<?>> entries = valueChanges.get(index).getValue();
                if (playerTime >= prevTime) {
                    index++;
                    listeners.forEach(ls -> ls.valuesChanged(entries, playerTime, realTime));
                }
                listeners.forEach(ls -> ls.playerTicked(playerTime, realTime));
            } catch (Exception e) {
                e.printStackTrace();
                stop();
                return;
            }
        }, 0, timeScale.getResolution(), timeScale.getUnit().getTimeUnit());
    }

    /**
     * Stops the player. <br>
     * If the player is already stopped, this method does nothing
     */
    public void stop() {
        if (playerService != null) {
            playerService.shutdown();
            playerService = null;
        }
        index = 0;
        playerTime = 0;
        started = false;
    }
}
