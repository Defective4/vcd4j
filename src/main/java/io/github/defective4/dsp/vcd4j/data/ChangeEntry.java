package io.github.defective4.dsp.vcd4j.data;

import java.util.Objects;

/**
 * Abstract base class for representing a change entry in a VCD file.
 *
 * This class provides common functionality for storing value change
 * information.
 *
 * @param <T> The type of value stored in the change entry.
 */
public abstract sealed class ChangeEntry<T> permits BinaryChangeEntry, MultibitChangeEntry {
    private final T value;
    private final VariableDefinition variable;

    protected ChangeEntry(VariableDefinition variable, T value) {
        Objects.requireNonNull(variable);
        Objects.requireNonNull(value);
        this.variable = variable;
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public VariableDefinition getVariable() {
        return variable;
    }

    /**
     * Checks if the change entry has an undefined value.<br>
     * For {@link BinaryChangeEntry} it's {@link State}.UNDEFINED<br>
     * For {@link MultibitChangeEntry} it's {@link MultibitChangeEntry#UNDEFINED}
     *
     * @return
     */
    public abstract boolean isUndefined();

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [variable=" + variable + ", value=" + value + "]";
    }
}
