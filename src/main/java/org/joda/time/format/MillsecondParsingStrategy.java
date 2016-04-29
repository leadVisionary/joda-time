package org.joda.time.format;

final class MillsecondParsingStrategy extends FormatterParsingStrategy<Long> {

    MillsecondParsingStrategy(DateTimeFormatter formatter) {
        super(formatter);
    }

    protected Long doParse(final CharSequence text) {
        return bucket.computeMillis(true, text);
    }
}
