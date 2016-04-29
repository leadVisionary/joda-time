package org.joda.time.format;

import org.joda.time.MutableDateTime;

final class MutableDateTimeParsingStrategy extends FormatterParsingStrategy<MutableDateTime> {

    MutableDateTimeParsingStrategy(DateTimeFormatter formatter) {
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
