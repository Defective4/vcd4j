package io.github.defective4.dsp.vcd4j.data;

public final class MultibitChangeEntry extends ChangeEntry<Integer> {

    public static final int UNDEFINED = -1;

    public MultibitChangeEntry(VariableDefinition variable, int value) {
        super(variable, value);
        if (value < -1) throw new IllegalArgumentException("value < -1");
        if (!isUndefined() && Integer.toBinaryString(value).length() > variable.getBitCount())
            throw new IllegalArgumentException(
                    "value must take less or equal bits to the defined variable's bitCount");
    }

    @Override
    public boolean isUndefined() {
        return getValue() == UNDEFINED;
    }
}
