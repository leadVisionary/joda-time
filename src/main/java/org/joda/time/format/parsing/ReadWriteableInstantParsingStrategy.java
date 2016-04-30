package org.joda.time.format.parsing;

import org.joda.time.Chronology;
import org.joda.time.DateTimeUtils;
import org.joda.time.ReadWritableInstant;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeParser;
import org.joda.time.format.DateTimeParserBucket;

public final class ReadWriteableInstantParsingStrategy implements ParsingStrategy<Integer> {
    private final ReadWritableInstant instant;
    private final int position;
    private final DateTimeFormatter formatter;
    private final DateTimeParserBucket bucket;

    public ReadWriteableInstantParsingStrategy(final DateTimeFormatter formatter,
                                               final ReadWritableInstant instant,
                                               final int position) {
        this.formatter = formatter;
        if (instant == null) {
            throw new IllegalArgumentException("Instant must not be null");
        }
        this.instant = instant;
        bucket = createBucket();
        this.position = position;
    }

    private DateTimeParserBucket createBucket() {
        long millis = instant.getMillis() + instant.getChronology().getZone().getOffset(instant.getMillis());
        int defaultYear = DateTimeUtils.getChronology(instant.getChronology()).year().get(instant.getMillis());
        Chronology chrono = Chronology.selectChronology(formatter.getChronology(),
                formatter.getZone(), instant.getChronology());
        return new DateTimeParserBucket(millis, chrono,
                formatter.getLocale(),
                formatter.getPivotYear(), defaultYear);
    }

    public Integer parse(CharSequence text) {
        DateTimeParser parser = formatter.getParser();
        if (parser == null) {
            throw new UnsupportedOperationException("Parsing not supported");
        }
        int newPos = parser.parseInto(bucket, text.toString(), position);
        instant.update(formatter.getZone(), bucket.computeMillis(false, text),
                bucket.getBucketChronology(formatter.isOffsetParsed()));
        return newPos;
    }
}
