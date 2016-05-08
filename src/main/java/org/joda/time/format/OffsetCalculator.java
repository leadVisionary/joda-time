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
        return fastCalculate(length);
    }

    private int fastCalculate(int length) {
        int i = sequence.isNegative() ? sequence.getCurrentPosition() + 1 : sequence.getCurrentPosition();
        final int index = i;
        i = i + 1;
        sequence.setCurrentPosition(sequence.getCurrentPosition() + length);
        return sequence.isNegative() ? -calculateValue(i, index) : calculateValue(i, index);
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

        boolean isPrefixedWithPlusOrMinus() {
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
