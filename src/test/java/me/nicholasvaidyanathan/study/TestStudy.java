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
     */
    public void test_fix_issues_86_correctly_parses_plus_in_front_of_year_for_yyyyMMdd() {
        // Given: a DatetimeFormatter for an expected pattern like 20130109
        final DateTimeFormatter formatter = givenADateTimeFormatter("yyyyMMdd");
        // When:  I try to parse a date that includes + per the ISO8601 spec of allowing it for 5 digit years
        final DateTime result = formatter.parseDateTime("+20130109");
        // Then: I get back the correct year
        assertEquals(2013, result.getYear());
        // And: I get back the correct month
        assertEquals(1, result.getMonthOfYear());
        // And: I get back the correct day
        assertEquals(9, result.getDayOfMonth());
    }

    private DateTimeFormatter givenADateTimeFormatter(String pattern) {
        return DateTimeFormat.forPattern(pattern);
    }

    /**
     * https://github.com/JodaOrg/joda-time/issues/86
     */
    public void test_fix_issues_86_correctly_parses_plus_in_front_of_year_for_MMyyyydd() {
        // Given: a DatetimeFormatter for an expected pattern like 01201309
        final DateTimeFormatter formatter = givenADateTimeFormatter("MMyyyydd");
        // When:  I try to parse a date that includes + per the ISO8601 spec of allowing it for 5 digit years
        final DateTime result = formatter.parseDateTime("01+201309");
        // Then: I get back the correct year
        assertEquals(2013, result.getYear());
        // And: I get back the correct month
        assertEquals(1, result.getMonthOfYear());
        // And: I get back the correct day
        assertEquals(9, result.getDayOfMonth());
    }

    /**
     * https://github.com/JodaOrg/joda-time/issues/86
     */
    public void test_fix_issues_86_correctly_parses_plus_in_front_of_year_for_MM_yyyy_dd() {
        // Given: a DatetimeFormatter for an expected pattern like 01-2013-09
        final DateTimeFormatter formatter = givenADateTimeFormatter("MM-yyyy-dd");
        // When:  I try to parse a date that includes + per the ISO8601 spec of allowing it for 5 digit years
        final DateTime result = formatter.parseDateTime("01-+2013-09");
        // Then: I get back the correct year
        assertEquals(2013, result.getYear());
        // And: I get back the correct month
        assertEquals(1, result.getMonthOfYear());
        // And: I get back the correct day
        assertEquals(9, result.getDayOfMonth());
    }

    /**
     * https://github.com/JodaOrg/joda-time/issues/86
     */
    public void test_fix_issues_86_correctly_parses_plus_in_front_of_year_for_yyyydd() {
        // Given: a DatetimeFormatter for an expected pattern like 201309
        final DateTimeFormatter formatter = givenADateTimeFormatter("yyyydd");
        // When:  I try to parse a date that includes + per the ISO8601 spec of allowing it for 5 digit years
        final DateTime result = formatter.parseDateTime("+201309");
        // Then: I get back the correct year
        assertEquals(2013, result.getYear());
        // And: I get back the correct day
        assertEquals(9, result.getDayOfYear());
    }
}
