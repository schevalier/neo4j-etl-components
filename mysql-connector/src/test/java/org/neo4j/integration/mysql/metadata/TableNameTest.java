package org.neo4j.integration.mysql.metadata;

import org.junit.Test;

import static org.junit.Assert.*;

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
}
