package org.neo4j.etl.neo4j.importcsv.config.formatting;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

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
