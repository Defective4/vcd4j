package io.github.defective4.dsp.vcd4j.data;

import java.util.Objects;

public class Scope {
    public enum Type {
        MODULE
    }

    public static final Scope DEFAULT = new Scope(Type.MODULE, "default");

    private final String name;
    private final Type type;

    public Scope(Type type, String name) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(name);
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
