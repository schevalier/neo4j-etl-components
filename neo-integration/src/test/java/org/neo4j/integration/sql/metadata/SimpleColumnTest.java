package org.neo4j.integration.sql.metadata;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;

import org.neo4j.integration.sql.ConnectionConfig;
import org.neo4j.integration.sql.Credentials;
import org.neo4j.integration.sql.DatabaseType;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
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
        assertThat( column1.aliasedColumn(), is( "test.Person.id AS `id-alias`" ) );
        assertThat( column2.aliasedColumn(), is( "test.Person.username AS `username`" ) );
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
