package examples;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Map.Entry;

import io.github.defective4.dsp.vcd4j.data.BinaryChangeEntry;
import io.github.defective4.dsp.vcd4j.data.ChangeEntry;
import io.github.defective4.dsp.vcd4j.data.MultibitChangeEntry;
import io.github.defective4.dsp.vcd4j.data.TimeScale;
import io.github.defective4.dsp.vcd4j.data.VCD;
import io.github.defective4.dsp.vcd4j.data.VariableDefinition;
import io.github.defective4.dsp.vcd4j.file.VCDParser;

public class VCDParserExample {
    public static void main(String[] args) {
        try (Reader reader = new InputStreamReader(
                VCDParserExample.class.getResourceAsStream("/examples/example.vcd"))) {
            VCD vcd = VCDParser.parse(reader);

            // Print header info
            System.out.println("Version: " + vcd.getVersion());
            System.out.println("Date: " + vcd.getDate());
            System.out.println("Comment: " + vcd.getComment());
            TimeScale timeScale = vcd.getTimeScale();
            System.out
                    .println("Time scale: " + timeScale.getResolution() + " "
                            + timeScale.getUnit().name().toLowerCase());

            System.out.println();
            System.out.println("Variables:");

            // Print variable definitions
            for (Entry<String, VariableDefinition> obj : vcd.getVariableDefinitions().entrySet()) {
                VariableDefinition var = obj.getValue();
                System.out
                        .println(String
                                .format("%s-bit variable named \"%s\" (key %s)", var.getBitCount(), var.getName(),
                                        var.getIdentifier()));
            }

            System.out.println();
            System.out.println("Value changes:");

            // Print value changes
            for (Entry<Long, List<ChangeEntry<?>>> entry : vcd.getValueChanges().entrySet()) {
                List<ChangeEntry<?>> changes = entry.getValue();
                for (ChangeEntry<?> change : changes) {
                    String value;
                    if (change instanceof BinaryChangeEntry bin) {
                        value = bin.getValue().name();
                    } else if (change instanceof MultibitChangeEntry multibit) {
                        if (multibit.isUndefined()) {
                            value = "Undefined";
                        } else {
                            value = "0x" + Integer.toHexString(multibit.getValue());
                        }
                    } else throw new IllegalStateException("Unknown change entry class: " + change.getClass());
                    System.out
                            .println(String
                                    .format("Variable \"%s\" changed to %s until %s", change.getVariable().getName(),
                                            value, entry.getKey()));
                }
                System.out.println();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
