package org.joda.time.format;

import org.joda.time.DateTimeFieldType;

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
        int limit = Math.min(iMaxParsedDigits, text.length() - position);

        boolean negative = false;
        int length = 0;
        while (length < limit) {
            final int index = position + length;
            char c = text.charAt(index);
            final boolean isPlusOrMinus = c == '-' || c == '+';
            final boolean isFirstCharacterOperator = length == 0 && isPlusOrMinus;
            final boolean b = !isPastBoundary(limit, length) && !isNotADigit(text.charAt(index + 1));
            final boolean b1 = isFirstCharacterOperator && iSigned && b;
            if (!b1 && isNotADigit(c)) {
                break;
            }
            if (b1) {
                negative = c == '-';

                length = (negative) ? length + 1 : length;
                position = (negative) ? position : position + 1;
                // Expand the limit to disregard the sign character.
                limit = Math.min(limit + 1, text.length() - position);
            }
            length = length + 1;
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

    private static boolean isPastBoundary(final int limit, final int length) {
        return length + 1 >= limit;
    }

    private static boolean isNotADigit(final char c) {
        return c < '0' || c > '9';
    }
}
