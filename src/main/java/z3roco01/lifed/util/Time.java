package z3roco01.lifed.util;

/**
 * Handles conversions between conventional time units and ticks
 */
public class Time {
    /**
     * How many ticks one unit takes
     */
    private final int ticksPerUnit;
    /**
     * For creating the converters
     * @param ticksPerUnit how many ticks one unit is equal
     */
    private Time(int ticksPerUnit) {
        this.ticksPerUnit = ticksPerUnit;
    }

    /**
     * converts the unit to ticks with at the stored rate
     * @param units how many units you want to convert
     * @return how many ticks it is equal to
     */
    public int ticks(int units) {
        return units * ticksPerUnit;
    }

    // objects for effectuating the conversion
    public static final Time SECONDS = new Time(20);    // 20 ticks = 1 second
    public static final Time MINUTES = new Time(1200);  // 1200 ticks = 1 minute
    public static final Time HOURS   = new Time(72000); // 72000 ticks = 1 hour
}
