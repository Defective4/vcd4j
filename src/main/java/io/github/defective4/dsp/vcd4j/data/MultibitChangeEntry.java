package io.github.defective4.dsp.vcd4j.data;

public class MultibitChangeEntry extends ChangeEntry<Integer> {

    public static final int UNDEFINED = -1;

    public MultibitChangeEntry(VariableDefinition variable, int value) {
        super(variable, value);
    }

    @Override
    public boolean isUndefined() {
        return getValue() == UNDEFINED;
    }
}
