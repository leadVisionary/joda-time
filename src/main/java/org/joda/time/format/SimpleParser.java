package org.joda.time.format;

import org.joda.time.*;

import java.util.concurrent.Callable;

final class SimpleParser {

    static long parseMillisFrom(DateTimeFormatter dateTimeFormatter, final String text) {
        final DateTimeParserBucket bucket = DateTimeParserBucket.getDateTimeParserBucket(
                dateTimeFormatter.getChronology(),
                dateTimeFormatter.getDefaultYear(),
                dateTimeFormatter.getLocale(),
                dateTimeFormatter.getPivotYear(),
                dateTimeFormatter.getZone(), 0);
        final Callable<Long> callback = new Callable<Long>() {
            public Long call() throws Exception {
                return bucket.computeMillis(true, text);
            }
        };
        return getResult(text, dateTimeFormatter.getParser0(), bucket, callback);
    }

    static DateTime parseDateTime(final String text,
                                  final InternalParser parser,
                                  final DateTimeParserBucket bucket,
                                  final Callable<DateTime> callback) {
        return getResult(text, parser, bucket, callback);
    }

    static MutableDateTime parseMutableDateTime(final String text, InternalParser parser, final DateTimeParserBucket bucket, Callable<MutableDateTime> callback) {
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
                    if (e instanceof RuntimeException) {
                        throw (RuntimeException)e;
                    }
                }
            }
        } else {
            newPos = ~newPos;
        }
        throw new IllegalArgumentException(FormatUtils.createErrorMessage(text, newPos));
    }

    static LocalDateTime parseIntoLocalDateTime(DateTimeFormatter dateTimeFormatter, final String text) {
        final DateTimeParserBucket bucket = DateTimeParserBucket.getDateTimeParserBucket(
                dateTimeFormatter.getChronology(),
                dateTimeFormatter.getDefaultYear(),
                dateTimeFormatter.getLocale(),
                dateTimeFormatter.getPivotYear(),
                dateTimeFormatter.getZone(), 0);
        final Callable<LocalDateTime> callback = new Callable<LocalDateTime>() {
            public LocalDateTime call() throws Exception {
                long millis = bucket.computeMillis(true, text);
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
        };
        return getResult(text, dateTimeFormatter.getParser0(), bucket, callback);
    }
}
