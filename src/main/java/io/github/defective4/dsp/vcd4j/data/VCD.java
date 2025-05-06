package io.github.defective4.dsp.vcd4j.data;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class VCD {
    private final String date, version, comment;
    private final Scope scope;
    private final TimeScale timeScale;
    private final Map<Long, List<ChangeEntry<?>>> valueChanges;
    private final Map<String, VariableDefinition> variableDefinitions;

    public VCD(String date, String version, String comment, Scope scope, TimeScale timeScale,
            Map<Long, List<ChangeEntry<?>>> valueChanges, Map<String, VariableDefinition> variableDefinitions) {
        this.date = date;
        this.version = version;
        this.comment = comment;
        this.scope = scope;
        this.timeScale = timeScale;
        this.valueChanges = Collections.unmodifiableMap(valueChanges);
        this.variableDefinitions = Collections.unmodifiableMap(variableDefinitions);
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

    @Override
    public String toString() {
        return "VCD [date=" + date + ", version=" + version + ", comment=" + comment + ", scope=" + scope
                + ", timeScale=" + timeScale + ", valueChanges=" + valueChanges + ", variableDefinitions="
                + variableDefinitions + "]";
    }

}
