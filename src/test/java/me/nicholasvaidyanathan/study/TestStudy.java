package me.nicholasvaidyanathan.study;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import static junit.framework.Assert.assertEquals;

public class TestStudy extends TestCase {

    public static TestSuite suite() {
        return new TestSuite(TestStudy.class);
    }

    /**
     * https://github.com/JodaOrg/joda-time/issues/86
     * NOTE: This test does not pass at the beginning of the study.
     * It is expected to pass with the correct fix.
     */
    public void test_fix_issues_86_correctly_parses_plus_in_front_of_year_for_yyyyMMdd() {
        // When:  I try to parse a date that includes + per the ISO8601 spec of allowing it for 5 digit years
        final DateTime result = givenADateTimeFormatter("yyyyMMdd").parseDateTime("+20130109");
        // Then: I get back the correct year
        assertEquals(2013, result.getYear());
        // And: I get back the correct month
        assertEquals(1, result.getMonthOfYear());
        // And: I get back the correct day
        assertEquals(9, result.getDayOfMonth());
    }

    /**
     * Returns a DateTimeFormatter for the given pattern.
     * NOTE: THIS CODE CAN BE "TAKEN AS GIVEN": THE BUG IS NOT IN THIS CODE.
     * @param pattern
     * @return
     */
    private DateTimeFormatter givenADateTimeFormatter(String pattern) {
        return DateTimeFormat.forPattern(pattern);
    }

    /**
     * https://github.com/JodaOrg/joda-time/issues/86
     * NOTE: This test does not pass at the beginning of the study.
     * It is expected to pass with the correct fix.
     */
    public void test_fix_issues_86_correctly_parses_plus_in_front_of_year_for_MMyyyydd() {
        // When:  I try to parse a date that includes + per the ISO8601 spec of allowing it for 5 digit years
        final DateTime result = givenADateTimeFormatter("MMyyyydd").parseDateTime("01+201309");
        // Then: I get back the correct year
        assertEquals(2013, result.getYear());
        // And: I get back the correct month
        assertEquals(1, result.getMonthOfYear());
        // And: I get back the correct day
        assertEquals(9, result.getDayOfMonth());
    }

    /**
     * https://github.com/JodaOrg/joda-time/issues/86
     * NOTE: This test PASSES at the beginning of the study.
     * It is expected to CONTINUE TO PASS with the correct fix.
     */
    public void test_fix_issues_86_correctly_parses_plus_in_front_of_year_for_MM_yyyy_dd() {
        // When:  I try to parse a date that includes + per the ISO8601 spec of allowing it for 5 digit years
        final DateTime result = givenADateTimeFormatter("MM-yyyy-dd").parseDateTime("01-+2013-09");
        // Then: I get back the correct year
        assertEquals(2013, result.getYear());
        // And: I get back the correct month
        assertEquals(1, result.getMonthOfYear());
        // And: I get back the correct day
        assertEquals(9, result.getDayOfMonth());
    }

    /**
     * https://github.com/JodaOrg/joda-time/issues/86
     * NOTE: This test does not pass at the beginning of the study.
     * It is expected to pass with the correct fix.
     */
    public void test_fix_issues_86_correctly_parses_plus_in_front_of_year_for_yyyydd() {
        // When:  I try to parse a date that includes + per the ISO8601 spec of allowing it for 5 digit years
        final DateTime result = givenADateTimeFormatter("yyyydd").parseDateTime("+201309");
        // Then: I get back the correct year
        assertEquals(2013, result.getYear());
        // And: I get back the correct day
        assertEquals(9, result.getDayOfYear());
    }
}
