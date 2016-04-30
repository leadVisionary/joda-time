package org.joda.time.format.parsing;

import org.joda.time.Chronology;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormatter;

public final class LocalDateTimeParsingStrategy extends FormatterParsingStrategy<LocalDateTime> {

    public LocalDateTimeParsingStrategy(final DateTimeFormatter formatter) {
        super(formatter);
    }

    protected LocalDateTime doParse(final CharSequence text) {
        long millis = bucket.computeMillis(true, text);
        Chronology chrono = bucket.getChronology();
        if (bucket.getOffsetInteger() != null) {  // treat withOffsetParsed() as being true
            int parsedOffset = bucket.getOffsetInteger();
            DateTimeZone parsedZone = DateTimeZone.forOffsetMillis(parsedOffset);
            chrono = chrono.withZone(parsedZone);
        } else if (bucket.getZone() != null) {
            chrono = chrono.withZone(bucket.getZone());
        }
        return new LocalDateTime(millis, chrono);
    }
}
