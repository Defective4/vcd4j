package io.github.defective4.dsp.vcd4j.data;

/**
 * Represents a multi-bit (integer) change of variable's state
 */
public final class MultibitChangeEntry extends ChangeEntry<Integer> {

    /**
     * Indicates that this entry has an undefined value
     */
    public static final int UNDEFINED = -1;

    /**
     * Constructs a new multi-bit change entry
     *
     * @param  variable                 associated variable
     * @param  value                    an integer value. It has to be more than or
     *                                  equal to {@value #UNDEFINED}
     *                                  ({@link #UNDEFINED}).
     *
     * @throws IllegalArgumentException if the value is less than
     *                                  {@value #UNDEFINED} or its bits count is not
     *                                  equal to
     *                                  {@link VariableDefinition#getBitCount()}
     */
    public MultibitChangeEntry(VariableDefinition variable, int value) {
        super(variable, value);
        if (value < -1) throw new IllegalArgumentException("value < -1");
        if (!isUndefined() && Integer.toBinaryString(value).length() > variable.getBitCount())
            throw new IllegalArgumentException("value must take less or equal bits to the defined variable's bitCount");
    }

    @Override
    public boolean isUndefined() {
        return getValue() == UNDEFINED;
    }
}
