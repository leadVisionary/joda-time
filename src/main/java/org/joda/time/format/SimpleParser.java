package org.joda.time.format;

import org.joda.time.*;

import java.util.concurrent.Callable;

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

    static LocalDateTime parseLocalDateTime(final CharSequence text,
                                            final InternalParser parser,
                                            final DateTimeParserBucket bucket) {
        final Callable<LocalDateTime> callback = new Callable<LocalDateTime>() {
            public LocalDateTime call() throws Exception {
                return getLocalDateTime(text, bucket);
            }
        };
        return getResult(text.toString(), parser, bucket, callback);
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

    static DateTime parseDateTime(final boolean iOffsetParsed,
                                  final DateTimeZone iZone,
                                  final String text,
                                  final InternalParser parser,
                                  final DateTimeParserBucket bucket) {
        final Callable<DateTime> callback = new Callable<DateTime>() {
            public DateTime call() throws Exception {
                DateTime dt = new DateTime(bucket.computeMillis(true, text), bucket.getBucketChronology(iOffsetParsed));
                if (iZone != null) {
                    dt = dt.withZone(iZone);
                }
                return dt;
            }
        };
        return getResult(text, parser, bucket, callback);
    }

    static MutableDateTime parseMutableDateTime(final boolean iOffsetParsed, final DateTimeZone iZone, final String text, InternalParser parser, final DateTimeParserBucket bucket) {
        final Callable<MutableDateTime> callback = new Callable<MutableDateTime>() {
            public MutableDateTime call() throws Exception {
                MutableDateTime dt = new MutableDateTime(bucket.computeMillis(true, text), bucket.getBucketChronology(iOffsetParsed));
                if (iZone != null) {
                    dt.setZone(iZone);
                }
                return dt;
            }
        };

        return getResult(text, parser, bucket, callback);
    }

    private static <T> T getResult(String text, InternalParser parser, DateTimeParserBucket bucket, Callable<T> callable) {
        if (parser == null) {
            throw new UnsupportedOperationException("Parsing not supported");
        }
        int newPos = parser.parseInto(bucket, text, 0);
        if (newPos >= 0) {
            if (newPos >= text.length()) {
                try {
                    return callable.call();
                } catch (Exception e) {

                }
            }
        } else {
            newPos = ~newPos;
        }
        throw new IllegalArgumentException(FormatUtils.createErrorMessage(text, newPos));
    }

}
