package org.joda.time.format.parsing;

import org.joda.time.MutableDateTime;
import org.joda.time.format.DateTimeFormatter;

public final class MutableDateTimeParsingStrategy extends FormatterParsingStrategy<MutableDateTime> {

    public MutableDateTimeParsingStrategy(final DateTimeFormatter formatter) {
        super(formatter);
    }

    public MutableDateTime doParse(final CharSequence text) {
        MutableDateTime dt = new MutableDateTime(bucket.computeMillis(true, text), bucket.getBucketChronology(formatter.isOffsetParsed()));
        if (formatter.getZone() != null) {
            dt.setZone(formatter.getZone());
        }
        return dt;
    }
}
