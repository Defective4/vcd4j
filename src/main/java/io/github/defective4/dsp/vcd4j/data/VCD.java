package io.github.defective4.dsp.vcd4j.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.github.defective4.dsp.vcd4j.data.TimeScale.TimeScaleUnit;
import io.github.defective4.dsp.vcd4j.player.VCDPlayer;

/**
 * Represents a complete Value Change Dump (VCD).
 * <p>
 * This class provides methods for accessing and manipulating the fields of a
 * VCD object, including date, version, variable definitions, and value changes.
 * </p>
 */
public class VCD {
    private String date, version, comment;
    private Scope scope;
    private TimeScale timeScale;
    private final Map<Long, List<ChangeEntry<?>>> valueChanges;
    private final Map<String, VariableDefinition> variableDefinitions;

    /**
     * Constructs a new Value Change Dump with all supported fields
     *
     * @param date                human readable date of creation
     * @param version             human readable version of software that created
     *                            this VCD
     * @param comment
     * @param scope               primary scope of the VCD
     * @param timeScale           time scale of this VCD
     * @param valueChanges        a map of all value changes that will be stored in
     *                            this VCD
     * @param variableDefinitions a map of variable definitions
     */
    public VCD(String date, String version, String comment, Scope scope, TimeScale timeScale,
            Map<Long, List<ChangeEntry<?>>> valueChanges, Map<String, VariableDefinition> variableDefinitions) {
        Objects.requireNonNull(scope);
        Objects.requireNonNull(timeScale);
        Objects.requireNonNull(valueChanges);
        Objects.requireNonNull(variableDefinitions);
        for (Map.Entry<String, VariableDefinition> entry : variableDefinitions.entrySet()) {
            if (!entry.getKey().equals(entry.getValue().getIdentifier()))
                throw new IllegalArgumentException("Map key and variable key mismatch");
        }
        this.date = date;
        this.version = version;
        this.comment = comment;
        this.scope = scope;
        this.timeScale = timeScale;
        this.valueChanges = valueChanges;
        this.variableDefinitions = variableDefinitions;
    }

    /**
     * Simplified constructor for when you need to manually construct the VCD
     *
     * @param timeScale           time scale of this VCD
     * @param valueChanges        a map of all value changes that will be stored in
     *                            this VCD
     * @param variableDefinitions a map of variable definitions
     */
    public VCD(TimeScale timeScale, Map<Long, List<ChangeEntry<?>>> valueChanges,
            Map<String, VariableDefinition> variableDefinitions) {
        this(null, null, null, Scope.DEFAULT, timeScale, valueChanges, variableDefinitions);
    }

    /**
     * Attempts to bring the time resolution to 1`time unit`.<br>
     * For example if the VCD's time scale is 1000ms, this method will try to bring
     * it down to 1ms, while also multiplying all of value change times.<br>
     * This method can be useful when trying to convert the VCD to a time scale with
     * different time unit (for example from picoseconds to nanoseconds)
     */
    public void adjustTimeScale() {
        long oldValue = timeScale.getResolution();
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
        return valueChanges;
    }

    public Map<String, VariableDefinition> getVariableDefinitions() {
        return variableDefinitions;
    }

    public String getVersion() {
        return version;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public void setTimeScale(TimeScale timeScale) {
        this.timeScale = timeScale;
    }

    /**
     * Sets this VCD's time scale unit. <br>
     * if adjustValues is <code>true</code>, also tries to adjust all value change
     * entries' times to match the new unit. <br>
     * <br>
     * This method is useful when trying to play back VCDs with a
     * {@link TimeScaleUnit#PICOSECOND} time scale, because picosecond time scales
     * aren't supported by {@link VCDPlayer}
     *
     * @param  unit                  new time scale unit
     * @param  adjustValues          if the change entries should have their times
     *                               adjusted
     *
     * @throws IllegalStateException if adjustValues is <code>true</code> and
     *                               adjustment of any value change would result in
     *                               a timestamp equal to 0 (unless the timestamp
     *                               was 0 already)
     */
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
        timeScale = new TimeScale(unit, timeScale.getResolution());
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "VCD [date=" + date + ", version=" + version + ", comment=" + comment + ", scope=" + scope
                + ", timeScale=" + timeScale + ", valueChanges=" + valueChanges + ", variableDefinitions="
                + variableDefinitions + "]";
    }

    /**
     * @param  entries
     * @return         a list containing all provided change entries
     */
    public static List<ChangeEntry<?>> makeChangeEntriesList(ChangeEntry<?>... entries) {
        List<ChangeEntry<?>> list = new ArrayList<>();
        Collections.addAll(list, entries);
        return list;
    }

}
