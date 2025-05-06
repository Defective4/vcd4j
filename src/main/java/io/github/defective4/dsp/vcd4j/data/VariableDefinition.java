package io.github.defective4.dsp.vcd4j.data;

public class VariableDefinition {
    public static enum Type {
        WIRE
    }

    private final byte bitCount;
    private final String name;
    private final Type type;

    public VariableDefinition(Type type, byte bitCount, String name) {
        this.type = type;
        this.bitCount = bitCount;
        this.name = name;
    }

    public byte getBitCount() {
        return bitCount;
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
