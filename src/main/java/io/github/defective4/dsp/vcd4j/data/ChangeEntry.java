package io.github.defective4.dsp.vcd4j.data;

import java.util.Objects;

public abstract class ChangeEntry<T> {
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

    public abstract boolean isUndefined();

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [variable=" + variable + ", value=" + value + "]";
    }
}
