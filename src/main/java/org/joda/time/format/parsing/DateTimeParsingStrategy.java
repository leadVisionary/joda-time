package org.joda.time.format.parsing;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

public final class DateTimeParsingStrategy extends FormatterParsingStrategy<DateTime> {
    public DateTimeParsingStrategy(final DateTimeFormatter formatter) {
        super(formatter);
    }

    protected DateTime doParse(final CharSequence text) {
        DateTime dt = new DateTime(bucket.computeMillis(true, text), bucket.getBucketChronology(formatter.isOffsetParsed()));
        if (formatter.getZone() != null) {
            dt = dt.withZone(formatter.getZone());
        }
        return dt;
    }
}
