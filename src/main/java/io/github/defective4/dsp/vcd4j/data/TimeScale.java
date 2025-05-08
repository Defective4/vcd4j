package io.github.defective4.dsp.vcd4j.data;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents time scale of a VCD object. <br>
 *
 * This class encapsulates information about the unit and resolution of the time
 * scale.
 */
public class TimeScale {

    /**
     * Unit of time used by VCD time scale
     */
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

        /**
         * Returns a {@link TimeUnit} counterpart of this enum.<br>
         * Since picoseconds aren't supported in Java, this method will return
         * <code>null</code> for {@link #PICOSECOND}
         *
         * @return Java's {@link TimeUnit} counterpart or <code>null</code> for
         *         {@link #PICOSECOND}
         */
        public java.util.concurrent.TimeUnit getTimeUnit() {
            return unit;
        }

        /**
         * Extracts time unit from <code>timescale</code> string used by VCDs
         *
         * @param  timescale time scale string to parse
         * @return           extracted time unit. Uses {@link #NANOSECOND} as fallback
         *                   if the unit wasn't recognized
         */
        public static TimeScaleUnit parseTimeUnit(String timescale) {
            Matcher matcher = PATTERN.matcher(timescale.toLowerCase());
            if (!matcher.find()) return NANOSECOND;
            String unitName = timescale.substring(matcher.start());
            for (TimeScaleUnit unit : values()) if (unit.name.equalsIgnoreCase(unitName)) return unit;
            return NANOSECOND;
        }
    }

    private final long resolution;

    private final TimeScaleUnit unit;

    /**
     * Constructs a new time scale for use in VCD objects
     *
     * @param  unit                     a non-null time scale unit
     * @param  resolution               a positive <code>long</code> representing
     *                                  this time scale's resolution
     *
     * @throws IllegalArgumentException if resolution is less than 1
     */
    public TimeScale(TimeScaleUnit unit, long resolution) {
        Objects.requireNonNull(unit);
        if (resolution < 1) throw new IllegalArgumentException("TimeScale resolution < 1");
        this.unit = unit;
        this.resolution = resolution;
    }

    public long getResolution() {
        return resolution;
    }

    public TimeScaleUnit getUnit() {
        return unit;
    }

    @Override
    public String toString() {
        return "VCDTimeScale [unit=" + unit + ", value=" + resolution + "]";
    }

}
