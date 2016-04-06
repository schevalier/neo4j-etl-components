package org.neo4j.integration.sql.metadata;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ColumnTypeTest
{
    @Test
    public void shouldReturnNameOnlyForLiteralColumnType()
    {
        // when
        String value = ColumnType.Literal.fullyQualifiedColumnName( new TableName( "test.db" ), "a-value" );

        // then
        assertEquals( "a-value", value );
    }

    @Test
    public void shouldReturnFullyQualifiedNameForAllOtherColumnTypes()
    {
        for ( ColumnType columnType : ColumnType.values() )
        {
            if ( columnType != ColumnType.Literal )
            {
                // when
                String value = columnType.fullyQualifiedColumnName( new TableName( "test.db" ), "a-value" );

                // then
                assertEquals( "test.db.a-value", value );

            }
        }
    }
}
