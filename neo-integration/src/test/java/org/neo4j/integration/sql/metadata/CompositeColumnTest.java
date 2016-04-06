package org.neo4j.integration.sql.metadata;

import java.util.Collections;
import java.util.HashMap;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;

import org.neo4j.integration.sql.RowAccessor;
import org.neo4j.integration.sql.exportcsv.ColumnUtil;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class CompositeColumnTest
{
    private ColumnUtil columnUtil = new ColumnUtil();

    @Test
    public void shouldReturnCollectionOfAliasedColumnNames()
    {
        // given
        TableName authorTable = new TableName( "test.Author" );
        Column column = columnUtil.compositeKeyColumn(
                authorTable,
                asList( "first_name", "last_name" ),
                ColumnRole.PrimaryKey );
        //then
        assertThat( column.aliasedColumn(),
                is( "`test`.`Author`.`first_name` AS `first_name`, `test`.`Author`.`last_name` AS `last_name`" ) );
    }

    @Test
    public void selectFromRowReturnsEmptyStringIfAllOfTheCompositeKeyColumnsAreNull() throws Exception
    {
        // given
        Column compositeColumn = columnUtil.compositeKeyColumn(
                new TableName( "test.Users" ),
                asList( "first_name", "last_name" ),
                ColumnRole.PrimaryKey );

        RowAccessor stubRowAccessor = columnLabel ->
                singletonList( Collections.<String, String>emptyMap() ).get( 0 ).get( columnLabel );

        // when
        String value = compositeColumn.selectFrom( stubRowAccessor );

        // then
        assertTrue( value.isEmpty() );
    }

    @Test
    public void selectFromRowReturnsEmptyStringIfAtLeastOneCompositeKeyColumnIsNull() throws Exception
    {
        // given
        HashMap<String, String> rowOne = new HashMap<>();
        rowOne.put( "first_name", "Boaty" );
        rowOne.put( "last_name", null );

        Column compositeColumn = columnUtil.compositeKeyColumn(
                new TableName( "test.Users" ),
                asList( "first_name", "last_name" ),
                ColumnRole.PrimaryKey );

        RowAccessor stubRowAccessor = columnLabel ->
                singletonList( rowOne ).get( 0 ).get( columnLabel );

        // when
        String value = compositeColumn.selectFrom( stubRowAccessor );

        // then
        assertTrue( value.isEmpty() );
    }

    @Test
    public void selectFromRowReturnsStringJoinedByNullCharacterForCompositeKeyColumnValues() throws Exception
    {
        // given
        HashMap<String, String> rowOne = new HashMap<>();
        rowOne.put( "first_name", "Boaty" );
        rowOne.put( "last_name", "Mc.Boatface" );

        Column compositeColumn = columnUtil.compositeKeyColumn(
                new TableName( "test.Users" ),
                asList( "first_name", "last_name" ),
                ColumnRole.PrimaryKey );

        RowAccessor stubRowAccessor = columnLabel -> singletonList( rowOne ).get( 0 ).get( columnLabel );
        // when
        String value = compositeColumn.selectFrom( stubRowAccessor );

        // then
        assertThat( value, is( "\"Boaty\0Mc.Boatface\"" ) );
    }

    @Test
    public void shouldSerializeToAndDeserializeFromJson()
    {
        // given
        TableName authorTable = new TableName( "test.Author" );
        Column column = columnUtil.compositeKeyColumn(
                authorTable,
                asList( "first_name", "last_name" ),
                ColumnRole.PrimaryKey );

        JsonNode json = column.toJson();

        // when
        Column deserialized = Column.fromJson( json );

        //then
        assertEquals( column, deserialized );
    }
}
