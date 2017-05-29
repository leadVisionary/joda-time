package org.joda.time.format.parsing;

import org.joda.time.MutableDateTime;
import org.joda.time.format.DateTimeFormatter;

public final class MutableDateTimeParsingStrategy extends FormatterParsingStrategy<MutableDateTime> {

    public MutableDateTimeParsingStrategy(final DateTimeFormatter formatter) {
        super(formatter);
    }

    public MutableDateTime convertToOutputType(long computedBucketResult) {
        MutableDateTime dt = new MutableDateTime(computedBucketResult, bucket.getBucketChronology(formatter.isOffsetParsed()));
        if (formatter.getZone() != null) {
            dt.setZone(formatter.getZone());
        }
        return dt;
    }
}
