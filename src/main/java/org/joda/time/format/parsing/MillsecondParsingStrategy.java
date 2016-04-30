package org.joda.time.format.parsing;

import org.joda.time.format.DateTimeFormatter;

public final class MillsecondParsingStrategy extends FormatterParsingStrategy<Long> {

    public MillsecondParsingStrategy(final DateTimeFormatter formatter) {
        super(formatter);
    }

    protected Long doParse(final CharSequence text) {
        return bucket.computeMillis(true, text);
    }
}
