package io.github.defective4.dsp.vcd4j.data;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.github.defective4.dsp.vcd4j.data.TimeScale.TimeScaleUnit;

public class VCD {
    private final String date, version, comment;
    private final Scope scope;
    private TimeScale timeScale;
    private final Map<Long, List<ChangeEntry<?>>> valueChanges;
    private final Map<String, VariableDefinition> variableDefinitions;

    public VCD(String date, String version, String comment, Scope scope, TimeScale timeScale,
            Map<Long, List<ChangeEntry<?>>> valueChanges, Map<String, VariableDefinition> variableDefinitions) {
        Objects.requireNonNull(scope);
        Objects.requireNonNull(timeScale);
        Objects.requireNonNull(valueChanges);
        Objects.requireNonNull(variableDefinitions);
        this.date = date;
        this.version = version;
        this.comment = comment;
        this.scope = scope;
        this.timeScale = timeScale;
        this.valueChanges = valueChanges;
        this.variableDefinitions = Collections.unmodifiableMap(variableDefinitions);
    }

    public VCD(TimeScale timeScale, Map<Long, List<ChangeEntry<?>>> valueChanges,
            Map<String, VariableDefinition> variableDefinitions) {
        this(null, null, null, Scope.DEFAULT, timeScale, valueChanges, variableDefinitions);
    }

    public void adjustTimeScale() {
        long oldValue = timeScale.getValue();
        timeScale = new TimeScale(timeScale.getUnit(), 1);
        Map<Long, List<ChangeEntry<?>>> converted = new LinkedHashMap<>();
        for (Map.Entry<Long, List<ChangeEntry<?>>> entry : valueChanges.entrySet()) {
            converted.put(entry.getKey() * oldValue, entry.getValue());
        }
        valueChanges.clear();
        valueChanges.putAll(converted);
    }

    public String getComment() {
        return comment;
    }

    public String getDate() {
        return date;
    }

    public Scope getScope() {
        return scope;
    }

    public TimeScale getTimeScale() {
        return timeScale;
    }

    public Map<Long, List<ChangeEntry<?>>> getValueChanges() {
        return Collections.unmodifiableMap(valueChanges);
    }

    public Map<String, VariableDefinition> getVariableDefinitions() {
        return variableDefinitions;
    }

    public String getVersion() {
        return version;
    }

    public void setTimeScaleUnit(TimeScaleUnit unit, boolean adjustValues) {
        Objects.requireNonNull(unit);
        if (adjustValues) {
            double multiplier = timeScale.getUnit().getNth() / unit.getNth();
            Map<Long, List<ChangeEntry<?>>> converted = new LinkedHashMap<>();
            for (Map.Entry<Long, List<ChangeEntry<?>>> entry : valueChanges.entrySet()) {
                double result;
                if (entry.getKey() == 0) {
                    result = 0;
                } else {
                    result = entry.getKey() * multiplier;
                    if (result < 1) throw new IllegalStateException(
                            "Couldn't convert value " + entry.getKey() + " because the division result would be 0");
                }
                converted.put((long) Math.floor(result), entry.getValue());
            }
            valueChanges.clear();
            valueChanges.putAll(converted);
        }
        timeScale = new TimeScale(unit, timeScale.getValue());
    }

    @Override
    public String toString() {
        return "VCD [date=" + date + ", version=" + version + ", comment=" + comment + ", scope=" + scope
                + ", timeScale=" + timeScale + ", valueChanges=" + valueChanges + ", variableDefinitions="
                + variableDefinitions + "]";
    }

}
