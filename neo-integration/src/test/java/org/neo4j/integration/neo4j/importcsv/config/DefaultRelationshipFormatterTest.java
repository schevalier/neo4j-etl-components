package org.neo4j.integration.neo4j.importcsv.config;

import org.junit.Test;

import static org.junit.Assert.*;

public class DefaultRelationshipFormatterTest
{
    @Test
    public void shouldFormatAsUpperCaseWithUnderscores()
    {
         // given
        DefaultRelationshipFormatter formatter = new DefaultRelationshipFormatter();

         // then
        assertEquals("AUTHOR_BOOK", formatter.format( "AuthorBook" ));
        assertEquals("AUTHOR_BOOK", formatter.format( "authors_books" ));
    }
}
