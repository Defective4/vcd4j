package io.github.defective4.dsp.vcd4j;

import java.io.File;

import io.github.defective4.dsp.vcd4j.data.VCD;
import io.github.defective4.dsp.vcd4j.file.VCDParser;

public class Main {
    public static void main(String[] args) {
        try {
            VCD vcd = VCDParser.parse(new File("test.vcd"));
            VCDPlayer player = new VCDPlayer(vcd);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
