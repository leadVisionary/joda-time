package org.joda.time.format;

import org.joda.time.Chronology;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;

final class ChronologyFactory {
    private ChronologyFactory() {}
    /**
     * Determines the correct chronology to use.
     *
     * @param defaultChronology
     * @param defaultTZ         @param chrono  the proposed chronology
     * @return the actual chronology
     */
    static Chronology selectChronology(Chronology defaultChronology, DateTimeZone defaultTZ, Chronology chrono) {
        chrono = getChronologyWithDefaultValue(defaultChronology, chrono);
        chrono = getChronologyWithTimeZone(chrono, defaultTZ);
        return chrono;
    }

    static Chronology getChronologyWithDefaultValue(Chronology defaultChronology, Chronology chrono) {
        chrono = DateTimeUtils.getChronology(chrono);
        if (defaultChronology != null) {
            chrono = defaultChronology;
        }
        return chrono;
    }

    static Chronology getChronologyWithTimeZone(Chronology chrono, DateTimeZone defaultTimeZone) {
        if (defaultTimeZone != null) {
            chrono = chrono.withZone(defaultTimeZone);
        }
        return chrono;
    }
}