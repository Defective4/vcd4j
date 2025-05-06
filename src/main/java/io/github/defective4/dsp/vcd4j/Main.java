package io.github.defective4.dsp.vcd4j;

import java.io.File;
import java.util.List;
import java.util.Map.Entry;

import io.github.defective4.dsp.vcd4j.data.ChangeEntry;
import io.github.defective4.dsp.vcd4j.data.MultibitChangeEntry;
import io.github.defective4.dsp.vcd4j.data.VCD;
import io.github.defective4.dsp.vcd4j.file.VCDParser;

public class Main {
    public static void main(String[] args) {
        try {
            VCD vcd = VCDParser.parse(new File("/tmp/test.vcd"));
            for (Entry<Long, List<ChangeEntry<?>>> et : vcd.getValueChanges().entrySet()) {
                System.out.println(et.getKey());
                for (ChangeEntry<?> e : et.getValue()) System.out
                        .println(e.getVariable().getName() + ": "
                                + (e instanceof MultibitChangeEntry mb ? Integer.toHexString(mb.getValue())
                                        : e.getValue()));
                System.out.println();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
