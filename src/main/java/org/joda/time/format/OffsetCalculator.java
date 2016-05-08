package org.joda.time.format;

final class OffsetCalculator {
    private final CharSequence text;
    private final NumericSequence sequence;
    private final boolean negative;
    private final int limit;

    private int currentPosition;
    private int value;


    OffsetCalculator(NumericSequence numericSequence) {
        this.text = numericSequence.getText();
        sequence = numericSequence;
        negative = numericSequence.isStartsWithSign() && numericSequence.getText().charAt(numericSequence.getStartingPosition()) == '-';
        currentPosition = numericSequence.isStartsWithSign() ? (negative ? numericSequence.getStartingPosition() : numericSequence.getStartingPosition() + 1) : numericSequence.getStartingPosition();
        // Expand the limit to disregard the sign character.
        limit = numericSequence.isStartsWithSign() ? Math.min(numericSequence.getMin() + 1, numericSequence.getText().length() - currentPosition) : numericSequence.getMin();
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
        int length = sequence.isStartsWithSign() ? 1 : 0;
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

    static class NumericSequence {
        private final CharSequence text;
        private final int min;
        private final boolean isSigned;
        private final int startingPosition;
        private final boolean startsWithSign;

        NumericSequence(CharSequence text, int maximumDigitsToParse, boolean isSigned, int startingPosition) {
            this.text = text;
            this.isSigned = isSigned;
            this.startingPosition = startingPosition;
            min = Math.min(maximumDigitsToParse, text.length() - startingPosition);
            startsWithSign = min >= 1 && isSigned && isPrefixedWithPlusOrMinus();
        }

        boolean isStartsWithSign() { return startsWithSign; }

        boolean isPrefixedWithPlusOrMinus() {
            final boolean isFirstCharacterOperator = isCharacterOperator(text.charAt(startingPosition));
            final boolean hasNextDigitCharacter = startingPosition < text.length() - 1 && Character.isDigit(text.charAt(startingPosition + 1));
            return isFirstCharacterOperator && hasNextDigitCharacter;
        }

        private static boolean isCharacterOperator(final char currentCharacter) {
            return currentCharacter == '-' || currentCharacter == '+';
        }

        public CharSequence getText() {
            return text;
        }

        public int getMin() {
            return min;
        }

        public boolean isSigned() {
            return isSigned;
        }

        public int getStartingPosition() {
            return startingPosition;
        }
    }
}
