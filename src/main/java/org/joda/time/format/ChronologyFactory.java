package org.joda.time.format;

import org.joda.time.Chronology;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;

public final class ChronologyFactory {
    private ChronologyFactory() {}
    /**
     * Determines the correct chronology to use.
     *
     * @param defaultChronology
     * @param defaultTZ         @param chrono  the proposed chronology
     * @return the actual chronology
     */
    public static Chronology selectChronology(Chronology defaultChronology, DateTimeZone defaultTZ, Chronology chrono) {
        return getChronologyWithTimeZone(getChronologyWithDefaultValue(defaultChronology, chrono), defaultTZ);
    }

    public static Chronology getChronologyWithDefaultValue(Chronology defaultChronology, Chronology chrono) {
        return (defaultChronology != null) ? defaultChronology : DateTimeUtils.getChronology(chrono);
    }

    public static Chronology getChronologyWithTimeZone(Chronology chrono, DateTimeZone defaultTimeZone) {
        return (defaultTimeZone != null) ? chrono.withZone(defaultTimeZone) : chrono;
    }
}