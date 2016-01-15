package org.neo4j.integration.sql.metadata;

import java.util.IllegalFormatException;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TableNameTest
{
    @Test
    public void shouldReturnSimpleNameFromQualifiedName()
    {
        // given
        TableName tableName = new TableName( "example.Person" );

        // when
        String simpleName = tableName.simpleName();

        // then
        assertEquals( "Person", simpleName );
    }

    @Test
    public void shouldReturnSimpleNameFromSimpleName()
    {
        // given
        TableName tableName = new TableName( "Person" );

        // when
        String simpleName = tableName.simpleName();

        // then
        assertEquals( "Person", simpleName );
    }

    @Test
    public void shouldReturnSchemaFromQualifiedName()
    {
        // given
        TableName tableName = new TableName( "example.Person" );

        // when
        String schema = tableName.schema();

        // then
        assertEquals( "example", schema );
    }

    @Test
    public void shouldThrowExceptionIfNoSchemaPresent()
    {
        // given
        TableName tableName = new TableName( "Person" );

        try
        {

            // when
            tableName.schema();
            fail( "Expected IllegalArgumentException" );
        }
        catch ( IllegalArgumentException e )
        {
            // then
            assertEquals( "Table name does not include schema: Person", e.getMessage() );
        }
    }
}
