package org.neo4j.etl.neo4j.importcsv.config.formatting;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DefaultLabelFormatterTest
{
    @Test
    public void shouldConvertToUpperCamelCase()
    {
        // given
        DefaultLabelFormatter formatter = new DefaultLabelFormatter();

        // then
        assertEquals( "OneTwoThree", formatter.format( "one_two_three" ) );
        assertEquals( "OneTwoThree", formatter.format( "one_two_three_" ) );
        assertEquals( "OneTwoThree", formatter.format( "_one_two_three" ) );
        assertEquals( "Person", formatter.format( "PERSON" ) );
        assertEquals( "AuthorBook", formatter.format( "AUTHOR_BOOK" ) );
        assertEquals( "Person", formatter.format( "PERSONS" ) );
        assertEquals( "Bus", formatter.format( "buses" ) );
        assertEquals( "AuthorBook", formatter.format( "authors_books" ) );
    }
}
