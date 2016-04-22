package org.neo4j.integration.neo4j.importcsv.config;

import edu.washington.cs.knowitall.morpha.MorphaStemmer;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DefaultRelationshipFormatterTest
{
    @Test
    public void shouldFormatAsUpperCaseWithUnderscores()
    {
        // given
        DefaultRelationshipFormatter formatter = new DefaultRelationshipFormatter();

        // then
        assertEquals( "AUTHOR_BOOK", formatter.format( "AuthorBook" ) );
        assertEquals( "AUTHOR_BOOK", formatter.format( "authors_books" ) );
    }

    @Test
    public void should()
    {
        // given
        System.out.println( MorphaStemmer.stem( "communities" ) );
        System.out.println( MorphaStemmer.stem( "authors_books" ) );

        // when

        // then
    }
}
