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

    static LocalDateTime parseLocalDateTime(CharSequence text, InternalParser parser, DateTimeParserBucket bucket) {
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

    static DateTime parseDateTime(boolean iOffsetParsed, DateTimeZone iZone, String text, InternalParser parser, DateTimeParserBucket bucket) {
        if (parser == null) {
            throw new UnsupportedOperationException("Parsing not supported");
        }
        int newPos = parser.parseInto(bucket, text, 0);
        if (newPos >= 0) {
            if (newPos >= text.length()) {
                DateTime dt = new DateTime(bucket.computeMillis(true, text), bucket.getBucketChronology(iOffsetParsed));
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

    static MutableDateTime parseMutableDateTime(boolean iOffsetParsed, DateTimeZone iZone, String text, InternalParser parser, DateTimeParserBucket bucket) {

        if (parser == null) {
            throw new UnsupportedOperationException("Parsing not supported");
        }
        int newPos = parser.parseInto(bucket, text, 0);
        if (newPos >= 0) {
            if (newPos >= text.length()) {
                MutableDateTime dt = new MutableDateTime(bucket.computeMillis(true, text), bucket.getBucketChronology(iOffsetParsed));
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
