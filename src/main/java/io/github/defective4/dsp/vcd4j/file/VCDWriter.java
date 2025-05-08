package io.github.defective4.dsp.vcd4j.file;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.github.defective4.dsp.vcd4j.data.BinaryChangeEntry;
import io.github.defective4.dsp.vcd4j.data.ChangeEntry;
import io.github.defective4.dsp.vcd4j.data.MultibitChangeEntry;
import io.github.defective4.dsp.vcd4j.data.Scope;
import io.github.defective4.dsp.vcd4j.data.TimeScale;
import io.github.defective4.dsp.vcd4j.data.VCD;
import io.github.defective4.dsp.vcd4j.data.VariableDefinition;

/**
 * Provides methods to write {@link VCD} objects to VCD files.
 *
 * This class supports writing VCD data to output files and writers.
 */
public class VCDWriter {

    private static final String INDENT = "  ";

    private VCDWriter() {}

    public static void write(VCD vcd, File file) throws IOException {
        write(vcd, file, StandardCharsets.UTF_8);
    }

    public static void write(VCD vcd, File file, Charset charset) throws IOException {
        Objects.requireNonNull(file);
        Objects.requireNonNull(vcd);
        Objects.requireNonNull(charset);
        validate(vcd);
        try (Writer writer = new FileWriter(file)) {
            write(vcd, writer);
        }
    }

    public static void write(VCD vcd, String file) throws IOException {
        write(vcd, new File(file));
    }

    public static void write(VCD vcd, String file, Charset charset) throws IOException {
        write(vcd, new File(file), charset);
    }

    public static void write(VCD vcd, Writer vcdWriter) {
        Objects.requireNonNull(vcdWriter);
        validate(vcd);
        try (PrintWriter writer = new PrintWriter(vcdWriter)) {
            if (vcd.getDate() != null) writeSection(writer, "date", vcd.getDate(), true);
            if (vcd.getVersion() != null) writeSection(writer, "version", vcd.getVersion(), true);
            if (vcd.getComment() != null) writeSection(writer, "comment", vcd.getComment(), true);
            TimeScale timeScale = vcd.getTimeScale();
            writeSection(writer, "timescale", timeScale.getResolution() + timeScale.getUnit().getName());
            Scope scope = vcd.getScope();
            writeSection(writer, "scope", scope.getType().name().toLowerCase() + " " + scope.getName());
            for (Map.Entry<String, VariableDefinition> entry : vcd.getVariableDefinitions().entrySet()) {
                VariableDefinition value = entry.getValue();
                writeSection(writer, "var", value.getType().name().toLowerCase() + " " + value.getBitCount() + " "
                        + value.getIdentifier() + " " + value.getName());
            }
            writer.println("$upscope $end");
            writer.println("$enddefinitions $end");
            writer.println("$dumpvars");
            Map<Long, List<ChangeEntry<?>>> valueChanges = vcd.getValueChanges();
            List<Map.Entry<Long, List<ChangeEntry<?>>>> sorted = new ArrayList<>(valueChanges.entrySet());
            sorted.sort((e1, e2) -> (int) (e1.getKey() - e2.getKey()));
            for (Map.Entry<Long, List<ChangeEntry<?>>> entry : sorted) {
                for (ChangeEntry<?> val : entry.getValue()) {
                    VariableDefinition def = val.getVariable();
                    if (val instanceof MultibitChangeEntry mb) {
                        writer.print("b");
                        if (mb.isUndefined()) writer.println("x".repeat(def.getBitCount()) + " " + def.getIdentifier());
                        else {
                            String bitsString = Integer.toBinaryString(mb.getValue());
                            if (bitsString.length() < def.getBitCount()) {
                                int len = bitsString.length();
                                for (int i = len; i < def.getBitCount(); i++) {
                                    writer.print("0");
                                }
                            }
                            writer.println(bitsString + " " + def.getIdentifier());
                        }
                    } else if (val instanceof BinaryChangeEntry bb) {
                        writer.println(bb.getValue().getChar() + def.getIdentifier());
                    } else throw new IllegalStateException("Unrecognized change entry class: " + val.getClass());
                }
                writer.println("#" + entry.getKey());
            }
        }
    }

    private static void validate(VCD vcd) {
        Map<String, VariableDefinition> def = vcd.getVariableDefinitions();
        Map<Long, List<ChangeEntry<?>>> changes = vcd.getValueChanges();
        for (List<ChangeEntry<?>> list : changes.values()) for (ChangeEntry<?> entry : list)
            if (!def.containsKey(entry.getVariable().getIdentifier())) throw new IllegalArgumentException(
                    String.format("Variable \"%s\" is undefined", entry.getVariable().getIdentifier()));
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
