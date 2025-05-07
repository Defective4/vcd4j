package io.github.defective4.dsp.vcd4j.data;

import java.util.Objects;

public class VariableDefinition {
    public static enum Type {
        WIRE
    }

    private final byte bitCount;
    private final String key;
    private final String name;
    private final Type type;

    public VariableDefinition(String key, Type type, byte bitCount, String name) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(name);
        if (bitCount < 1) throw new IllegalArgumentException("bitCount < 1");
        this.type = type;
        this.bitCount = bitCount;
        this.name = name;
        this.key = key;
    }

    public byte getBitCount() {
        return bitCount;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return "VCDVariableDef [type=" + type + ", bitCount=" + bitCount + ", name=" + name + "]";
    }

}
