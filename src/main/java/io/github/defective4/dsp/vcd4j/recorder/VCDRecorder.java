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

public class VCDRecorder {
    public static class Builder {
        private long accuracy = 1;
        private boolean accuracyCeil = false;
        private final TimeScale timeScale;

        public Builder(TimeScale timeScale) {
            Objects.requireNonNull(timeScale);
//            if (timeScale.getUnit().getTimeUnit() == null) throw new IllegalArgumentException(
//                    timeScale.getUnit().name().toLowerCase() + " time scale is not allowed");
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

        public Builder setAccuracy(long accuracy) {
            if (accuracy < 1) throw new IllegalArgumentException("accuracy < 1");
            this.accuracy = accuracy;
            return this;
        }

        public Builder setAccuracyCeil(boolean accuracyCeil) {
            this.accuracyCeil = accuracyCeil;
            return this;
        }
    }

    private final long accuracy;
    private final boolean accuracyCeil;

    private final Map<Long, List<ChangeEntry<?>>> changes = new HashMap<>();
    private long startTimeNanos = -1;
    private final TimeScale timeScale;
    private final Map<String, VariableDefinition> variables = new HashMap<>();

    private VCDRecorder(TimeScale timeScale, long accuracy, boolean accuracyCeil) {
        this.timeScale = timeScale;
        this.accuracy = accuracy;
        this.accuracyCeil = accuracyCeil;
    }

    public VCD getVCD(boolean sorted) {
        if (startTimeNanos != -1) throw new IllegalStateException("Stop the recorder before getting the VCD");
        Map<Long, List<ChangeEntry<?>>> changes = sorted ? new LinkedHashMap<>() : new HashMap<>();
        Map<String, VariableDefinition> variables = new HashMap<>();
        if (sorted) {
            List<Map.Entry<Long, List<ChangeEntry<?>>>> entries = new ArrayList<>(this.changes.entrySet());
            entries.sort((e1, e2) -> (int) (e1.getKey() - e2.getKey()));
            entries.forEach(entry -> changes.put(entry.getKey(), entry.getValue()));
        } else changes.putAll(this.changes);
        variables.putAll(this.variables);
        return new VCD(timeScale, changes, variables);
    }

    public void insertBinaryChange(VariableDefinition variable, State state) {
        if (startTimeNanos == -1) throw new IllegalStateException("Recorder is not started");
        if (variable.getBitCount() != 1)
            throw new IllegalArgumentException("The variable definition's bitCount must be equal to 1");
        VariableDefinition currentDef = variables.get(variable.getKey());
        if (currentDef == null) variables.put(variable.getKey(), variable);
        else if (currentDef.getBitCount() != 1) throw new IllegalArgumentException(String
                .format("Variable \"%s\" with key \"%s\" is already defined with a different bit count",
                        variable.getName(), variable.getKey()));
        long timePassed = calculateTimePassed();
        putEntry(new BinaryChangeEntry(variable, state), timePassed);
    }

    public void insertMultibitChange(VariableDefinition variable, int value) {
        if (startTimeNanos == -1) throw new IllegalStateException("Recorder is not started");
        if (variable.getBitCount() == 1)
            throw new IllegalArgumentException("The variable definition's bitCount must more than 1");
        VariableDefinition currentDef = variables.get(variable.getKey());
        if (currentDef == null) variables.put(variable.getKey(), variable);
        else if (currentDef.getBitCount() != variable.getBitCount()) throw new IllegalArgumentException(String
                .format("Variable \"%s\" with key \"%s\" is already defined with a different bit count",
                        variable.getName(), variable.getKey()));
        long timePassed = calculateTimePassed();
        putEntry(new MultibitChangeEntry(variable, value), timePassed);
    }

    public void start() {
        if (startTimeNanos != -1) return;
        variables.clear();
        changes.clear();
        startTimeNanos = System.nanoTime();
    }

    public void stop() {
        if (startTimeNanos == -1) return;
        startTimeNanos = -1;
    }

    private long calculateTimePassed() {
        long timeNanos = System.nanoTime() - startTimeNanos;
        double div = timeScale.getUnit().getNth() / TimeScaleUnit.NANOSECOND.getNth();
        long timeConverted = (long) (timeNanos / div);
        timeConverted /= timeScale.getValue();
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
