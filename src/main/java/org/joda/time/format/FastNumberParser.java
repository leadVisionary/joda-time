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
        final OffsetCalculator offsetCalculator = new OffsetCalculator(text, iSigned, iMaxParsedDigits, position).invoke();
        final int length = offsetCalculator.getLength();
        position = offsetCalculator.getPosition();
        final boolean negative = offsetCalculator.isNegative();

        if (length == 0) {
            return ~position;
        }

        int value;
        if (length >= 9) {
            // Since value may exceed integer limits, use stock parser
            // which checks for this.
            value = Integer.parseInt(text.subSequence(position, position += length).toString());
        } else {
            int i = position;
            if (negative) {
                i++;
            }

            final int index = i++;
            if (index > text.length()) {
                return ~position;
            }
            position += length;
            value = text.charAt(index) - '0';
            while (i < position) {
                value = ((value << 3) + (value << 1)) + text.charAt(i++) - '0';
            }
            if (negative) {
                value = -value;
            }
        }

        bucket.saveField(iFieldType, value);
        return position;
    }

    private static class OffsetCalculator {
        private final CharSequence text;
        private final boolean iSigned;
        private int position;
        private int length;
        private boolean negative;
        private int limit;

        public OffsetCalculator(final CharSequence text,
                                final boolean iSigned,
                                final int iMaxParsedDigits,
                                final int position) {
            this.text = text;
            this.iSigned = iSigned;
            this.position = position;
            length = 0;
            negative = false;
            limit = Math.min(iMaxParsedDigits, text.length() - position);
        }

        int getPosition() {
            return position;
        }

        int getLength() {
            return length;
        }

        boolean isNegative() {
            return negative;
        }

        public OffsetCalculator invoke() {


            while (length < limit && (isADigit(text.charAt(position + length))
                    || isPrefixedWithPlusOrMinus(position))) {
                updateBasedOnSign();
                length = length + 1;
            }
            return this;
        }

        private void updateBasedOnSign() {
            if (isPrefixedWithPlusOrMinus(position)) {
                negative = text.charAt(position + length) == '-';
                length = (negative) ? length + 1 : length;
                position = (negative) ? position : position + 1;
                // Expand the limit to disregard the sign character.
                limit = Math.min(limit + 1, text.length() - position);
            }
        }

        private boolean isPrefixedWithPlusOrMinus(final int position) {
            final int index = position + length;
            final char currentCharacter = text.charAt(index);
            final boolean isFirstCharacterOperator = length == 0 && (currentCharacter == '-' || currentCharacter == '+');
            final boolean hasNextDigitCharacter = index < text.length() - 1 && isADigit(text.charAt(index + 1));
            return isFirstCharacterOperator && iSigned && isBeforeBoundary() && hasNextDigitCharacter;
        }

        private boolean isBeforeBoundary() {
            return length + 1 <= limit;
        }

        private static boolean isADigit(final char c) {
            return !isNotADigit(c);
        }

        private static boolean isNotADigit(final char c) {
            return c < '0' || c > '9';
        }
    }
}
