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

}
