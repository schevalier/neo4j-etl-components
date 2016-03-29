package org.neo4j.integration.neo4j.importcsv.config;

import org.junit.Test;

import static org.junit.Assert.*;

public class DefaultPropertyFormatterTest
{
    @Test
    public void shouldConvertToCamelCase()
    {
        // given
        DefaultPropertyFormatter formatter = new DefaultPropertyFormatter();

        // then
        assertEquals( "oneTwoThree", formatter.format( "one_two_three" ) );
        assertEquals( "oneTwoThree", formatter.format( "one_two_three_" ) );
        assertEquals( "person", formatter.format( "PERSON" ) );
        assertEquals( "authorBook", formatter.format( "AUTHOR_BOOK" ) );
    }
}
