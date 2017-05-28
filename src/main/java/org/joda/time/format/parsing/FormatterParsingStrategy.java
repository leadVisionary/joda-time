package org.joda.time.format.parsing;

import org.joda.time.Chronology;
import org.joda.time.format.*;

abstract class FormatterParsingStrategy<T> implements ParsingStrategy<T> {
    protected final DateTimeFormatter formatter;
    protected final DateTimeParserBucket bucket;

    FormatterParsingStrategy(final DateTimeFormatter formatter) {
        this.formatter = formatter;
        Chronology chrono = Chronology.selectChronology(formatter.getChronology(), formatter.getZone(), formatter.getChronology());
        this.bucket = new DateTimeParserBucket((long) 0, chrono, formatter.getLocale(), formatter.getPivotYear(), formatter.getDefaultYear());
    }

    public T parse(final CharSequence text) {
        final DateTimeParser parser = formatter.getParser();
        if (parser == null) {
            throw new UnsupportedOperationException("Parsing not supported");
        }
        int newPos = parser.parseInto(bucket, text.toString(), 0);
        if (newPos >= 0 && newPos >= text.length()) {
            return convertToOutputType(bucket.computeMillis(true, text));
        } else {
            throw new IllegalArgumentException(FormatUtils.createErrorMessage(text.toString(), ~newPos));
        }
    }

    protected abstract T convertToOutputType(long computedBucketResult);
}
