package test.sam.myutils.stringutils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

import sam.string.StringUtils;

class StringUtilsTest {

    @Test
    void test() {
        assertSame("", StringUtils.camelCaseToSpacedString(""));
        assertSame("anime", StringUtils.camelCaseToSpacedString("anime"));
        camel("aniss A", "anissA");
        camel("Database Minor Version", "DatabaseMinorVersion");
        camel("get Database Minor Version", "getDatabaseMinorVersion");
        camel("get Database Minor Version", "getDatabaseMinorVersion");
    }
    public void camel(String expected, String toBeConverted) {
        assertEquals(expected, StringUtils.camelCaseToSpacedString(toBeConverted));
    }

}
