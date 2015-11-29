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
        updatePositionAndValue();
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
        return Character.isDigit(text.charAt(position + length)) || hasSign;
    }

    private boolean isPrefixedWithPlusOrMinus() {
        final int index = position + length;
        final char currentCharacter = text.charAt(index);
        final boolean isFirstCharacterOperator = length == 0 && isCharacterOperator(currentCharacter);
        final boolean hasNextDigitCharacter = index < text.length() - 1 && Character.isDigit(text.charAt(index + 1));
        return isFirstCharacterOperator && isBeforeBoundary() && hasNextDigitCharacter;
    }

    private static boolean isCharacterOperator(final char currentCharacter) {
        return currentCharacter == '-' || currentCharacter == '+';
    }

    private boolean isBeforeBoundary() {
        return length + 1 <= limit;
    }

    private void updateBasedOnSign() {
        if (isPrefixedWithPlusOrMinus()) {
            negative = text.charAt(position + length) == '-';
            length = negative ? length + 1 : length;
            position = negative ? position : position + 1;
            // Expand the limit to disregard the sign character.
            limit = Math.min(limit + 1, text.length() - position);
        }
    }

    private void updatePositionAndValue() {
        if (length == 0) {
            position = ~position;
        } else if (length >= 9) {
            useDefaultParser();
        } else {
            useFastParser();
        }
    }

    private void useDefaultParser() {
        // Since value may exceed integer limits, use stock parser
        // which checks for this.
        final String toParse = text.subSequence(position, position + length).toString();
        value = Integer.parseInt(toParse);
        position += length;
    }

    private void useFastParser() {
        int i = negative ? position + 1 : position;

        final int index = i++;
        if (index < text.length()) {
            position += length;
            value = negative ? -calculateValue(i, index) : calculateValue(i, index);
        } else {
            position = ~position;
        }
    }

    private int calculateValue(final int i, final int index) {
        int startingIndex = i;
        int calculated = getAsciiCharacterFor(index);

        while (startingIndex < position) {
            calculated = ((calculated << 3) + (calculated << 1)) + getAsciiCharacterFor(startingIndex++);
        }
        return calculated;
    }

    private int getAsciiCharacterFor(final int index) {
        return text.charAt(index) - '0';
    }
}
