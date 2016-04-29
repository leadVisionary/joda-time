package org.joda.time.format;

import org.joda.time.Chronology;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;

final class LocalDateTimeParsingStrategy extends FormatterParsingStrategy<LocalDateTime> {

    LocalDateTimeParsingStrategy(DateTimeFormatter formatter) {
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
