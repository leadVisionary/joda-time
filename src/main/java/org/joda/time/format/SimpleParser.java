package org.joda.time.format;

import org.joda.time.*;

final class SimpleParser {

    static int parseIntoReadWriteableInstant(boolean iOffsetParsed, DateTimeZone iZone, ReadWritableInstant instant, String text, int position, InternalParser parser, DateTimeParserBucket bucket) {
        if (parser == null) {
            throw new UnsupportedOperationException("Parsing not supported");
        }
        int newPos = parser.parseInto(bucket, text, position);
        instant.update(iZone, bucket.computeMillis(false, text), bucket.getBucketChronology(iOffsetParsed));
        return newPos;
    }

    static long parseMillis(CharSequence text, InternalParser parser, DateTimeParserBucket bucket) {
        if (parser == null) {
            throw new UnsupportedOperationException("Parsing not supported");
        }
        int newPos = parser.parseInto(bucket, text, 0);
        if (newPos >= 0) {
            if (newPos >= text.length()) {
                return getMillis(text, bucket);
            }
        } else {
            newPos = ~newPos;
        }
        throw new IllegalArgumentException(FormatUtils.createErrorMessage(text.toString(), newPos));
    }

    private static long getMillis(CharSequence text, DateTimeParserBucket bucket) {
        return bucket.computeMillis(true, text);
    }

    static LocalDateTime parseLocalDateTime(CharSequence text, InternalParser parser, DateTimeParserBucket bucket) {
        if (parser == null) {
            throw new UnsupportedOperationException("Parsing not supported");
        }

        int newPos = parser.parseInto(bucket, text, 0);
        if (newPos >= 0) {
            if (newPos >= text.length()) {
                return getLocalDateTime(text, bucket);
            }
        } else {
            newPos = ~newPos;
        }
        throw new IllegalArgumentException(FormatUtils.createErrorMessage(text.toString(), newPos));
    }

    private static LocalDateTime getLocalDateTime(CharSequence text, DateTimeParserBucket bucket) {
        long millis = getMillis(text, bucket);
        Chronology chrono = bucket.getChronology();
        if (bucket.getOffsetInteger() != null) {  // treat withOffsetParsed() as being true
            int parsedOffset = bucket.getOffsetInteger();
            DateTimeZone parsedZone = DateTimeZone.forOffsetMillis(parsedOffset);
            chrono = chrono.withZone(parsedZone);
        } else if (bucket.getZone() != null) {
            chrono = chrono.withZone(bucket.getZone());
        }
        return new LocalDateTime(millis, chrono);
    }

    static DateTime parseDateTime(boolean iOffsetParsed, DateTimeZone iZone, String text, InternalParser parser, DateTimeParserBucket bucket) {
        if (parser == null) {
            throw new UnsupportedOperationException("Parsing not supported");
        }
        int newPos = parser.parseInto(bucket, text, 0);
        if (newPos >= 0) {
            if (newPos >= text.length()) {
                return getDateTime(iOffsetParsed, iZone, text, bucket);
            }
        } else {
            newPos = ~newPos;
        }
        throw new IllegalArgumentException(FormatUtils.createErrorMessage(text, newPos));
    }

    private static DateTime getDateTime(boolean iOffsetParsed, DateTimeZone iZone, String text, DateTimeParserBucket bucket) {
        DateTime dt = new DateTime(bucket.computeMillis(true, text), bucket.getBucketChronology(iOffsetParsed));
        if (iZone != null) {
            dt = dt.withZone(iZone);
        }
        return dt;
    }

    static MutableDateTime parseMutableDateTime(boolean iOffsetParsed, DateTimeZone iZone, String text, InternalParser parser, DateTimeParserBucket bucket) {

        if (parser == null) {
            throw new UnsupportedOperationException("Parsing not supported");
        }
        int newPos = parser.parseInto(bucket, text, 0);
        if (newPos >= 0) {
            if (newPos >= text.length()) {
                return getMutableDateTime(iOffsetParsed, iZone, text, bucket);
            }
        } else {
            newPos = ~newPos;
        }
        throw new IllegalArgumentException(FormatUtils.createErrorMessage(text, newPos));
    }

    private static MutableDateTime getMutableDateTime(boolean iOffsetParsed, DateTimeZone iZone, String text, DateTimeParserBucket bucket) {
        MutableDateTime dt = new MutableDateTime(bucket.computeMillis(true, text), bucket.getBucketChronology(iOffsetParsed));
        if (iZone != null) {
            dt.setZone(iZone);
        }
        return dt;
    }
}
