package org.neo4j.integration.sql.metadata;

import org.junit.Test;

import org.neo4j.integration.sql.exportcsv.TestUtil;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class SimpleColumnTest
{
    private TestUtil testUtil = new TestUtil();

    @Test
    public void shouldReturnCollectionOfAliasedColumnNames()
    {
        // given
        TableName personTable = new TableName( "test.Person" );
        Column column1 = testUtil.column( personTable, "test.Person.id", "id", ColumnType.PrimaryKey );

        Column column2 = testUtil.column( personTable, "test.Person.username", "username", ColumnType.Data );

        // then
        assertThat( column1.aliasedColumn(), is( "test.Person.id AS id" ) );
        assertThat( column2.aliasedColumn(), is( "test.Person.username AS username" ) );
    }
}
