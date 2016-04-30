package org.joda.time.format;

final class OffsetCalculator {
    private final CharSequence text;
    private final boolean isSigned;
    private int currentPosition;
    private int length;
    private boolean negative;
    private int limit;
    private int value;

    OffsetCalculator(final CharSequence text,
                     final int maximumDigitsToParse,
                     final boolean isSigned,
                     final int startingPosition) {
        this.text = text;
        this.currentPosition = startingPosition;
        length = 0;
        negative = false;
        this.isSigned = isSigned;
        limit = Math.min(maximumDigitsToParse, text.length() - startingPosition);
    }

    int getCurrentPosition() {
        return currentPosition;
    }

    int getValue() {
        return value;
    }

    void calculate() {
        calculateLength();
        updatePositionAndValue();
    }

    private void calculateLength() {
        while (length < limit && shouldContinue()) {
            updateBasedOnSign();
            length = length + 1;
        }
    }

    private boolean shouldContinue() {
        final boolean hasSign = isPrefixedWithPlusOrMinus() && isSigned;
        return Character.isDigit(text.charAt(currentPosition + length)) || hasSign;
    }

    private boolean isPrefixedWithPlusOrMinus() {
        final int index = currentPosition + length;
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
            negative = text.charAt(currentPosition + length) == '-';
            length = negative ? length + 1 : length;
            currentPosition = negative ? currentPosition : currentPosition + 1;
            // Expand the limit to disregard the sign character.
            limit = Math.min(limit + 1, text.length() - currentPosition);
        }
    }

    private void updatePositionAndValue() {
        if (length == 0) {
            currentPosition = ~currentPosition;
        } else if (length >= 9) {
            useDefaultParser();
        } else {
            useFastParser();
        }
    }

    private void useDefaultParser() {
        // Since value may exceed integer limits, use stock parser
        // which checks for this.
        final String toParse = text.subSequence(currentPosition, currentPosition + length).toString();
        value = Integer.parseInt(toParse);
        currentPosition += length;
    }

    private void useFastParser() {
        int i = negative ? currentPosition + 1 : currentPosition;

        final int index = i++;
        if (index < text.length()) {
            currentPosition += length;
            value = negative ? -calculateValue(i, index) : calculateValue(i, index);
        } else {
            currentPosition = ~currentPosition;
        }
    }

    private int calculateValue(final int i, final int index) {
        int startingIndex = i;
        int calculated = getAsciiCharacterFor(index);

        while (startingIndex < currentPosition) {
            calculated = ((calculated << 3) + (calculated << 1)) + getAsciiCharacterFor(startingIndex++);
        }
        return calculated;
    }

    private int getAsciiCharacterFor(final int index) {
        return text.charAt(index) - '0';
    }
}
