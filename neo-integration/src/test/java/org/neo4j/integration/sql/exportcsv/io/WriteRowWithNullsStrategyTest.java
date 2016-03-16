package org.neo4j.integration.sql.exportcsv.io;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import org.neo4j.integration.sql.RowAccessor;
import org.neo4j.integration.sql.exportcsv.mysql.MySqlDataType;
import org.neo4j.integration.sql.metadata.Column;
import org.neo4j.integration.sql.metadata.ColumnType;
import org.neo4j.integration.sql.metadata.TableName;

import static java.util.Arrays.asList;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class WriteRowWithNullsStrategyTest
{
    @Test
    public void shouldReturnTrueIfAnyOfTheNonKeyColumnsAreNull() throws Exception
    {
        // given
        List<Map<String, String>> rows = new ArrayList<>();
        HashMap<String, String> e = new HashMap<>();
        e.put( "id", "1" );
        e.put( "username", "user-1" );
        e.put( "age", null );
        rows.add( e );

        TableName table = new TableName( "users" );
        List<Column> columns = asList(
                new Column( table, "id", "id", ColumnType.PrimaryKey, MySqlDataType.TEXT ),
                new Column( table, "username", "username", ColumnType.ForeignKey, MySqlDataType.TEXT ),
                new Column( table, "age", "age", ColumnType.Data, MySqlDataType.TEXT ) );

        // when
//        results.next();
        WriteRowWithNullsStrategy strategy = new WriteRowWithNullsStrategy();

        // then
        RowAccessor stubRowAccessor = columnLabel -> rows.get( 0 ).get( columnLabel );
        assertTrue( strategy.test( stubRowAccessor, columns ) );
    }

    @Test
    public void shouldReturnFalseIfAnyOfTheKeyColumnsAreNull() throws Exception
    {
        // given
        List<Map<String, String>> rows = new ArrayList<>();
        HashMap<String, String> e = new HashMap<>();
        e.put( "id", "1" );
        e.put( "username", null );
        e.put( "age", "42" );
        rows.add( e );

        TableName table = new TableName( "users" );
        List<Column> columns = asList(
                new Column( table, "id", "id", ColumnType.PrimaryKey, MySqlDataType.TEXT ),
                new Column( table, "username", "username", ColumnType.ForeignKey, MySqlDataType.TEXT ),
                new Column( table, "age", "age", ColumnType.Data, MySqlDataType.TEXT ) );

        // when
        WriteRowWithNullsStrategy strategy = new WriteRowWithNullsStrategy();

        // then
        RowAccessor stubRowAccessor = columnLabel -> rows.get( 0 ).get( columnLabel );
        assertFalse( strategy.test( stubRowAccessor, columns ) );
    }

    @Test(expected = Exception.class)
    public void shouldBubbleException() throws Exception
    {
        // given
        List<Map<String, String>> rows = new ArrayList<>();

        TableName table = new TableName( "users" );
        List<Column> columns = asList(
                new Column( table, "id", "id", ColumnType.PrimaryKey, MySqlDataType.TEXT ),
                new Column( table, "username", "username", ColumnType.ForeignKey, MySqlDataType.TEXT ),
                new Column( table, "age", "age", ColumnType.Data, MySqlDataType.TEXT ) );

        // when
//        results.next();
        WriteRowWithNullsStrategy strategy = new WriteRowWithNullsStrategy();

        // then
        RowAccessor stubRowAccessor = columnLabel -> rows.get( 0 ).get( columnLabel );
        strategy.test( stubRowAccessor, columns );
        fail( "Should have bubbled up the exception" );
    }
}
