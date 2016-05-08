package org.joda.time.format;

final class OffsetCalculator {
    private final NumericSequence sequence;


    private int value;

    OffsetCalculator(final NumericSequence numericSequence) {
        sequence = numericSequence;
    }

    int getValue() {
        return value;
    }

    void calculate() {
        updatePositionAndValue(calculateLength());
    }

    private int calculateLength() {
        int length = sequence.isStartsWithSign() ? 1 : 0;
        while (length + 1 <= sequence.getLimit() && sequence.isDigitAt(length)) {
            length = length + 1;
        }
        return length;
    }

    private void updatePositionAndValue(int length) {
        if (length == 0) {
            sequence.setCurrentPosition(~sequence.getCurrentPosition());
        } else if (length >= 9) {
            useDefaultParser(length);
        } else {
            useFastParser(length);
        }
    }

    private void useDefaultParser(int length) {
        // Since value may exceed integer limits, use stock parser
        // which checks for this.
        final String toParse = sequence.getPart(length);
        value = Integer.parseInt(toParse);
        sequence.setCurrentPosition(sequence.getCurrentPosition() + length);
    }

    private void useFastParser(int length) {
        int i = sequence.isNegative() ? sequence.getCurrentPosition() + 1 : sequence.getCurrentPosition();

        final int index = i++;
        if (index < sequence.length()) {
            sequence.setCurrentPosition(sequence.getCurrentPosition() + length);
            value = sequence.isNegative() ? -calculateValue(i, index) : calculateValue(i, index);
        } else {
            sequence.setCurrentPosition(~sequence.getCurrentPosition());
        }
    }

    private int calculateValue(final int i, final int index) {
        int startingIndex = i;
        int calculated = getAsciiCharacterFor(index);

        while (startingIndex < sequence.getCurrentPosition()) {
            calculated = ((calculated << 3) + (calculated << 1)) + getAsciiCharacterFor(startingIndex++);
        }
        return calculated;
    }

    private int getAsciiCharacterFor(final int index) {
        return sequence.charAt(index) - '0';
    }

    static class NumericSequence {
        private final CharSequence text;
        private final int min;
        private final boolean isSigned;
        private final int startingPosition;
        private final boolean startsWithSign;
        private final boolean negative;
        private final int limit;

        private int currentPosition;

        NumericSequence(CharSequence text, int maximumDigitsToParse, boolean isSigned, int startingPosition) {
            this.text = text;
            this.isSigned = isSigned;
            this.startingPosition = startingPosition;
            min = Math.min(maximumDigitsToParse, text.length() - startingPosition);
            startsWithSign = min >= 1 && isSigned && isPrefixedWithPlusOrMinus();
            negative = isStartsWithSign() && text.charAt(startingPosition) == '-';
            // Expand the limit to disregard the sign character.
            currentPosition = isStartsWithSign() ? (isNegative() ? startingPosition : startingPosition + 1) : startingPosition;
            limit = isStartsWithSign() ? Math.min(getMin() + 1, getText().length() - getCurrentPosition()) : getMin();
        }

        boolean isStartsWithSign() { return startsWithSign; }

        boolean isNegative() { return negative; }

        int getCurrentPosition() { return currentPosition; }
        void setCurrentPosition(final int position) { currentPosition = position; }

        boolean isPrefixedWithPlusOrMinus() {
            final boolean isFirstCharacterOperator = isCharacterOperator(text.charAt(startingPosition));
            final boolean hasNextDigitCharacter = startingPosition < text.length() - 1 && Character.isDigit(text.charAt(startingPosition + 1));
            return isFirstCharacterOperator && hasNextDigitCharacter;
        }

        private static boolean isCharacterOperator(final char currentCharacter) {
            return currentCharacter == '-' || currentCharacter == '+';
        }

        boolean isDigitAt(final int index) {
            return Character.isDigit(charAt(getCurrentPosition() + index));
        }

        String getPart(final int length) {
            return text.subSequence(getCurrentPosition(), getCurrentPosition() + length).toString();
        }

        int length() { return  text.length(); }

        char charAt(int index) {
            return text.charAt(index);
        }

        public CharSequence getText() {
            return text;
        }

        public int getMin() {
            return min;
        }

        int getLimit() { return limit; }

        public boolean isSigned() {
            return isSigned;
        }
    }
}
