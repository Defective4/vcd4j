package io.github.defective4.dsp.vcd4j.data;

import java.util.Objects;

/**
 * Represents a variable used in VCD object
 */
public class VariableDefinition {

    /**
     * Used to represent a variable type.
     */
    public static enum VarType {
        WIRE
    }

    private final byte bitCount;
    private final String id;
    private final String name;
    private final VarType type;

    /**
     * Constructs a new variable definition
     *
     * @param  id                       an identifier used to reference this
     *                                  variable in data section of a VCD.<br>
     *                                  It can be any string of printable characters
     * @param  type                     variable type
     * @param  bitCount                 bits used to represent this variable's
     *                                  value. Use 1 for binary values, and more
     *                                  than 1 for multi-bit values
     * @param  name                     variable name that will be displayed in the
     *                                  VCD viewer
     *
     * @throws NullPointerException     if any of the arguments is <code>null</code>
     * @throws IllegalArgumentException if bitCount is less than 1
     */
    public VariableDefinition(String id, VarType type, int bitCount, String name) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(name);
        Objects.requireNonNull(id);
        if (bitCount < 1) throw new IllegalArgumentException("bitCount < 1");
        this.type = type;
        this.bitCount = (byte) bitCount;
        this.name = name;
        this.id = id;
    }

    public byte getBitCount() {
        return bitCount;
    }

    public String getIdentifier() {
        return id;
    }

    public String getName() {
        return name;
    }

    public VarType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "VCDVariableDef [type=" + type + ", bitCount=" + bitCount + ", name=" + name + "]";
    }

}
