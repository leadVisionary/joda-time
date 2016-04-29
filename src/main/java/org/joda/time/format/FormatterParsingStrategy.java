package org.joda.time.format;

import org.joda.time.Chronology;

abstract class FormatterParsingStrategy<T> implements ParsingStrategy<T> {
    protected final DateTimeFormatter formatter;
    protected final DateTimeParserBucket bucket;

    FormatterParsingStrategy(final DateTimeFormatter formatter) {
        this.formatter = formatter;
        Chronology chrono = ChronologyFactory.selectChronology(formatter.getChronology(), formatter.getZone(), formatter.getChronology());
        this.bucket = new DateTimeParserBucket((long) 0, chrono, formatter.getLocale(), formatter.getPivotYear(), formatter.getDefaultYear());
    }

    public T parse(CharSequence text) {
        if (formatter.getParser0() == null) {
            throw new UnsupportedOperationException("Parsing not supported");
        }
        int newPos = formatter.getParser0().parseInto(bucket, text, 0);
        if (newPos >= 0 && newPos >= text.length()) {
            return doParse(text);
        } else {
            throw new IllegalArgumentException(FormatUtils.createErrorMessage(text.toString(), ~newPos));
        }
    }

    protected abstract T doParse(CharSequence text);
}
