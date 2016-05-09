package org.joda.time.format;

final class OffsetCalculator {
    private final NumericSequence sequence;

    OffsetCalculator(final NumericSequence numericSequence) {
        sequence = numericSequence;
    }

    int calculate() {
        final int length = sequence.getLength();
        if (length == 0 || !sequence.hasMoreThanOneDigit()) {
            sequence.setCurrentPosition(~sequence.getCurrentPosition());
            return 0;
        } else if (length >= 9) {
            return defaultCalculate(length);
        } else {
            return fastCalculate(length);
        }
    }

    private int defaultCalculate(int length) {
        // Since value may exceed integer limits, use stock parser
        // which checks for this.
        final String toParse = sequence.getPart(length);
        sequence.setCurrentPosition(sequence.getCurrentPosition() + length);
        return Integer.parseInt(toParse);
    }

    private int fastCalculate(int length) {
        int startingIndex = sequence.isNegative() ? sequence.getCurrentPosition() + 1 : sequence.getCurrentPosition();
        int calculated = sequence.getAsciiCharacterFor(startingIndex);
        sequence.setCurrentPosition(sequence.getCurrentPosition() + length);
        for (int i = startingIndex + 1; i < sequence.getCurrentPosition(); i++) {
            calculated = ((calculated << 3) + (calculated << 1)) + sequence.getAsciiCharacterFor(i);
        }
        return sequence.isNegative() ? -calculated : calculated;
    }

    static class NumericSequence {
        private final CharSequence text;
        private final int min;
        private final int startingPosition;
        private final boolean startsWithSign;
        private final boolean negative;
        private final int limit;

        private int currentPosition;

        NumericSequence(CharSequence text, int maximumDigitsToParse, boolean isSigned, int startingPosition) {
            this.text = text;
            this.startingPosition = startingPosition;
            min = Math.min(maximumDigitsToParse, text.length() - startingPosition);
            startsWithSign = min >= 1 && isSigned && isPrefixedWithPlusOrMinus();
            negative = isStartsWithSign() && text.charAt(startingPosition) == '-';
            // Expand the limit to disregard the sign character.
            currentPosition = isStartsWithSign() ? (isNegative() ? startingPosition : startingPosition + 1) : startingPosition;
            limit = isStartsWithSign() ? Math.min(min + 1, text.length() - getCurrentPosition()) : min;
        }

        boolean isStartsWithSign() { return startsWithSign; }

        boolean isNegative() { return negative; }

        int getCurrentPosition() { return currentPosition; }
        void setCurrentPosition(final int position) { currentPosition = position; }

        private boolean isPrefixedWithPlusOrMinus() {
            final boolean isFirstCharacterOperator = isCharacterOperator(charAt(startingPosition));
            final boolean hasNextDigitCharacter = startingPosition < text.length() - 1 && Character.isDigit(charAt(startingPosition + 1));
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

        boolean hasMoreThanOneDigit() {
            return (isNegative() ? getCurrentPosition() + 1 : getCurrentPosition()) < text.length(); }
        
        int getAsciiCharacterFor(final int index) {
            return charAt(index) - '0';
        }

        char charAt(int index) {
            return text.charAt(index);
        }

        private int getLength() {
            int length = isStartsWithSign() ? 1 : 0;
            while (length + 1 <=  limit && isDigitAt(length)) {
                length = length + 1;
            }
            return length;
        }
    }
}
