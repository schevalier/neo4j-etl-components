package org.neo4j.integration.sql.metadata;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;

import org.neo4j.integration.sql.QueryResults;
import org.neo4j.integration.sql.StubQueryResults;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class SimpleColumnTest
{

    @Test
    public void shouldReturnCollectionOfAliasedColumnNames()
    {
        // given
        TableName personTable = new TableName( "test.Person" );
        Column column1 = new SimpleColumn( personTable, "id", "id-alias", ColumnRole.PrimaryKey, SqlDataType.INT );

        Column column2 = new SimpleColumn( personTable, "username", ColumnRole.Data, SqlDataType.TEXT );

        // then
        assertThat( column1.aliasedColumn(), is( "`test`.`Person`.`id` AS `id-alias`" ) );
        assertThat( column2.aliasedColumn(), is( "`test`.`Person`.`username` AS `username`" ) );
    }

    @Test
    public void selectRowShouldGetValueFromResults() throws Exception
    {
        // given
        TableName personTable = new TableName( "test.Person" );
        QueryResults results = StubQueryResults.builder()
                .columns( "id", "username" )
                .addRow( "1", "user-1" )
                .build();

        Column column1 = new SimpleColumn( personTable, "id", ColumnRole.Data, SqlDataType.INT );
        Column column2 = new SimpleColumn( personTable, "username", ColumnRole.Data, SqlDataType.TEXT );

        // then
        results.next();
        assertThat( column1.selectFrom( results ), is( "1" ) );
        assertThat( column2.selectFrom( results ), is( "user-1" ) );
    }

    @Test
    public void selectRowFromShouldNotAddQuotesForNullValues() throws Exception
    {
        // given
        TableName personTable = new TableName( "test.Person" );
        QueryResults results = StubQueryResults.builder()
                .columns( "id", "username" )
                .addRow( null, null )
                .build();

        Column column1 = new SimpleColumn( personTable, "id", ColumnRole.Data, SqlDataType.INT );
        Column column2 = new SimpleColumn( personTable, "username", ColumnRole.Data, SqlDataType.TEXT );

        // then
        results.next();
        assertNull( column1.selectFrom( results ) );
        assertNull( column2.selectFrom( results ) );
    }

    @Test
    public void shouldNotAddBackTicksForLiteralColumns()
    {
        // given
        TableName personTable = new TableName( "test.Person" );
        Column labelColumn = new SimpleColumn(
                personTable,
                "\"Person\"",
                "Person",
                ColumnRole.Literal,
                SqlDataType.LABEL_DATA_TYPE );
        // then
        assertThat( labelColumn.aliasedColumn(), is( "\"Person\" AS `Person`" ) );
    }

    @Test
    public void shouldSerializeToAndDeserializeFromJson()
    {
        // given
        TableName personTable = new TableName( "test.Person" );
        Column column = new SimpleColumn( personTable, "id", "id-alias", ColumnRole.PrimaryKey, SqlDataType.INT );

        JsonNode json = column.toJson();

        // when
        Column deserialized = Column.fromJson( json );

        // then
        assertEquals( column, deserialized );
    }
}
