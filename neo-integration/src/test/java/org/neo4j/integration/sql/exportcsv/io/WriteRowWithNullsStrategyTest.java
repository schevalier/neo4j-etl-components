package org.neo4j.integration.sql.exportcsv.io;

import java.util.HashMap;
import java.util.List;

import org.junit.Test;

import org.neo4j.integration.sql.RowAccessor;
import org.neo4j.integration.sql.exportcsv.ColumnUtil;
import org.neo4j.integration.sql.metadata.Column;
import org.neo4j.integration.sql.metadata.ColumnType;
import org.neo4j.integration.sql.metadata.TableName;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class WriteRowWithNullsStrategyTest
{

    private ColumnUtil columnUtil = new ColumnUtil();

    @Test
    public void shouldReturnTrueIfAnyOfTheNonKeyColumnsAreNull() throws Exception
    {
        // given
        HashMap<String, String> rowOne = new HashMap<>();
        rowOne.put( "id", "1" );
        rowOne.put( "username", "user-1" );
        rowOne.put( "first_name", "Boaty" );
        rowOne.put( "last_name", "Mc.Boatface" );
        rowOne.put( "age", null );

        TableName table = new TableName( "test.users" );
        List<Column> columns = asList(
                columnUtil.column( table, "id", ColumnType.PrimaryKey ),
                columnUtil.column( table, "username", ColumnType.ForeignKey ),
                columnUtil.column( table, "age", ColumnType.Data ),
                columnUtil.compositeColumn( table, asList( "first_name", "last_name" ) ) );

        // when
        WriteRowWithNullsStrategy strategy = new WriteRowWithNullsStrategy();

        // then
        RowAccessor stubRowAccessor = columnLabel -> singletonList( rowOne ).get( 0 ).get( columnLabel );
        assertTrue( strategy.test( stubRowAccessor, columns ) );
    }

    @Test
    public void shouldReturnFalseIfAnyOfTheKeyColumnsAreNull() throws Exception
    {
        // given
        HashMap<String, String> rowOne = new HashMap<>();
        rowOne.put( "id", "1" );
        rowOne.put( "username", null );
        rowOne.put( "age", "42" );
        TableName table = new TableName( "test.Users" );

        List<Column> columns = asList(
                columnUtil.column( table, "id", ColumnType.PrimaryKey ),
                columnUtil.column( table, "username", ColumnType.ForeignKey ),
                columnUtil.column( table, "age", ColumnType.Data ) );

        // when
        WriteRowWithNullsStrategy strategy = new WriteRowWithNullsStrategy();

        // then
        RowAccessor stubRowAccessor = columnLabel -> singletonList( rowOne ).get( 0 ).get( columnLabel );
        assertFalse( strategy.test( stubRowAccessor, columns ) );
    }

    @Test
    public void shouldReturnFalseIfAnyOfTheCompositeKeyColumnsAreNull() throws Exception
    {
        // given
        HashMap<String, String> rowOne = new HashMap<>();
        rowOne.put( "age", "42" );

        TableName table = new TableName( "test.Users" );
        Column compositeColumn = columnUtil.compositeColumn( table, asList( "first_name", "last_name" ) );

        List<Column> columns = asList(
                compositeColumn,
                columnUtil.column( table, "age", ColumnType.Data ) );

        // when
        WriteRowWithNullsStrategy strategy = new WriteRowWithNullsStrategy();

        // then
        RowAccessor stubRowAccessor = columnLabel -> singletonList( rowOne ).get( 0 ).get( columnLabel );
        assertFalse( strategy.test( stubRowAccessor, columns ) );
    }

    @Test(expected = IllegalStateException.class)
    public void shouldBubbleAccessorException() throws Exception
    {
        // given
        TableName table = new TableName( "users" );
        List<Column> columns = asList(
                columnUtil.column( table, "id", ColumnType.PrimaryKey ),
                columnUtil.column( table, "username", ColumnType.ForeignKey ),
                columnUtil.column( table, "age", ColumnType.Data ) );

        // when
        WriteRowWithNullsStrategy strategy = new WriteRowWithNullsStrategy();

        // then
        RowAccessor stubRowAccessor = columnLabel -> {
            throw new IllegalStateException();
        };
        strategy.test( stubRowAccessor, columns );
        fail( "Should have bubbled up the exception" );
    }
}
