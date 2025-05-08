package io.github.defective4.dsp.vcd4j.data;

/**
 * Represents a one-bit binary change entry in a VCD file.
 *
 * This class is an extension of {@link ChangeEntry} for handling binary state
 * changes.
 */
public final class BinaryChangeEntry extends ChangeEntry<State> {

    /**
     * Constructs a new binary change entry
     *
     * @param  variable                 associated variable
     * @param  value                    associated non-null value.
     *
     * @throws IllegalArgumentException if variable's bit count is more than 1
     */
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
