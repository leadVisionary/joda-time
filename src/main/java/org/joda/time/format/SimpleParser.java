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

    static LocalDateTime parseLocalDateTime(final CharSequence text,
                                            final InternalParser parser,
                                            final DateTimeParserBucket bucket,
                                            final Callable<LocalDateTime> callback) {
        return getResult(text.toString(), parser, bucket, callback);
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
}
