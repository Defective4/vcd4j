package examples;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import io.github.defective4.dsp.vcd4j.data.ChangeEntry;
import io.github.defective4.dsp.vcd4j.data.VCD;
import io.github.defective4.dsp.vcd4j.file.VCDParser;
import io.github.defective4.dsp.vcd4j.player.PlayerAdapter;
import io.github.defective4.dsp.vcd4j.player.VCDPlayer;

public class VCDPlayerExample {
    public static void main(String[] args) {
        try (Reader reader = new InputStreamReader(
                VCDPlayerExample.class.getResourceAsStream("/examples/example.vcd"))) {
            // Parse the example VCD file
            VCD vcd = VCDParser.parse(reader);
            VCDPlayer player = new VCDPlayer(vcd);

            // Add a listener so we can actually listen to the value changes in real time
            player.addListener(new PlayerAdapter() {
                @Override
                public void playerStopped(long playerTime, long realTime) {
                    System.err.println("Playback stopped at " + realTime / 1000000 + "ms!");
                }

                @Override
                public void valuesChanged(List<ChangeEntry<?>> entries, long playerTime, long realTime) {
                    String msTime = realTime / 1000000 + "ms";
                    for (ChangeEntry<?> entry : entries) {
                        Object value = entry.getValue();
                        System.out
                                .println(String
                                        .format("Variable %s changed to %s at %s (%s of player time units)",
                                                entry.getVariable().getName(), value, msTime, playerTime));
                    }
                    System.out.println();
                }
            });

            // Start the player.
            // The application will exit when the player stops replaying the VCD
            player.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
