package org.joda.time.format.parsing;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

public final class DateTimeParsingStrategy extends FormatterParsingStrategy<DateTime> {
    public DateTimeParsingStrategy(final DateTimeFormatter formatter) {
        super(formatter);
    }

    protected DateTime convertToOutputType(long computedBucketResult) {
        DateTime dt = new DateTime(computedBucketResult, bucket.getBucketChronology(formatter.isOffsetParsed()));
        if (formatter.getZone() != null) {
            dt = dt.withZone(formatter.getZone());
        }
        return dt;
    }
}
