package io.github.defective4.dsp.vcd4j.data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeScale {

    public static enum TimeUnit {
        MICROSECOND("us"), MILLISECOND("ms"), NANOSECOND("ns"), PICOSECOND("ps"), SECOND("s");

        private static final Pattern PATTERN;
        private final String name;

        static {
            TimeUnit[] vals = TimeUnit.values();
            String[] names = new String[vals.length];
            for (int i = 0; i < names.length; i++) names[i] = vals[i].name;
            PATTERN = Pattern.compile("[" + String.join(",", names) + "]");
        }

        private TimeUnit(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static TimeUnit parseTimeUnit(String timescale) {
            Matcher matcher = PATTERN.matcher(timescale.toLowerCase());
            if (!matcher.find()) return NANOSECOND;
            String unitName = timescale.substring(matcher.start());
            for (TimeUnit unit : values()) if (unit.name.equalsIgnoreCase(unitName)) return unit;
            return NANOSECOND;
        }
    }

    private final TimeUnit unit;

    private final long value;

    public TimeScale(TimeUnit unit, long value) {
        this.unit = unit;
        this.value = value;
    }

    public TimeUnit getUnit() {
        return unit;
    }

    public long getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "VCDTimeScale [unit=" + unit + ", value=" + value + "]";
    }

}
