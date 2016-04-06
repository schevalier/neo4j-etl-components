package org.neo4j.integration.sql.metadata;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ColumnRoleTest
{
    @Test
    public void shouldReturnNameOnlyForLiteralColumnType()
    {
        // when
        String value = ColumnRole.Literal.fullyQualifiedColumnName( new TableName( "test.db" ), "a-value" );

        // then
        assertEquals( "a-value", value );
    }

    @Test
    public void shouldReturnFullyQualifiedNameForAllOtherColumnTypes()
    {
        for ( ColumnRole columnRole : ColumnRole.values() )
        {
            if ( columnRole != ColumnRole.Literal )
            {
                // when
                String value = columnRole.fullyQualifiedColumnName( new TableName( "test.db" ), "a-value" );

                // then
                assertEquals( "test.db.a-value", value );

            }
        }
    }
}
