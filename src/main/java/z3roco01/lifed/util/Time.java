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

    /**
     * create a string to represent the ticks in a human readable format
     * @param ticks amount of ticks
     * @return a pretty string
     */
    public static String prettyTicks(int ticks) {
        // amount of seconds passed, the base unit
        int seconds = (int)Math.round(((double)ticks) / 20.0);

        int minutes = Math.floorDiv((int)Math.floor(seconds), 60);
        seconds -= minutes*60;

        int hours = Math.floorDiv(minutes, 60);
        minutes -= hours*60;

        return hours + ":" + minutes + ":" + seconds;
    }
}
