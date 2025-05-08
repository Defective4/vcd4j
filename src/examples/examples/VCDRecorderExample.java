package examples;

import java.io.File;

import io.github.defective4.dsp.vcd4j.data.State;
import io.github.defective4.dsp.vcd4j.data.TimeScale;
import io.github.defective4.dsp.vcd4j.data.TimeScale.TimeScaleUnit;
import io.github.defective4.dsp.vcd4j.data.VCD;
import io.github.defective4.dsp.vcd4j.data.VariableDefinition;
import io.github.defective4.dsp.vcd4j.file.VCDWriter;
import io.github.defective4.dsp.vcd4j.recorder.VCDRecorder;

public class VCDRecorderExample {
    public static void main(String[] args) {
        try {
            VariableDefinition variable = new VariableDefinition(".", VariableDefinition.VarType.WIRE, 1, "logic");

            TimeScale timeScale = new TimeScale(TimeScaleUnit.MILLISECOND, 1);
            VCDRecorder recorder = new VCDRecorder.Builder(timeScale)
                    .setAccuracy(500) // Up to 500ms accuracy
                    .setAccuracyCeil(false) // Round time down (default and recommended)
                    .build();
            recorder.start(); // Start the recorder

            // Make the signal HIGH and LOW every 500 ms for 5 seconds
            System.err.println("Started recording for 5 seconds...");
            for (int i = 0; i < 5; i++) {
                recorder.insertBinaryChange(variable, State.HIGH);
                Thread.sleep(500);
                recorder.insertBinaryChange(variable, State.LOW);
                Thread.sleep(500);
            }
            recorder.stop(); // Stop the recorder
            System.err.println("Recording finished!");

            VCD vcd = recorder.getVCD();
            File outputFile = new File("recorded.vcd");
            VCDWriter.write(vcd, outputFile); // Save the VCD
            System.err.println("Result saved to " + outputFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
