package org.neo4j.integration.sql.metadata;

import org.junit.Test;

import static java.util.Arrays.asList;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CompositeKeyColumnTest
{
    @Test
    public void shouldReturnCollectionOfAliasedColumnNames()
    {
        // given
        TableName authorTable = new TableName( "test.Author" );
        Column column = new CompositeKeyColumn( authorTable,
                asList( new SimpleColumn(
                                new TableName( "test.Author" ),
                                "test.Author.first_name",
                                "first_name",
                                ColumnType.PrimaryKey,
                                SqlDataType.KEY_DATA_TYPE ),
                        new SimpleColumn(
                                new TableName( "test.Author" ),
                                "test.Author.last_name",
                                "last_name",
                                ColumnType.PrimaryKey,
                                SqlDataType.KEY_DATA_TYPE ) ) );


        //then
        assertThat( column.aliasedColumn(),
                is( "test.Author.first_name AS first_name, test.Author.last_name AS last_name" ) );
    }

}
