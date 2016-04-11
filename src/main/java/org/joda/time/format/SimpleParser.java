package org.joda.time.format;

import org.joda.time.*;

import java.util.Locale;

final class SimpleParser {

    static int parseIntoReadWriteableInstant(Chronology iChrono, Locale iLocale, boolean iOffsetParsed, Integer iPivotYear, DateTimeZone iZone, ReadWritableInstant instant, String text, int position, InternalParser parser) {
        if (instant == null) {
            throw new IllegalArgumentException("Instant must not be null");
        }
        Chronology chrono = ChronologyFactory.selectChronology(iChrono, iZone, instant.getChronology());
        long millis = instant.getMillis() + instant.getChronology().getZone().getOffset(instant.getMillis());
        int defaultYear = DateTimeUtils.getChronology(instant.getChronology()).year().get(instant.getMillis());
        DateTimeParserBucket bucket = new DateTimeParserBucket(millis, chrono, iLocale, iPivotYear, defaultYear);
        int newPos = requireParser(parser).parseInto(bucket, text, position);
        instant.update(iZone, bucket.computeMillis(false, text), ChronologyFactory.getChronology(iOffsetParsed, bucket.getChronology(), bucket.getOffsetInteger(), bucket.getZone()));
        return newPos;
    }

    static long parseMillis(Chronology iChrono, int iDefaultYear, Locale iLocale, Integer iPivotYear, DateTimeZone iZone, String text, InternalParser parser) {
        Chronology chrono = ChronologyFactory.selectChronology(iChrono, iZone, iChrono);
        DateTimeParserBucket bucket = new DateTimeParserBucket(0, chrono, iLocale, iPivotYear, iDefaultYear);
        return bucket.doParseMillis(parser, text);
    }

    static LocalDateTime parseLocalDateTime(Chronology iChrono, int iDefaultYear, Locale iLocale, Integer iPivotYear, DateTimeZone iZone, String text, InternalParser parser) {
        Chronology chronology = ChronologyFactory.selectChronology(iChrono, iZone, null);
        DateTimeParserBucket bucket = new DateTimeParserBucket(0, chronology.withUTC(), iLocale, iPivotYear, iDefaultYear);
        return getLocalDateTime(bucket, text, parser, chronology.withUTC());
    }

    static DateTime parseDateTime(Chronology iChrono, int iDefaultYear, Locale iLocale, boolean iOffsetParsed, Integer iPivotYear, DateTimeZone iZone, String text, InternalParser parser) {
        Chronology chrono = ChronologyFactory.selectChronology(iChrono, iZone, null);
        DateTimeParserBucket bucket = new DateTimeParserBucket(0, chrono, iLocale, iPivotYear, iDefaultYear);
        return getDateTime(bucket, iOffsetParsed, iZone, text, parser, chrono);
    }

    static MutableDateTime parseMutableDateTime(Chronology iChrono, int iDefaultYear, Locale iLocale, boolean iOffsetParsed, Integer iPivotYear, DateTimeZone iZone, String text, InternalParser parser) {
        Chronology chrono = ChronologyFactory.selectChronology(iChrono, iZone, null);
        DateTimeParserBucket bucket = new DateTimeParserBucket(0, chrono, iLocale, iPivotYear, iDefaultYear);
        return getMutableDateTime(bucket, iOffsetParsed, iZone, text, parser, chrono);
    }

    /**
     * Checks whether parsing is supported.
     *
     * @param iParser
     * @throws UnsupportedOperationException if parsing is not supported
     */
    static InternalParser requireParser(InternalParser iParser) {
        InternalParser parser = iParser;
        if (parser == null) {
            throw new UnsupportedOperationException("Parsing not supported");
        }
        return parser;
    }

    static LocalDateTime getLocalDateTime(DateTimeParserBucket bucket, String text, InternalParser parser, Chronology chrono) {

        int newPos = requireParser(parser).parseInto(bucket, text, 0);
        if (newPos >= 0) {
            if (newPos >= text.length()) {
                return getLocalDateTime(bucket, text, chrono);
            }
        } else {
            newPos = ~newPos;
        }
        throw new IllegalArgumentException(FormatUtils.createErrorMessage(text, newPos));
    }

    static LocalDateTime getLocalDateTime(DateTimeParserBucket bucket, String text, Chronology chrono) {
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

    static MutableDateTime getMutableDateTime(DateTimeParserBucket bucket, boolean iOffsetParsed, DateTimeZone iZone, String text, InternalParser parser, Chronology chrono) {

        int newPos = requireParser(parser).parseInto(bucket, text, 0);
        if (newPos >= 0) {
            if (newPos >= text.length()) {
                return getMutableDateTime(bucket, iOffsetParsed, iZone, text, chrono);
            }
        } else {
            newPos = ~newPos;
        }
        throw new IllegalArgumentException(FormatUtils.createErrorMessage(text, newPos));
    }

    static MutableDateTime getMutableDateTime(DateTimeParserBucket bucket, boolean iOffsetParsed, DateTimeZone iZone, String text, Chronology chrono) {
        return getMutableDateTime(iZone, bucket.computeMillis(true, text), ChronologyFactory.getChronology(iOffsetParsed, chrono, bucket.getOffsetInteger(), bucket.getZone()));
    }

    static MutableDateTime getMutableDateTime(DateTimeZone iZone, long l, Chronology chronology) {
        MutableDateTime dt = new MutableDateTime(l, chronology);
        if (iZone != null) {
            dt.setZone(iZone);
        }
        return dt;
    }

    static DateTime getDateTime(DateTimeParserBucket bucket, boolean iOffsetParsed, DateTimeZone iZone, String text, final InternalParser parser, Chronology chrono) {
        int newPos = requireParser(parser).parseInto(bucket, text, 0);
        if (newPos >= 0) {
            if (newPos >= text.length()) {
                return getDateTime(iZone, ChronologyFactory.getChronology(iOffsetParsed, chrono, bucket.getOffsetInteger(), bucket.getZone()), bucket.computeMillis(true, text));
            }
        } else {
            newPos = ~newPos;
        }
        throw new IllegalArgumentException(FormatUtils.createErrorMessage(text, newPos));
    }

    static DateTime getDateTime(DateTimeZone iZone, Chronology chronology, long millis) {
        DateTime dt = new DateTime(millis, chronology);
        if (iZone != null) {
            dt = dt.withZone(iZone);
        }
        return dt;
    }
}
