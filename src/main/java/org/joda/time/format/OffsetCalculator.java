package org.joda.time.format;

final class OffsetCalculator {
    private final CharSequence text;
    private final int maxParsedDigits;
    private final boolean isSigned;
    private int position;
    private int length;
    private boolean negative;
    private int limit;
    private int value;

    OffsetCalculator(final CharSequence text,
                     final int iMaxParsedDigits,
                     final boolean iSigned,
                     final int position) {
        this.text = text;
        this.position = position;
        length = 0;
        negative = false;
        maxParsedDigits = iMaxParsedDigits;
        isSigned = iSigned;
    }

    int getPosition() {
        return position;
    }

    int getValue() {
        return value;
    }

    void calculate() {
        calculateLength();

        if (length == 0) {
            position = ~position;
        } else {
            updateValue();
        }
    }

    private void calculateLength() {
        limit = Math.min(maxParsedDigits, text.length() - position);
        while (length < limit && shouldContinue()) {
            updateBasedOnSign();
            length = length + 1;
        }
    }

    private boolean shouldContinue() {
        final boolean hasSign = isPrefixedWithPlusOrMinus() && isSigned;
        return isADigit(text.charAt(position + length)) || hasSign;
    }

    private static boolean isADigit(final char c) {
        return !isNotADigit(c);
    }

    private static boolean isNotADigit(final char c) {
        return c < '0' || c > '9';
    }

    private boolean isPrefixedWithPlusOrMinus() {
        final int index = position + length;
        final char currentCharacter = text.charAt(index);
        final boolean isFirstCharacterOperator = length == 0 && (currentCharacter == '-' || currentCharacter == '+');
        final boolean hasNextDigitCharacter = index < text.length() - 1 && isADigit(text.charAt(index + 1));
        return isFirstCharacterOperator && isBeforeBoundary() && hasNextDigitCharacter;
    }

    private boolean isBeforeBoundary() {
        return length + 1 <= limit;
    }

    private void updateBasedOnSign() {
        if (isPrefixedWithPlusOrMinus()) {
            negative = text.charAt(position + length) == '-';
            length = (negative) ? length + 1 : length;
            position = (negative) ? position : position + 1;
            // Expand the limit to disregard the sign character.
            limit = Math.min(limit + 1, text.length() - position);
        }
    }

    private void updateValue() {
        if (length >= 9) {
            // Since value may exceed integer limits, use stock parser
            // which checks for this.
            value = Integer.parseInt(text.subSequence(position, position += length).toString());
        } else {
            calculateValueForLengthBetween1And8();
        }
    }

    private void calculateValueForLengthBetween1And8() {
        int i = position;
        if (negative) {
            i++;
        }

        final int index = i++;
        if (index > text.length()) {
            position = ~position;
            return;
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
}
