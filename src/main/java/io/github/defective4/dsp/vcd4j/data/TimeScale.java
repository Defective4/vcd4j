package io.github.defective4.dsp.vcd4j.data;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeScale {

    public static enum TimeScaleUnit {
        MICROSECOND("us", TimeUnit.MICROSECONDS, 0.000001),
        MILLISECOND("ms", TimeUnit.MILLISECONDS, 0.001),
        NANOSECOND("ns", TimeUnit.NANOSECONDS, 0.000000001),
        PICOSECOND("ps", null, 0.000000000001),
        SECOND("s", TimeUnit.SECONDS, 1d);

        private static final Pattern PATTERN;
        private final String name;
        private final double nth;
        private final java.util.concurrent.TimeUnit unit;

        static {
            TimeScaleUnit[] vals = TimeScaleUnit.values();
            String[] names = new String[vals.length];
            for (int i = 0; i < names.length; i++) names[i] = vals[i].name;
            PATTERN = Pattern.compile("[" + String.join(",", names) + "]");
        }

        private TimeScaleUnit(String name, java.util.concurrent.TimeUnit unit, double nth) {
            this.name = name;
            this.unit = unit;
            this.nth = nth;
        }

        public String getName() {
            return name;
        }

        public double getNth() {
            return nth;
        }

        public java.util.concurrent.TimeUnit getTimeUnit() {
            return unit;
        }

        public static TimeScaleUnit parseTimeUnit(String timescale) {
            Matcher matcher = PATTERN.matcher(timescale.toLowerCase());
            if (!matcher.find()) return NANOSECOND;
            String unitName = timescale.substring(matcher.start());
            for (TimeScaleUnit unit : values()) if (unit.name.equalsIgnoreCase(unitName)) return unit;
            return NANOSECOND;
        }
    }

    private final TimeScaleUnit unit;

    private final long value;

    public TimeScale(TimeScaleUnit unit, long value) {
        Objects.requireNonNull(unit);
        if (value < 1) throw new IllegalArgumentException("TimeScale value < 1");
        this.unit = unit;
        this.value = value;
    }

    public TimeScaleUnit getUnit() {
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
