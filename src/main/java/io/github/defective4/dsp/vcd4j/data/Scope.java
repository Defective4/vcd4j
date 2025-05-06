package io.github.defective4.dsp.vcd4j.data;

public class Scope {
    public enum Type {
        MODULE
    }

    private final String name;
    private final Type type;

    public Scope(Type type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return "VCDScope [type=" + type + ", name=" + name + "]";
    }

}
