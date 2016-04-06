package org.neo4j.integration.sql.exportcsv.mysql.schema;

import java.util.Collection;

import org.junit.Test;

import org.neo4j.integration.io.AwaitHandle;
import org.neo4j.integration.sql.DatabaseClient;
import org.neo4j.integration.sql.QueryResults;
import org.neo4j.integration.sql.StubQueryResults;
import org.neo4j.integration.sql.exportcsv.ColumnUtil;
import org.neo4j.integration.sql.metadata.ColumnType;
import org.neo4j.integration.sql.metadata.CompositeColumn;
import org.neo4j.integration.sql.metadata.SimpleColumn;
import org.neo4j.integration.sql.metadata.SqlDataType;
import org.neo4j.integration.sql.metadata.Table;
import org.neo4j.integration.sql.metadata.TableName;

import static java.util.Arrays.asList;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TableMetadataProducerTest
{

    private final ColumnUtil columnUtil = new ColumnUtil();

    @Test
    public void shouldReturnTableMetadata() throws Exception
    {
        // given
        QueryResults results = StubQueryResults.builder()
                .columns( "COLUMN_NAME", "DATA_TYPE", "COLUMN_TYPE" )
                .addRow( "id", "INT", "PrimaryKey" )
                .addRow( "username", "TEXT", "Data" )
                .addRow( "addressId", "int", "ForeignKey" )
                .build();

        DatabaseClient databaseClient = mock( DatabaseClient.class );
        when( databaseClient.executeQuery( any( String.class ) ) ).thenReturn( AwaitHandle.forReturnValue( results ) );

        TableMetadataProducer tableMetadataProducer = new TableMetadataProducer( databaseClient );

        // when
        TableName forTable = new TableName( "test.Person" );
        Collection<Table> metadata = tableMetadataProducer.createMetadataFor( forTable );

        // then
        Table table = metadata.stream().findFirst().get();

        assertEquals( forTable, table.name() );
        assertEquals( "test.Person", table.descriptor() );

        assertThat( table.columns(), contains(
                new SimpleColumn( forTable, "id", ColumnType.PrimaryKey, SqlDataType.INT ),
                columnUtil.column( forTable, "username", ColumnType.Data ),
                new SimpleColumn( forTable, "addressId", ColumnType.ForeignKey, SqlDataType.INT ) ) );
    }

    @Test
    public void shouldReturnTableMetadataForJoinTable() throws Exception
    {
        // given
        QueryResults columnProjectionResults = StubQueryResults.builder()
                .columns( "COLUMN_NAME", "DATA_TYPE", "COLUMN_TYPE" )
                .addRow( "credits", "text", "Data" )
                .build();

        DatabaseClient databaseClient = mock( DatabaseClient.class );
        when( databaseClient.executeQuery( any( String.class ) ) ).thenReturn( AwaitHandle.forReturnValue(
                columnProjectionResults ) );

        TableMetadataProducer tableMetadataProducer =
                new TableMetadataProducer( databaseClient, c -> c == ColumnType.Data );

        // when
        TableName forTable = new TableName( "test.Student_Course" );
        Collection<Table> metadata = tableMetadataProducer.createMetadataFor( forTable );

        // then
        Table table = metadata.stream().findFirst().get();

        assertEquals( forTable, table.name() );
        assertEquals( "test.Student_Course", table.descriptor() );

        assertThat( table.columns(), contains(
                columnUtil.column( forTable, "credits", ColumnType.Data ) ) );
    }

    @Test
    public void tableMetadataShouldAddCompositeKeyColumnForTablesWithMultiplePrimaryKeys() throws Exception
    {
        // given
        QueryResults results = StubQueryResults.builder()
                .columns( "COLUMN_NAME", "DATA_TYPE", "COLUMN_TYPE" )
                .addRow( "first_name", "VARCHAR", "PrimaryKey" )
                .addRow( "last_name", "VARCHAR", "PrimaryKey" )
                .build();

        DatabaseClient databaseClient = mock( DatabaseClient.class );
        when( databaseClient.executeQuery( any( String.class ) ) ).thenReturn( AwaitHandle.forReturnValue( results ) );

        TableMetadataProducer tableMetadataProducer = new TableMetadataProducer( databaseClient );

        // when
        TableName forTable = new TableName( "test.Author" );
        Collection<Table> metadata = tableMetadataProducer.createMetadataFor( forTable );

        // then
        Table table = metadata.stream().findFirst().get();

        assertEquals( forTable, table.name() );
        assertEquals( "test.Author", table.descriptor() );

        assertThat( table.columns(), contains(
                new CompositeColumn(
                        forTable,
                        asList(
                                new SimpleColumn( forTable, "first_name", ColumnType.PrimaryKey, SqlDataType.VARCHAR ),
                                new SimpleColumn( forTable, "last_name", ColumnType.PrimaryKey, SqlDataType.VARCHAR )
                        ) ) ) );
    }
}

