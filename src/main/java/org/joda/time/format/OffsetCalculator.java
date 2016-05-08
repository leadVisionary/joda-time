package org.joda.time.format;

final class OffsetCalculator {
    private final NumericSequence sequence;

    OffsetCalculator(final NumericSequence numericSequence) {
        sequence = numericSequence;
    }

    int calculate() {
        return updatePositionAndValue(calculateLength());
    }

    private int calculateLength() {
        int length = sequence.isStartsWithSign() ? 1 : 0;
        while (length + 1 <= sequence.getLimit() && sequence.isDigitAt(length)) {
            length = length + 1;
        }
        return length;
    }

    private int updatePositionAndValue(int length) {
        if (length == 0) {
            sequence.setCurrentPosition(~sequence.getCurrentPosition());
            return 0;
        } else if (length >= 9) {
            return useDefaultParser(length);
        } else {
            return useFastParser(length);
        }
    }

    private int useDefaultParser(int length) {
        // Since value may exceed integer limits, use stock parser
        // which checks for this.
        final String toParse = sequence.getPart(length);
        sequence.setCurrentPosition(sequence.getCurrentPosition() + length);
        return Integer.parseInt(toParse);
    }

    private int useFastParser(int length) {
        int i = sequence.getIndexOfFirstDigit();

        final int index = i++;
        if (index < sequence.length()) {
            sequence.setCurrentPosition(sequence.getCurrentPosition() + length);
            return sequence.isNegative() ? -calculateValue(i, index) : calculateValue(i, index);
        } else {
            sequence.setCurrentPosition(~sequence.getCurrentPosition());
            return 0;
        }
    }

    private int calculateValue(final int i, final int index) {
        int startingIndex = i;
        int calculated = sequence.getAsciiCharacterFor(index);

        while (startingIndex < sequence.getCurrentPosition()) {
            calculated = ((calculated << 3) + (calculated << 1)) + sequence.getAsciiCharacterFor(startingIndex++);
        }
        return calculated;
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

        int getIndexOfFirstDigit() { return isNegative() ? getCurrentPosition() + 1 : getCurrentPosition(); }

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

        int getAsciiCharacterFor(final int index) {
            return charAt(index) - '0';
        }

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
