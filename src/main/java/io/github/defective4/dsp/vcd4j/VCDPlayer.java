package io.github.defective4.dsp.vcd4j;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.github.defective4.dsp.vcd4j.data.ChangeEntry;
import io.github.defective4.dsp.vcd4j.data.TimeScale;
import io.github.defective4.dsp.vcd4j.data.VCD;
import io.github.defective4.dsp.vcd4j.data.VariableDefinition;

public class VCDPlayer {
    private final TimeScale timeScale;
    private final Map<Long, List<ChangeEntry<?>>> valueChanges;
    private final Map<String, VariableDefinition> variableDefinitions;

    public VCDPlayer(TimeScale timeScale, Map<Long, List<ChangeEntry<?>>> valueChanges,
            Map<String, VariableDefinition> variableDefinitions) {
        Objects.requireNonNull(timeScale);
        Objects.requireNonNull(valueChanges);
        Objects.requireNonNull(variableDefinitions);
        this.timeScale = timeScale;
        this.valueChanges = valueChanges;
        this.variableDefinitions = variableDefinitions;
    }

    public VCDPlayer(VCD vcd) {
        this(vcd.getTimeScale(), vcd.getValueChanges(), vcd.getVariableDefinitions());
    }
}
