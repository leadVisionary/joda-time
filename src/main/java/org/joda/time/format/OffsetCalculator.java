package org.joda.time.format;

final class OffsetCalculator {
    private final CharSequence text;
    private final boolean isSigned;
    private int currentPosition;
    private boolean negative;
    private int limit;
    private int value;

    OffsetCalculator(final CharSequence text,
                     final int maximumDigitsToParse,
                     final boolean isSigned,
                     final int startingPosition) {
        this.text = text;
        this.currentPosition = startingPosition;
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
        updatePositionAndValue(calculateLength());
    }

    private int calculateLength() {
        int length = 0;
        while (length < limit && isBeforeBoundary(length) && (Character.isDigit(text.charAt(currentPosition + length)) || isPrefixedWithPlusOrMinus(length) && isSigned)) {
            if (isPrefixedWithPlusOrMinus(length)) {
                negative = text.charAt(currentPosition + length) == '-';
                currentPosition = negative ? currentPosition : currentPosition + 1;
                // Expand the limit to disregard the sign character.
                limit = Math.min(limit + 1, text.length() - currentPosition);
            }
            length = length + 1;
        }
        return length;
    }

    private boolean isPrefixedWithPlusOrMinus(int length) {
        final int index = currentPosition + length;
        final char currentCharacter = text.charAt(index);
        final boolean isFirstCharacterOperator = length == 0 && isCharacterOperator(currentCharacter);
        final boolean hasNextDigitCharacter = index < text.length() - 1 && Character.isDigit(text.charAt(index + 1));
        return isFirstCharacterOperator && hasNextDigitCharacter;
    }

    private static boolean isCharacterOperator(final char currentCharacter) {
        return currentCharacter == '-' || currentCharacter == '+';
    }

    private boolean isBeforeBoundary(int length) {
        return length + 1 <= limit;
    }

    private void updatePositionAndValue(int length) {
        if (length == 0) {
            currentPosition = ~currentPosition;
        } else if (length >= 9) {
            useDefaultParser(length);
        } else {
            useFastParser(length);
        }
    }

    private void useDefaultParser(int length) {
        // Since value may exceed integer limits, use stock parser
        // which checks for this.
        final String toParse = text.subSequence(currentPosition, currentPosition + length).toString();
        value = Integer.parseInt(toParse);
        currentPosition += length;
    }

    private void useFastParser(int length) {
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
