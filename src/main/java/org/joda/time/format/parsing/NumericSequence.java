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
        final int min = Math.min(maximumDigitsToParse, text.length() - startingPosition);
        startsWithSign = min >= 1 && isSigned && isPrefixedWithPlusOrMinus(startingPosition);
        negative = startsWithSign && text.charAt(startingPosition) == '-';
        // Expand the limit to disregard the sign character.
        currentPosition = startsWithSign ? (isNegative() ? startingPosition : startingPosition + 1) : startingPosition;
        limit = startsWithSign ? Math.min(min + 1, text.length() - getCurrentPosition()) : min;
        length = calculateLength();
    }

    private int calculateLength() {
        int length = startsWithSign ? 1 : 0;
        while (length + 1 <= limit && isDigitAt(length)) {
            length = length + 1;
        }
        return length;
    }

    public int getIndexOfFirstDigit() {
        return isNegative() ? getCurrentPosition() + 1 : getCurrentPosition();
    }

    public boolean isNegative() {
        return negative;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(final int position) {
        currentPosition = position;
    }

    private boolean isPrefixedWithPlusOrMinus(final int startingPosition) {
        final boolean isFirstCharacterOperator = isCharacterOperator(charAt(startingPosition));
        final boolean hasNextDigitCharacter = startingPosition < text.length() - 1 && Character.isDigit(charAt(startingPosition + 1));
        return isFirstCharacterOperator && hasNextDigitCharacter;
    }

    private static boolean isCharacterOperator(final char currentCharacter) {
        return currentCharacter == '-' || currentCharacter == '+';
    }

    private boolean isDigitAt(final int index) {
        return Character.isDigit(charAt(getCurrentPosition() + index));
    }

    public String getPart(final int length) {
        return text.subSequence(getCurrentPosition(), getCurrentPosition() + length).toString();
    }

    public boolean hasMoreThanOneDigit() {
        return (isNegative() ? getCurrentPosition() + 1 : getCurrentPosition()) < text.length();
    }

    public int getAsciiCharacterFor(final int index) {
        return charAt(index) - '0';
    }

    private char charAt(int index) {
        return text.charAt(index);
    }

    public int getLength() { return length; }
}
