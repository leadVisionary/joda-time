package org.joda.time.format;

import org.joda.time.*;

import java.util.Locale;
import java.util.concurrent.Callable;

final class SimpleParser {
    private final DateTimeFormatter formatter;
    private final DateTimeParserBucket bucket;

    SimpleParser(final DateTimeFormatter formatter) {
        this.formatter = formatter;
        this.bucket = getDateTimeParserBucket(
                formatter.getChronology(),
                formatter.getDefaultYear(),
                formatter.getLocale(),
                formatter.getPivotYear(),
                formatter.getZone(), 0);
    }

    long parseMillisFrom(final String text) {
        final Callable<Long> callback = new Callable<Long>() {
            public Long call() throws Exception {
                return bucket.computeMillis(true, text);
            }
        };
        return getResult(text, formatter.getParser0(), bucket, callback);
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

    LocalDateTime parseIntoLocalDateTime(final String text) {
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
        return getResult(text, formatter.getParser0(), bucket, callback);
    }

    DateTime getDateTime(final String text) {
        final Callable<DateTime> callback = new Callable<DateTime>() {
            public DateTime call() throws Exception {
                DateTime dt = new DateTime(bucket.computeMillis(true, text), bucket.getBucketChronology(formatter.isOffsetParsed()));
                if (formatter.getZone() != null) {
                    dt = dt.withZone(formatter.getZone());
                }
                return dt;
            }
        };
        return getResult(text, formatter.getParser0(), bucket, callback);
    }

    MutableDateTime getMutableDateTime(final String text) {
        final Callable<MutableDateTime> callback = new Callable<MutableDateTime>() {
            public MutableDateTime call() throws Exception {
                MutableDateTime dt = new MutableDateTime(bucket.computeMillis(true, text), bucket.getBucketChronology(formatter.isOffsetParsed()));
                if (formatter.getZone() != null) {
                    dt.setZone(formatter.getZone());
                }
                return dt;
            }
        };
        return getResult(text, formatter.getParser0(), bucket, callback);
    }

    private static DateTimeParserBucket getDateTimeParserBucket(Chronology iChrono, int iDefaultYear, Locale iLocale, Integer iPivotYear, DateTimeZone iZone, long millis) {
        Chronology chrono = ChronologyFactory.selectChronology(iChrono, iZone, iChrono);
        return new DateTimeParserBucket(millis, chrono, iLocale, iPivotYear, iDefaultYear);
    }
}
