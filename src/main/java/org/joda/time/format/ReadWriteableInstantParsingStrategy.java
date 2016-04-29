package org.joda.time.format;

import org.joda.time.Chronology;
import org.joda.time.DateTimeUtils;
import org.joda.time.ReadWritableInstant;

final class ReadWriteableInstantParsingStrategy implements ParsingStrategy<Integer> {
    private final DateTimeFormatter formatter;
    private final ReadWritableInstant instant;
    private final int position;

    ReadWriteableInstantParsingStrategy(DateTimeFormatter formatter, ReadWritableInstant instant, int position) {
        this.formatter = formatter;
        this.instant = instant;
        this.position = position;
    }

    public Integer parse(CharSequence text) {
        if (instant == null) {
            throw new IllegalArgumentException("Instant must not be null");
        }

        long millis = instant.getMillis() + instant.getChronology().getZone().getOffset(instant.getMillis());
        int defaultYear = DateTimeUtils.getChronology(instant.getChronology()).year().get(instant.getMillis());
        Chronology chrono = ChronologyFactory.selectChronology(formatter.getChronology(),
                formatter.getZone(), instant.getChronology());
        DateTimeParserBucket bucket = new DateTimeParserBucket(millis, chrono,
                formatter.getLocale(),
                formatter.getPivotYear(), defaultYear)
                ;
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
