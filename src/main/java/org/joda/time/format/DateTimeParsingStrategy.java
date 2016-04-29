package org.joda.time.format;

import org.joda.time.DateTime;

final class DateTimeParsingStrategy extends FormatterParsingStrategy<DateTime> {
    DateTimeParsingStrategy(DateTimeFormatter formatter) {
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
