package io.github.defective4.dsp.vcd4j.data;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeScale {

    public static enum TimeUnit {
        MICROSECOND("us", java.util.concurrent.TimeUnit.MICROSECONDS),
        MILLISECOND("ms", java.util.concurrent.TimeUnit.MILLISECONDS),
        NANOSECOND("ns", java.util.concurrent.TimeUnit.NANOSECONDS),
        PICOSECOND("ps", null),
        SECOND("s", java.util.concurrent.TimeUnit.SECONDS);

        private static final Pattern PATTERN;
        private final String name;
        private final java.util.concurrent.TimeUnit unit;

        static {
            TimeUnit[] vals = TimeUnit.values();
            String[] names = new String[vals.length];
            for (int i = 0; i < names.length; i++) names[i] = vals[i].name;
            PATTERN = Pattern.compile("[" + String.join(",", names) + "]");
        }

        private TimeUnit(String name, java.util.concurrent.TimeUnit unit) {
            this.name = name;
            this.unit = unit;
        }

        public String getName() {
            return name;
        }

        public java.util.concurrent.TimeUnit getTimeUnit() {
            return unit;
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
        Objects.requireNonNull(unit);
        if (value < 1) throw new IllegalArgumentException("TimeScale value < 1");
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
