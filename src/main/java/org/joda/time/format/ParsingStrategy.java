package org.joda.time.format;

public interface ParsingStrategy<T> {
    T parse(CharSequence text);
}
