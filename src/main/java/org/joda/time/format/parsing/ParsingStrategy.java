package org.joda.time.format.parsing;

public interface ParsingStrategy<T> {
    T parse(CharSequence text);
}
