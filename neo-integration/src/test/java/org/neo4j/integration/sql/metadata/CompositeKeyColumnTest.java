package org.neo4j.integration.sql.metadata;

import java.util.Collections;
import java.util.HashMap;
import java.util.stream.Stream;

import org.junit.Test;

import org.neo4j.integration.sql.RowAccessor;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

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

    @Test
    public void selectFromRowReturnsEmptyStringIfAllOfTheCompositeKeyColumnsAreNull() throws Exception
    {
        // given
        Column compositeColumn = createCompositeKeyColumn();


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

        Column compositeColumn = createCompositeKeyColumn();


        RowAccessor stubRowAccessor = columnLabel ->
                singletonList(  rowOne ).get( 0 ).get( columnLabel );

        // when
        String value = compositeColumn.selectFrom( stubRowAccessor );

        // then
        assertTrue( value.isEmpty() );
    }

    @Test
    public void selectFromRowReturnsStringJoinedBy_ForCompositeKeyColumnValues() throws Exception
    {
        // given
        HashMap<String, String> rowOne = new HashMap<>();
        rowOne.put( "first_name", "Boaty" );
        rowOne.put( "last_name", "Mc.Boatface" );

        Column compositeColumn = createCompositeKeyColumn();


        RowAccessor stubRowAccessor = columnLabel -> singletonList( rowOne ).get( 0 ).get( columnLabel );
        // when
        String value = compositeColumn.selectFrom( stubRowAccessor );

        // then
        assertThat( value, is("Boaty_Mc.Boatface"));
    }

    private Column createCompositeKeyColumn()
    {
        TableName table = new TableName( "test.Users" );
        return new CompositeKeyColumn( table,
                asList( new SimpleColumn(
                                new TableName( "test.Users" ),
                                "test.Users.first_name",
                                "first_name",
                                ColumnType.PrimaryKey,
                                SqlDataType.KEY_DATA_TYPE ),
                        new SimpleColumn(
                                new TableName( "test.Users" ),
                                "test.Users.last_name",
                                "last_name",
                                ColumnType.PrimaryKey,
                                SqlDataType.KEY_DATA_TYPE ) ) );
    }
}
