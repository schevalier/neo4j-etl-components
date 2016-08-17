package org.neo4j.etl.sql.metadata;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
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

    @Test
    public void shouldImplementCaseInsensitiveEquality()
    {
        // given
        TableName tableName1 = new TableName( "example.Person" );
        TableName tableName2 = new TableName( "example.Person" );
        TableName tableName3 = new TableName( "example.person" );
        TableName tableName4 = new TableName( "EXAMPLE.PERSON" );
        TableName tableName5 = new TableName( "test.Address" );

        // then
        assertEquals( tableName1, tableName2 );
        assertEquals( tableName2, tableName1 );
        assertEquals( tableName1, tableName3 );
        assertEquals( tableName1, tableName4 );
        assertNotEquals( tableName1, tableName5 );
    }
}
