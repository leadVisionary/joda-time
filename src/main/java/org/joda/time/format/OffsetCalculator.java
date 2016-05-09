package org.joda.time.format;

final class OffsetCalculator {
    private OffsetCalculator() {}

    static int calculate(final NumericSequence sequence) {
        final int length = sequence.getLength();
        if (length == 0 || !sequence.hasMoreThanOneDigit()) {
            sequence.setCurrentPosition(~sequence.getCurrentPosition());
            return 0;
        } else if (length >= 9) {
            return defaultCalculate(sequence, length);
        } else {
            return fastCalculate(sequence, length);
        }
    }

    private static int defaultCalculate(final NumericSequence sequence, int length) {
        // Since value may exceed integer limits, use stock parser
        // which checks for this.
        final String toParse = sequence.getPart(length);
        sequence.setCurrentPosition(sequence.getCurrentPosition() + length);
        return Integer.parseInt(toParse);
    }

    private static int fastCalculate(final NumericSequence sequence, int length) {
        int startingIndex = sequence.isNegative() ? sequence.getCurrentPosition() + 1 : sequence.getCurrentPosition();
        int calculated = sequence.getAsciiCharacterFor(startingIndex);
        sequence.setCurrentPosition(sequence.getCurrentPosition() + length);
        for (int i = startingIndex + 1; i < sequence.getCurrentPosition(); i++) {
            calculated = ((calculated << 3) + (calculated << 1)) + sequence.getAsciiCharacterFor(i);
        }
        return sequence.isNegative() ? -calculated : calculated;
    }
}
