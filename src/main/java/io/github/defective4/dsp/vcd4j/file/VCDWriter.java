package io.github.defective4.dsp.vcd4j.file;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import io.github.defective4.dsp.vcd4j.data.BinaryChangeEntry;
import io.github.defective4.dsp.vcd4j.data.ChangeEntry;
import io.github.defective4.dsp.vcd4j.data.MultibitChangeEntry;
import io.github.defective4.dsp.vcd4j.data.Scope;
import io.github.defective4.dsp.vcd4j.data.TimeScale;
import io.github.defective4.dsp.vcd4j.data.VCD;
import io.github.defective4.dsp.vcd4j.data.VariableDefinition;

public class VCDWriter {

    private static final String INDENT = "  ";

    private VCDWriter() {}

    public static void write(VCD vcd, File file) throws IOException {
        validate(vcd);
        try (Writer writer = new FileWriter(file)) {
            write(vcd, writer);
        }
    }

    public static void write(VCD vcd, Writer vcdWriter) {
        validate(vcd);
        try (PrintWriter writer = new PrintWriter(vcdWriter)) {
            if (vcd.getDate() != null) writeSection(writer, "date", vcd.getDate(), true);
            if (vcd.getVersion() != null) writeSection(writer, "version", vcd.getVersion(), true);
            if (vcd.getComment() != null) writeSection(writer, "comment", vcd.getComment(), true);
            TimeScale timeScale = vcd.getTimeScale();
            writeSection(writer, "timescale", timeScale.getValue() + timeScale.getUnit().getName());
            Scope scope = vcd.getScope();
            writeSection(writer, "scope", scope.getType().name().toLowerCase() + " " + scope.getName());
            for (Map.Entry<String, VariableDefinition> entry : vcd.getVariableDefinitions().entrySet()) {
                VariableDefinition value = entry.getValue();
                writeSection(writer, "var", value.getType().name().toLowerCase() + " " + value.getBitCount() + " "
                        + value.getKey() + " " + value.getName());
            }
            writer.println("$upscope $end");
            writer.println("$enddefinitions $end");
            writer.println("$dumpvars");
            for (Map.Entry<Long, List<ChangeEntry<?>>> entry : vcd.getValueChanges().entrySet()) {
                for (ChangeEntry<?> val : entry.getValue()) {
                    VariableDefinition def = val.getVariable();
                    if (val instanceof MultibitChangeEntry mb) {
                        writer.print("b");
                        if (mb.isUndefined()) writer.println("x".repeat(def.getBitCount()) + " " + def.getKey());
                        else {
                            String bitsString = Integer.toBinaryString(mb.getValue());
                            if (bitsString.length() < def.getBitCount()) {
                                int len = bitsString.length();
                                for (int i = len; i < def.getBitCount(); i++) {
                                    writer.print("0");
                                }
                            }
                            writer.println(bitsString + " " + def.getKey());
                        }
                    } else if (val instanceof BinaryChangeEntry bb) {
                        writer.println(bb.getValue().getChar() + def.getKey());
                    }
                }
                writer.println("#" + entry.getKey());
            }
        }
    }

    private static void validate(VCD vcd) {
        Map<String, VariableDefinition> def = vcd.getVariableDefinitions();
        Map<Long, List<ChangeEntry<?>>> changes = vcd.getValueChanges();
        for (List<ChangeEntry<?>> list : changes.values()) for (ChangeEntry<?> entry : list)
            if (!def.containsKey(entry.getVariable().getKey())) throw new IllegalArgumentException(
                    String.format("Variable \"%s\" is undefined", entry.getVariable().getKey()));
    }

    private static void writeSection(PrintWriter writer, String tag, String data) {
        writeSection(writer, tag, data, false);
    }

    private static void writeSection(PrintWriter writer, String tag, String data, boolean indent) {
        if (indent) {
            writer.println("$" + tag);
            writer.println(INDENT + data);
            writer.println("$end");
        } else {
            writer.println("$" + tag + " " + data + " $end");
        }
    }
}
