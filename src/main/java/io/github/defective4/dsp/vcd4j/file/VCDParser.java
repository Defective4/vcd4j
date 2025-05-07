package io.github.defective4.dsp.vcd4j.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.github.defective4.dsp.vcd4j.data.BinaryChangeEntry;
import io.github.defective4.dsp.vcd4j.data.ChangeEntry;
import io.github.defective4.dsp.vcd4j.data.MultibitChangeEntry;
import io.github.defective4.dsp.vcd4j.data.Scope;
import io.github.defective4.dsp.vcd4j.data.Scope.Type;
import io.github.defective4.dsp.vcd4j.data.State;
import io.github.defective4.dsp.vcd4j.data.TimeScale;
import io.github.defective4.dsp.vcd4j.data.TimeScale.TimeScaleUnit;
import io.github.defective4.dsp.vcd4j.data.VCD;
import io.github.defective4.dsp.vcd4j.data.VariableDefinition;

public class VCDParser {
    private VCDParser() {}

    public static VCD parse(File vcdFile) throws FileNotFoundException, IOException {
        Objects.requireNonNull(vcdFile);
        try (Reader reader = new FileReader(vcdFile)) {
            return parse(reader);
        }
    }

    public static VCD parse(Reader vcdReader) throws IOException {
        Objects.requireNonNull(vcdReader);
        TimeScale timeScale = null;
        Scope scope = null;
        String date = null;
        String version = null;
        String comment = null;
        Map<String, VariableDefinition> variables = new HashMap<>();
        // Read header
        try (BufferedReader reader = new BufferedReader(vcdReader)) {
            while (true) {
                readUntilToken(reader, '$');
                String tag = readUntilSpace(reader).trim().toLowerCase();
                if ("dumpvars".equals(tag)) break;
                String data = formatData(readUntilTag(reader, "end").trim().replace("\n", " ").replace("\r", " "));
                switch (tag) {
                    case "var" -> {
                        String[] split = data.split(" ");
                        String typeName = split[0];
                        String bitsStr = split[1];
                        String key = split[2];
                        String name = split[3];

                        VariableDefinition.Type type;
                        try {
                            type = VariableDefinition.Type.valueOf(typeName.toUpperCase());
                        } catch (Exception e) {
                            throw new IOException(String
                                    .format("Inalid variable type \"%s\" for variable \"%s\". Only \"%s\" is supported.",
                                            typeName, name, VariableDefinition.Type.values()[0].name().toLowerCase()));
                        }

                        byte bitsCount;
                        try {
                            bitsCount = Byte.parseByte(bitsStr);
                            if (bitsCount < 1) throw new IllegalStateException();
                        } catch (Exception e) {
                            throw new IOException("Invalid bits count: " + bitsStr);
                        }

                        variables.put(key, new VariableDefinition(key, type, bitsCount, name));
                    }
                    case "date" -> date = data;
                    case "version" -> version = data;
                    case "comment" -> comment = data;
                    case "scope" -> {
                        String[] split = data.split(" ");
                        String typeName = split[0];
                        Type type;
                        try {
                            type = Type.valueOf(typeName.toUpperCase());
                        } catch (Exception e) {
                            throw new IOException(String
                                    .format("Invalid scope type \"%s\". Only \"%s\" is supported", typeName,
                                            Type.values()[0].name().toLowerCase()));
                        }
                        String name = String.join(" ", Arrays.copyOfRange(split, 1, split.length));
                        scope = new Scope(type, name);
                    }
                    case "timescale" -> {
                        TimeScaleUnit timeScaleUnit = TimeScaleUnit.parseTimeUnit(data);
                        long timeScaleValue;
                        try {
                            timeScaleValue = Long
                                    .parseLong(data.substring(0, data.length() - timeScaleUnit.getName().length()));
                            if (timeScaleValue < 0) throw new IllegalStateException();
                        } catch (Exception e) {
                            throw new IOException("Invalid timescale value \"" + data + "\"");
                        }
                        timeScale = new TimeScale(timeScaleUnit, timeScaleValue);
                    }
                    default -> {}
                }
            }
            if (timeScale == null) throw new IOException("Timescale is missing from the VCD file");
            if (scope == null) throw new IOException("Scope is missing from the VCD file");
            // Begin reading dumpvars
            List<ChangeEntry<?>> entries = new ArrayList<>();
            Map<Long, List<ChangeEntry<?>>> changesMap = new LinkedHashMap<>();
            while (true) {
                String line = reader.readLine();
                if (line == null) break;
                if (line.isBlank() || line.startsWith("$")) continue;
                if (line.startsWith("#")) {
                    try {
                        long timestamp = Long.parseLong(line.substring(1));
                        List<ChangeEntry<?>> nl = new ArrayList<>(entries);
                        entries.clear();
                        changesMap.put(timestamp, nl);
                    } catch (Exception e) {
                        throw new IOException("Invalid timestamp in line " + line);
                    }
                } else if (line.startsWith("b")) {
                    int ix = line.indexOf(' ');
                    if (ix < 0) throw new IOException("Invalid multibit variable found: " + line);
                    String bits = line.substring(1, ix).toLowerCase();
                    String key = line.substring(ix + 1);
                    VariableDefinition variable = variables.get(key);
                    if (variable == null) throw new IOException(
                            String.format("No variable bound to string \"%s\" in line: %s", key, line));
                    int value;
                    try {
                        if (bits.equals("x".repeat(bits.length()))) {
                            value = MultibitChangeEntry.UNDEFINED;
                        } else value = Integer.parseInt(bits, 2);
                    } catch (Exception e) {
                        throw new IOException("Invalid multibit value for variable on line: " + line);
                    }
                    entries.add(new MultibitChangeEntry(variable, value));
                } else {
                    char c = Character.toLowerCase(line.charAt(0));
                    String key = line.substring(1);
                    VariableDefinition variable = variables.get(key);
                    if (variable == null) throw new IOException(
                            String.format("No variable bound to string \"%s\" in line: %s", key, line));
                    State state = switch (c) {
                        case '0' -> State.LOW;
                        case '1' -> State.HIGH;
                        case 'x' -> State.UNDEFINED;
                        case 'z' -> State.FLOATING;
                        default -> throw new IOException(String.format("Invalid state \"%s\" on line: %s", c, line));
                    };
                    entries.add(new BinaryChangeEntry(variable, state));
                }
            }
            if (!changesMap.containsKey(0l)) {
                List<ChangeEntry<?>> nl = new ArrayList<>();
                for (VariableDefinition def : variables.values()) {
                    nl
                            .add(def.getBitCount() == 1 ? new BinaryChangeEntry(def, State.UNDEFINED)
                                    : new MultibitChangeEntry(def, MultibitChangeEntry.UNDEFINED));
                }
            }
            return new VCD(date, version, comment, scope, timeScale, changesMap, variables);
        }
    }

    private static String formatData(String data) {
        StringBuilder builder = new StringBuilder();
        for (char c : data.toCharArray()) {
            if (Character.isWhitespace(c)) builder.append(' ');
            else builder.append(c);
        }
        String formatted = builder.toString();
        while (formatted.contains("  ")) formatted = formatted.replace("  ", " ");
        return formatted;
    }

    private static String readUntilSpace(Reader reader) throws IOException {
        StringBuilder builder = new StringBuilder();
        while (true) {
            int read = reader.read();
            if (read < 0) throw new IOException("End of stream");
            char c = (char) read;
            if (Character.isWhitespace(c)) break;
            builder.append(c);
        }
        return builder.toString();
    }

    private static String readUntilTag(Reader reader, String tag) throws IOException {
        StringBuilder builder = new StringBuilder();
        while (true) {
            int read = reader.read();
            if (read < 0) throw new IOException("End of stream");
            char c = (char) read;
            builder.append(c);
            if (builder.toString().endsWith("$" + tag)) {
                builder = new StringBuilder(builder.substring(0, builder.length() - tag.length() - 1));
                break;
            }
        }
        return builder.toString();
    }

    private static String readUntilToken(Reader reader, char token) throws IOException {
        StringBuilder builder = new StringBuilder();
        while (true) {
            int read = reader.read();
            if (read < 0) throw new IOException("End of stream");
            char c = (char) read;
            if (c == token) {
                break;
            }
            builder.append(c);
        }
        return builder.toString();
    }
}
