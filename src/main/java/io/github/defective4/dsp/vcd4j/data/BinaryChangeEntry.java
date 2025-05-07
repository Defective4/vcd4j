package io.github.defective4.dsp.vcd4j.data;

public final class BinaryChangeEntry extends ChangeEntry<State> {
    public BinaryChangeEntry(VariableDefinition variable, State value) {
        super(variable, value);
        if (variable.getBitCount() != 1)
            throw new IllegalArgumentException("Only 1-bit variables are allowed in binary change entry");
    }

    @Override
    public boolean isUndefined() {
        return getValue() == State.UNDEFINED;
    }
}
