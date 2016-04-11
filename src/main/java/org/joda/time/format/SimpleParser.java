package org.joda.time.format;

import org.joda.time.*;

import java.util.Locale;

final class SimpleParser {

    static int parseIntoReadWriteableInstant(Chronology iChrono, Locale iLocale, boolean iOffsetParsed, Integer iPivotYear, DateTimeZone iZone, ReadWritableInstant instant, String text, int position, InternalParser parser) {
        DateTimeParserBucket bucket = getDateTimeParserBucket(iChrono, iLocale, iPivotYear, iZone, instant);
        if (parser == null) {
            throw new UnsupportedOperationException("Parsing not supported");
        }
        int newPos = parser.parseInto(bucket, text, position);
        instant.update(iZone, bucket.computeMillis(false, text), ChronologyFactory.getChronology(iOffsetParsed, bucket.getChronology(), bucket.getOffsetInteger(), bucket.getZone()));
        return newPos;
    }

    private static DateTimeParserBucket getDateTimeParserBucket(Chronology iChrono, Locale iLocale, Integer iPivotYear, DateTimeZone iZone, ReadWritableInstant instant) {
        if (instant == null) {
            throw new IllegalArgumentException("Instant must not be null");
        }
        Chronology chrono = ChronologyFactory.selectChronology(iChrono, iZone, instant.getChronology());
        long millis = instant.getMillis() + instant.getChronology().getZone().getOffset(instant.getMillis());
        int defaultYear = DateTimeUtils.getChronology(instant.getChronology()).year().get(instant.getMillis());
        return new DateTimeParserBucket(millis, chrono, iLocale, iPivotYear, defaultYear);
    }

    static long parseMillis(Chronology iChrono, int iDefaultYear, Locale iLocale, Integer iPivotYear, DateTimeZone iZone, CharSequence text, InternalParser parser) {
        DateTimeParserBucket bucket = getDateTimeParserBucket(iChrono, iDefaultYear, iLocale, iPivotYear, iZone);
        int newPos = parser.parseInto(bucket, text, 0);
        if (newPos >= 0) {
            if (newPos >= text.length()) {
                return bucket.computeMillis(true, text);
            }
        } else {
            newPos = ~newPos;
        }
        throw new IllegalArgumentException(FormatUtils.createErrorMessage(text.toString(), newPos));
    }

    private static DateTimeParserBucket getDateTimeParserBucket(Chronology iChrono, int iDefaultYear, Locale iLocale, Integer iPivotYear, DateTimeZone iZone) {
        Chronology chrono = ChronologyFactory.selectChronology(iChrono, iZone, iChrono);
        return new DateTimeParserBucket(0, chrono, iLocale, iPivotYear, iDefaultYear);
    }

    static LocalDateTime parseLocalDateTime(Chronology iChrono, int iDefaultYear, Locale iLocale, Integer iPivotYear, DateTimeZone iZone, CharSequence text, InternalParser parser) {
        Chronology chronology = ChronologyFactory.selectChronology(iChrono, iZone, null);
        DateTimeParserBucket bucket = new DateTimeParserBucket(0, chronology.withUTC(), iLocale, iPivotYear, iDefaultYear);
        Chronology chrono = bucket.getChronology();

        if (parser == null) {
            throw new UnsupportedOperationException("Parsing not supported");
        }
        int newPos = parser.parseInto(bucket, text, 0);
        if (newPos >= 0) {
            if (newPos >= text.length()) {
                long millis = bucket.computeMillis(true, text);
                if (bucket.getOffsetInteger() != null) {  // treat withOffsetParsed() as being true
                    int parsedOffset = bucket.getOffsetInteger();
                    DateTimeZone parsedZone = DateTimeZone.forOffsetMillis(parsedOffset);
                    chrono = chrono.withZone(parsedZone);
                } else if (bucket.getZone() != null) {
                    chrono = chrono.withZone(bucket.getZone());
                }
                return new LocalDateTime(millis, chrono);
            }
        } else {
            newPos = ~newPos;
        }
        throw new IllegalArgumentException(FormatUtils.createErrorMessage(text.toString(), newPos));
    }

    static DateTime parseDateTime(Chronology iChrono, int iDefaultYear, Locale iLocale, boolean iOffsetParsed, Integer iPivotYear, DateTimeZone iZone, String text, InternalParser parser) {
        Chronology chrono = ChronologyFactory.selectChronology(iChrono, iZone, null);
        DateTimeParserBucket bucket = new DateTimeParserBucket(0, chrono, iLocale, iPivotYear, iDefaultYear);
        if (parser == null) {
            throw new UnsupportedOperationException("Parsing not supported");
        }
        int newPos = parser.parseInto(bucket, text, 0);
        if (newPos >= 0) {
            if (newPos >= text.length()) {
                DateTime dt = new DateTime(bucket.computeMillis(true, text), ChronologyFactory.getChronology(iOffsetParsed, chrono, bucket.getOffsetInteger(), bucket.getZone()));
                if (iZone != null) {
                    dt = dt.withZone(iZone);
                }
                return dt;
            }
        } else {
            newPos = ~newPos;
        }
        throw new IllegalArgumentException(FormatUtils.createErrorMessage(text, newPos));
    }

    static MutableDateTime parseMutableDateTime(Chronology iChrono, int iDefaultYear, Locale iLocale, boolean iOffsetParsed, Integer iPivotYear, DateTimeZone iZone, String text, InternalParser parser) {
        Chronology chrono = ChronologyFactory.selectChronology(iChrono, iZone, null);
        DateTimeParserBucket bucket = new DateTimeParserBucket(0, chrono, iLocale, iPivotYear, iDefaultYear);

        if (parser == null) {
            throw new UnsupportedOperationException("Parsing not supported");
        }
        int newPos = parser.parseInto(bucket, text, 0);
        if (newPos >= 0) {
            if (newPos >= text.length()) {
                MutableDateTime dt = new MutableDateTime(bucket.computeMillis(true, text), ChronologyFactory.getChronology(iOffsetParsed, bucket.getChronology(), bucket.getOffsetInteger(), bucket.getZone()));
                if (iZone != null) {
                    dt.setZone(iZone);
                }
                return dt;
            }
        } else {
            newPos = ~newPos;
        }
        throw new IllegalArgumentException(FormatUtils.createErrorMessage(text, newPos));
    }
}
