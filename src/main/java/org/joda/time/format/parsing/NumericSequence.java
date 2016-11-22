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
        negative = startsWithSign && charAt(startingPosition) == '-';
        currentPosition = startingPosition;
        // Expand the limit to disregard the sign character.
        limit = Math.min(startsWithSign ? maximumDigitsToParse + 1 : maximumDigitsToParse, text.length() - getCurrentPosition());
        length = calculateLength();
    }

    private boolean isPrefixedWithPlusOrMinus(final int maximumDigitsToParse, final boolean isSigned, final int startingPosition) {
        final int min = Math.min(maximumDigitsToParse, text.length() - startingPosition);
        return min >= 1 && isSigned && isCharacterOperator(charAt(startingPosition)) && hasNextDigitCharacter(startingPosition);
    }

    private char charAt(int index) {
        return text.charAt(index);
    }

    private static boolean isCharacterOperator(final char currentCharacter) {
        return currentCharacter == '-' || currentCharacter == '+';
    }

    private boolean hasNextDigitCharacter(int startingPosition) {
        return startingPosition < text.length() - 1 && isDigitAt(startingPosition + 1);
    }

    private boolean isDigitAt(final int index) {
        return Character.isDigit(charAt(index));
    }

    private int calculateLength() {
        int length = startsWithSign ? 1 : 0;
        while (length + 1 <= limit && isDigitAt(getCurrentPosition() + length)) {
            length = length + 1;
        }
        return length;
    }

    public int getIndexOfFirstDigit() {
        return startsWithSign ? getCurrentPosition() + 1 : getCurrentPosition();
    }

    public boolean isNegative() {
        return negative;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void addLengthToPosition() {
        currentPosition = getCurrentPosition() + length;
    }

    public void invertPosition() {
        currentPosition = ~getCurrentPosition();
    }

    public String getNumberAsString() {
        return text.subSequence(getCurrentPosition(), getCurrentPosition() + length).toString();
    }

    public boolean hasMoreThanOneDigit() {
        return (isNegative() ? getCurrentPosition() + 1 : getCurrentPosition()) < text.length();
    }

    public int getAsciiCharacterFor(final int index) {
        return charAt(index) - '0';
    }

    public int getLength() { return length; }
}
