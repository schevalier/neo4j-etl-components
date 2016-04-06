package org.neo4j.integration.sql.metadata;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class SimpleColumnTest
{
    @Test
    public void shouldReturnCollectionOfAliasedColumnNames()
    {
        // given
        TableName personTable = new TableName( "test.Person" );
        Column column1 = new SimpleColumn( personTable, "id", "id-alias", ColumnType.PrimaryKey, SqlDataType.INT );

        Column column2 = new SimpleColumn( personTable, "username", ColumnType.Data, SqlDataType.TEXT );

        // then
        assertThat( column1.aliasedColumn(), is( "test.Person.id AS `id-alias`" ) );
        assertThat( column2.aliasedColumn(), is( "test.Person.username AS `username`" ) );
    }
}
