package io.github.defective4.dsp.vcd4j.data;

public class BinaryChangeEntry extends ChangeEntry<State> {
    public BinaryChangeEntry(VariableDefinition variable, State value) {
        super(variable, value);
    }

    @Override
    public boolean isUndefined() {
        return getValue() == State.UNDEFINED;
    }
}
