package org.joda.time.format;

final class OffsetCalculator {
    private final CharSequence text;
    private final boolean startsWithSign;
    private final boolean negative;
    private final int limit;

    private int currentPosition;
    private int value;


    OffsetCalculator(final CharSequence text,
                     final int maximumDigitsToParse,
                     final boolean isSigned,
                     final int startingPosition) {
        this.text = text;
        final int min = Math.min(maximumDigitsToParse, text.length() - startingPosition);
        startsWithSign = min >= 1 && isSigned && isPrefixedWithPlusOrMinus(text, startingPosition);
        negative = startsWithSign && text.charAt(startingPosition) == '-';
        currentPosition = startsWithSign ? (negative ?  startingPosition : startingPosition + 1) : startingPosition;
        // Expand the limit to disregard the sign character.
        limit = startsWithSign ? Math.min(min + 1, text.length() - currentPosition) : min;
    }

    private static boolean isPrefixedWithPlusOrMinus(final CharSequence text, final int startingPosition) {
        final boolean isFirstCharacterOperator = isCharacterOperator(text.charAt(startingPosition));
        final boolean hasNextDigitCharacter = startingPosition < text.length() - 1 && Character.isDigit(text.charAt(startingPosition + 1));
        return isFirstCharacterOperator && hasNextDigitCharacter;
    }

    private static boolean isCharacterOperator(final char currentCharacter) {
        return currentCharacter == '-' || currentCharacter == '+';
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
        int length = startsWithSign ? 1 : 0;
        while (length + 1 <= limit && Character.isDigit(text.charAt(currentPosition + length))) {
            length = length + 1;
        }
        return length;
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
