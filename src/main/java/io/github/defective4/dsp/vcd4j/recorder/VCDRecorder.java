package io.github.defective4.dsp.vcd4j.recorder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.github.defective4.dsp.vcd4j.data.BinaryChangeEntry;
import io.github.defective4.dsp.vcd4j.data.ChangeEntry;
import io.github.defective4.dsp.vcd4j.data.MultibitChangeEntry;
import io.github.defective4.dsp.vcd4j.data.State;
import io.github.defective4.dsp.vcd4j.data.TimeScale;
import io.github.defective4.dsp.vcd4j.data.TimeScale.TimeScaleUnit;
import io.github.defective4.dsp.vcd4j.data.VCD;
import io.github.defective4.dsp.vcd4j.data.VariableDefinition;
import io.github.defective4.dsp.vcd4j.player.VCDPlayer;

/**
 * Records value changes and stores them in VCD
 */
public class VCDRecorder {
    /**
     * The builder class
     */
    public static class Builder {
        private long accuracy = 1;
        private boolean accuracyCeil = false;
        private final TimeScale timeScale;

        /**
         * Constructs a new builder
         *
         * @param timeScale a non-null time scale. Unlike {@link VCDPlayer},
         *                  {@link VCDRecorder} accepts {@link TimeScaleUnit#PICOSECOND}
         */
        public Builder(TimeScale timeScale) {
            Objects.requireNonNull(timeScale);
            this.timeScale = timeScale;
        }

        public VCDRecorder build() {
            return new VCDRecorder(timeScale, accuracy, accuracyCeil);
        }

        public long getAccuracy() {
            return accuracy;
        }

        public boolean isAccuracyCeil() {
            return accuracyCeil;
        }

        /**
         * Set this recorder's accuracy.<br>
         * For example a value of 500 means that the recorder will round all value
         * changes to nearest multiply of 500 of its time scale's units.<br>
         * <b>Default value:</b> 1
         *
         * @param  accuracy
         * @return
         * @throws IllegalArgumentException if accuracy is less than 1
         */
        public Builder setAccuracy(long accuracy) {
            if (accuracy < 1) throw new IllegalArgumentException("accuracy < 1");
            this.accuracy = accuracy;
            return this;
        }

        /**
         * It set to true, value change times will be rounded up.<br>
         * Only useful is accuracy is set to more than 1.<br>
         * <b>Default value:</b> false
         *
         * @param  accuracyCeil
         * @return
         */
        public Builder setAccuracyCeil(boolean accuracyCeil) {
            this.accuracyCeil = accuracyCeil;
            return this;
        }
    }

    private final long accuracy;
    private final boolean accuracyCeil;

    private final Map<Long, List<ChangeEntry<?>>> changes = new LinkedHashMap<>();
    private long startTimeNanos = -1;
    private final TimeScale timeScale;
    private final Map<String, VariableDefinition> variables = new HashMap<>();

    private VCDRecorder(TimeScale timeScale, long accuracy, boolean accuracyCeil) {
        this.timeScale = timeScale;
        this.accuracy = accuracy;
        this.accuracyCeil = accuracyCeil;
    }

    /**
     * Get recorderd VCD.<br>
     * Only usable after starting, then stopping the recorder.
     *
     * @return                       new VCD
     * @throws IllegalStateException if the recorder was not stopped before using
     *                               this method
     */
    public VCD getVCD() {
        if (startTimeNanos != -1) throw new IllegalStateException("Stop the recorder before getting the VCD");
        Map<Long, List<ChangeEntry<?>>> changes = new LinkedHashMap<>();
        Map<String, VariableDefinition> variables = new HashMap<>();
        changes.putAll(this.changes);
        variables.putAll(this.variables);
        return new VCD(timeScale, changes, variables);
    }

    /**
     * Insert a new 1-bit change at current time relative to recorder's startup
     * time.
     *
     * @param  variable                 the variable definition
     * @param  state                    state of the variable at time of insertion
     * @throws IllegalStateException    if the recorder is not started
     * @throws IllegalArgumentException if the variable definition's bit count is
     *                                  not equal to 1, or if the variable is
     *                                  already defined in the recorder with a
     *                                  different bit count
     */
    public void insertBinaryChange(VariableDefinition variable, State state) {
        if (startTimeNanos == -1) throw new IllegalStateException("Recorder is not started");
        if (variable.getBitCount() != 1)
            throw new IllegalArgumentException("The variable definition's bitCount must be equal to 1");
        VariableDefinition currentDef = variables.get(variable.getIdentifier());
        if (currentDef == null) variables.put(variable.getIdentifier(), variable);
        else if (currentDef.getBitCount() != 1) throw new IllegalArgumentException(String
                .format("Variable \"%s\" with key \"%s\" is already defined with a different bit count",
                        variable.getName(), variable.getIdentifier()));
        long timePassed = calculateTimePassed();
        putEntry(new BinaryChangeEntry(variable, state), timePassed);
    }

    /**
     * Insert a new mutli-bit change at current time relative to recorder's startup
     * time.
     *
     * @param  variable                 the variable definition
     * @param  value                    value of the variable at time of insertion
     * @throws IllegalStateException    if the recorder is not started
     * @throws IllegalArgumentException if the variable is already defined in the
     *                                  recorder with a different bit count
     */
    public void insertMultibitChange(VariableDefinition variable, int value) {
        if (startTimeNanos == -1) throw new IllegalStateException("Recorder is not started");
        VariableDefinition currentDef = variables.get(variable.getIdentifier());
        if (currentDef == null) variables.put(variable.getIdentifier(), variable);
        else if (currentDef.getBitCount() != variable.getBitCount()) throw new IllegalArgumentException(String
                .format("Variable \"%s\" with key \"%s\" is already defined with a different bit count",
                        variable.getName(), variable.getIdentifier()));
        long timePassed = calculateTimePassed();
        putEntry(new MultibitChangeEntry(variable, value), timePassed);
    }

    /**
     * Starts the recorder
     */
    public void start() {
        if (startTimeNanos != -1) return;
        variables.clear();
        changes.clear();
        startTimeNanos = System.nanoTime();
    }

    /**
     * Stops the recorder
     */
    public void stop() {
        if (startTimeNanos == -1) return;
        long time = calculateTimePassed();
        startTimeNanos = -1;
        List<Map.Entry<Long, List<ChangeEntry<?>>>> entries = new ArrayList<>(changes.entrySet());
        Map<Long, List<ChangeEntry<?>>> changes = new LinkedHashMap<>();
        List<ChangeEntry<?>> defaultEntries = new ArrayList<>();
        for (VariableDefinition def : variables.values()) defaultEntries
                .add(def.getBitCount() == 1 ? new BinaryChangeEntry(def, State.UNDEFINED)
                        : new MultibitChangeEntry(def, MultibitChangeEntry.UNDEFINED));
        if (entries.isEmpty()) {
            changes.put(time, defaultEntries);
        } else {
            changes.put(entries.get(0).getKey(), defaultEntries);
            for (int i = 0; i < entries.size() - 1; i++) {
                changes.put(entries.get(i + 1).getKey(), entries.get(i).getValue());
            }
            if (!changes.containsKey(time)) changes.put(time, entries.get(entries.size() - 1).getValue());
        }
        this.changes.clear();
        this.changes.putAll(changes);
    }

    private long calculateTimePassed() {
        long timeNanos = System.nanoTime() - startTimeNanos;
        double div = timeScale.getUnit().getNth() / TimeScaleUnit.NANOSECOND.getNth();
        long timeConverted = (long) (timeNanos / div);
        timeConverted /= timeScale.getResolution();
        if (accuracy != 1) {
            double divResult = timeConverted / (double) accuracy;
            timeConverted = (long) (accuracyCeil ? Math.ceil(divResult) : divResult) * accuracy;
        }
        return timeConverted;
    }

    private void putEntry(ChangeEntry<?> entry, long timePassed) {
        if (!changes.containsKey(timePassed)) changes.put(timePassed, new ArrayList<>());
        changes.get(timePassed).add(entry);
    }
}
