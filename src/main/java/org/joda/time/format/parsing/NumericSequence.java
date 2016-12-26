package org.joda.time.format.parsing;

public final class NumericSequence {
    private final CharSequence text;
    private final boolean startsWithSign;
    private final boolean negative;
    private final int limit;
    private final int length;

    private int currentPosition;

    public NumericSequence(final CharSequence text, final int maximumDigitsToParse, final boolean isSigned, final int startingPosition) {
        this.text = text;
        startsWithSign = isPrefixedWithPlusOrMinus(maximumDigitsToParse, isSigned, startingPosition);
        negative = startsWithSign && text.charAt(startingPosition) == '-';
        currentPosition = determineCurrentPosition(startingPosition);
        limit = findLastIndexOfStringToParse(text, maximumDigitsToParse);
        length = calculateLength();
    }

    private boolean isPrefixedWithPlusOrMinus(final int maximumDigitsToParse, final boolean isSigned, final int startingPosition) {
        final int min = Math.min(maximumDigitsToParse, text.length() - startingPosition);
        if (min <= 1 || !isSigned) {
            return false;
        } else {
            final char currentCharacter = text.charAt(startingPosition);
            final boolean isCharacterOperator = currentCharacter == '-' || currentCharacter == '+';
            final boolean hasNextDigitCharacter = startingPosition < text.length() - 1 && Character.isDigit(text.charAt(startingPosition + 1));
            return isCharacterOperator && hasNextDigitCharacter;
        }
    }

    private int determineCurrentPosition(int startingPosition) {
        int position = startingPosition;
        if (startsWithSign) {
            if (!negative) {
                position = position + 1;
            }
        }
        return position;
    }

    private int findLastIndexOfStringToParse(final CharSequence text, final int maximumDigitsToParse) {
        final int lastIndex = startsWithSign ? maximumDigitsToParse + 1 : maximumDigitsToParse;
        return Math.min(lastIndex, text.length() - getCurrentPosition());
    }

    private int calculateLength() {
        int length = startsWithSign ? 1 : 0;
        while (length + 1 <= limit && Character.isDigit(text.charAt(getCurrentPosition() + length))) {
            length = length + 1;
        }
        return length;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    private int getAsciiCharacterFor(final int index) {
        return text.charAt(index) - '0';
    }

    public int calculate() {
        final int length = this.length;
        final boolean doesNotHaveMoreThan1Digit = !((negative ? getCurrentPosition() + 1 : getCurrentPosition()) < text.length());
        if (length == 0 || doesNotHaveMoreThan1Digit) {
            return handleFailure();
        } else if (length >= 9) {
            return defaultCalculate();
        } else {
            return this.fastCalculate();
        }
    }

    private int handleFailure() {
        currentPosition = ~getCurrentPosition();
        return 0;
    }
    private int defaultCalculate() {
        // Since value may exceed integer limits, use stock parser
        // which checks for this.
        final String toParse = text.subSequence(getCurrentPosition(), getCurrentPosition() + length).toString();
        currentPosition = getCurrentPosition() + length;
        return Integer.parseInt(toParse);
    }

    private int fastCalculate() {
        int indexOfFirstDigit = negative ? getCurrentPosition() + 1 : getCurrentPosition();
        int calculated = getAsciiCharacterFor(indexOfFirstDigit);
        currentPosition = getCurrentPosition() + length;
        for (int i = indexOfFirstDigit + 1; i < getCurrentPosition(); i++) {
            calculated = ((calculated << 3) + (calculated << 1)) + getAsciiCharacterFor(i);
        }
        return negative ? -calculated : calculated;
    }
}
