package org.joda.time.format;

import org.joda.time.DateTimeFieldType;

final class FastNumberParser {
    private final DateTimeFieldType iFieldType;
    private final int iMaxParsedDigits;
    private final boolean iSigned;

    public FastNumberParser(final int maxParsedDigits,
                            final boolean signed,
                            final DateTimeFieldType fieldType) {
        iFieldType = fieldType;
        iMaxParsedDigits = maxParsedDigits;
        iSigned = signed;
    }

    public int parse(final DateTimeParserBucket bucket,
                     final CharSequence text,
                     int position) {


        final OffsetCalculator calculator = new OffsetCalculator(text, position);
        calculator.invoke(iMaxParsedDigits, iSigned);
        position = calculator.getPosition();


        bucket.saveField(iFieldType, calculator.getValue());
        return position;
    }

    private static class OffsetCalculator {
        private final CharSequence text;
        private int position;
        private int length;
        private boolean negative;
        private int limit;
        private int value;

        public OffsetCalculator(final CharSequence text,
                                final int position) {
            this.text = text;
            this.position = position;
            length = 0;
            negative = false;
        }

        int getPosition() {
            return position;
        }

        int getLength() {
            return length;
        }

        int getValue() { return value; }

        boolean isNegative() {
            return negative;
        }

        public void invoke(final int iMaxParsedDigits, final boolean iSigned) {
            limit = Math.min(iMaxParsedDigits, text.length() - position);
            while (length < limit && (isADigit(text.charAt(position + length))
                    || isPrefixedWithPlusOrMinus() && iSigned)) {
                updateBasedOnSign();
                length = length + 1;
            }

            if (length == 0) {
                position = ~position;
                return;
            }

            if (length >= 9) {
                // Since value may exceed integer limits, use stock parser
                // which checks for this.
                value = Integer.parseInt(text.subSequence(position, position += length).toString());
            } else {
                int i = position;
                if (isNegative()) {
                    i++;
                }

                final int index = i++;
                if (index > text.length()) {
                    position = ~position;
                    return;
                }
                value = text.charAt(index) - '0';
                position += length;
                while (i < position) {
                    value = ((value << 3) + (value << 1)) + text.charAt(i++) - '0';
                }
                if (isNegative()) {
                    value = -value;
                }
            }

        }

        private static boolean isADigit(final char c) {
            return !isNotADigit(c);
        }

        private static boolean isNotADigit(final char c) {
            return c < '0' || c > '9';
        }

        private boolean isPrefixedWithPlusOrMinus() {
            final int index = position + length;
            final char currentCharacter = text.charAt(index);
            final boolean isFirstCharacterOperator = length == 0 && (currentCharacter == '-' || currentCharacter == '+');
            final boolean hasNextDigitCharacter = index < text.length() - 1 && isADigit(text.charAt(index + 1));
            return isFirstCharacterOperator && isBeforeBoundary() && hasNextDigitCharacter;
        }

        private boolean isBeforeBoundary() {
            return length + 1 <= limit;
        }

        private void updateBasedOnSign() {
            if (isPrefixedWithPlusOrMinus()) {
                negative = text.charAt(position + length) == '-';
                length = (negative) ? length + 1 : length;
                position = (negative) ? position : position + 1;
                // Expand the limit to disregard the sign character.
                limit = Math.min(limit + 1, text.length() - position);
            }
        }
    }
}
