package org.joda.time.format;

import org.joda.time.DateTimeFieldType;
import org.joda.time.format.DateTimeParserBucket;

final class FastNumberParser {
    private final DateTimeFieldType iFieldType;
    private final int iMaxParsedDigits;
    private final boolean iSigned;

    public FastNumberParser(final int maxParsedDigits,
                            final boolean signed,
                            final DateTimeFieldType fieldType) {
        iFieldType = fieldType;
        iMaxParsedDigits = maxParsedDigits;
        iSigned = signed;
    }

    public int parse(final DateTimeParserBucket bucket,
                     final CharSequence text,
                     int position) {
        final int remainingCharacters = text.length() - position;
        int limit = Math.min(iMaxParsedDigits, remainingCharacters);

        boolean negative = false;
        int length = 0;
        while (length < limit) {
            char c = text.charAt(position + length);
            final boolean isFirstCharacterOperator = length == 0 && (c == '-' || c == '+');
            if (isFirstCharacterOperator && iSigned) {
                negative = c == '-';

                if (isPastBoundaryOrNotDigit(text, position, limit, length)) {
                    break;
                }

                if (negative) {
                    length++;
                } else {
                    // Skip the '+' for parseInt to succeed.
                    position++;
                }
                // Expand the limit to disregard the sign character.
                limit = Math.min(limit + 1, remainingCharacters);
                continue;
            }
            if (isNotADigit(c)) {
                break;
            }
            length++;
        }

        if (length == 0) {
            return ~position;
        }

        int value;
        if (length >= 9) {
            // Since value may exceed integer limits, use stock parser
            // which checks for this.
            value = Integer.parseInt(text.subSequence(position, position += length).toString());
        } else {
            int i = position;
            if (negative) {
                i++;
            }

            final int index = i++;
            if (index > text.length()) {
                return ~position;
            }
            value = text.charAt(index) - '0';
            position += length;
            while (i < position) {
                value = ((value << 3) + (value << 1)) + text.charAt(i++) - '0';
            }
            if (negative) {
                value = -value;
            }
        }

        bucket.saveField(iFieldType, value);
        return position;
    }

    private static boolean isPastBoundaryOrNotDigit(final CharSequence text, final int position, final int limit, final int length) {
        final boolean isPastBoundary = length + 1 >= limit;
        if (isPastBoundary) {
            return true;
        }
        final char nextCharacter = text.charAt(position + length + 1);
        final boolean isNotDigit = isNotADigit(nextCharacter);
        return isNotDigit;
    }

    private static boolean isNotADigit(final char c) {
        return c < '0' || c > '9';
    }
}
