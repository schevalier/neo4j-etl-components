package org.neo4j.integration.sql.exportcsv.io;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;

import org.neo4j.integration.io.AwaitHandle;
import org.neo4j.integration.neo4j.importcsv.config.Delimiter;
import org.neo4j.integration.neo4j.importcsv.config.Formatting;
import org.neo4j.integration.sql.ConnectionConfig;
import org.neo4j.integration.sql.DatabaseClient;
import org.neo4j.integration.sql.QueryResults;
import org.neo4j.integration.sql.StubQueryResults;
import org.neo4j.integration.sql.exportcsv.ColumnUtil;
import org.neo4j.integration.sql.exportcsv.DatabaseExportSqlSupplier;
import org.neo4j.integration.sql.exportcsv.ExportToCsvConfig;
import org.neo4j.integration.sql.exportcsv.mapping.ColumnToCsvFieldMappings;
import org.neo4j.integration.sql.metadata.ColumnType;
import org.neo4j.integration.sql.metadata.TableName;
import org.neo4j.integration.util.ResourceRule;
import org.neo4j.integration.util.TemporaryDirectory;

import static java.util.Arrays.asList;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CsvFileWriterTest
{
    @Rule
    public final ResourceRule<Path> tempDirectory = new ResourceRule<>( TemporaryDirectory.temporaryDirectory() );
    private ColumnUtil columnUtil = new ColumnUtil();

    @Test
    public void shouldCreateCsvFile() throws Exception
    {
        // given
        TableName table = new TableName( "users" );

        // setup sql runner
        QueryResults results = StubQueryResults.builder()
                .columns( "id", "username" )
                .addRow( "1", "user-1" )
                .addRow( "2", "user-2" )
                .build();

        DatabaseClient databaseClient = mock( DatabaseClient.class );
        when( databaseClient.executeQuery( any() ) ).thenReturn( AwaitHandle.forReturnValue( results ) );

        // setup config
        ExportToCsvConfig config = ExportToCsvConfig.builder()
                .destination( tempDirectory.get() )
                .connectionConfig( mock( ConnectionConfig.class ) )
                .formatting( Formatting.DEFAULT )
                .build();

        // return columns from mappings
        ColumnToCsvFieldMappings mappings = mock( ColumnToCsvFieldMappings.class );

        when( mappings.columns() ).thenReturn(
                asList(
                        columnUtil.keyColumn( table, "id", ColumnType.PrimaryKey ),
                        columnUtil.column( table, "username", ColumnType.Data ) ) );


        // create writer under test
        CsvFileWriter writer = new CsvFileWriter( config, databaseClient );

        // when
        Path exportFile = writer.writeExportFile( mappings, mock( DatabaseExportSqlSupplier.class ),
                table.fullName(), new WriteRowWithNullsStrategy() );

        // then
        List<String> contents = Files.readAllLines( exportFile );
        assertEquals( asList( "1,user-1", "2,user-2" ), contents );
    }

    @Test
    public void shouldWriteDelimiterForNullOrEmptyValues() throws Exception
    {
        // given
        TableName table = new TableName( "users" );

        // setup sql runner
        QueryResults results = StubQueryResults.builder()
                .columns( "id", "username", "age" )
                .addRow( "1", "user-1", "45" )
                .addRow( "2", "", "32" )
                .addRow( "3", "user-3", null )
                .addRow( "4", "user-4", "" )
                .build();

        DatabaseClient databaseClient = mock( DatabaseClient.class );
        when( databaseClient.executeQuery( any() ) ).thenReturn( AwaitHandle.forReturnValue( results ) );

        // setup config
        ExportToCsvConfig config = ExportToCsvConfig.builder()
                .destination( tempDirectory.get() )
                .connectionConfig( mock( ConnectionConfig.class ) )
                .formatting( Formatting.builder().delimiter( Delimiter.TAB ).build() )
                .build();

        // return columns from mappings
        ColumnToCsvFieldMappings mappings = mock( ColumnToCsvFieldMappings.class );

        when( mappings.columns() ).thenReturn(
                asList(
                        columnUtil.keyColumn( table, "id", ColumnType.PrimaryKey ),
                        columnUtil.column( table, "username", ColumnType.Data ),
                        columnUtil.column( table, "age", ColumnType.Data ) ) );

        // create writer under test
        CsvFileWriter writer = new CsvFileWriter( config, databaseClient );

        // when
        Path exportFile = writer.writeExportFile( mappings, mock( DatabaseExportSqlSupplier.class ), table.fullName()
                , new WriteRowWithNullsStrategy() );

        // then
        List<String> contents = Files.readAllLines( exportFile );
        assertEquals( asList( "1\tuser-1\t45", "2\t\t32", "3\tuser-3\t", "4\tuser-4\t" ), contents );
    }

    @Test
    public void shouldWriteSkipWritingRowForRelationshipsIfAnyColumnHasNullOrEmptyValues() throws Exception
    {
        // given
        TableName table = new TableName( "users" );

        // setup sql runner
        QueryResults results = StubQueryResults.builder()
                .columns( "id", "username" )
                .addRow( "1", "user-1" )
                .addRow( "2", "" )
                .addRow( "3", null )
                .addRow( "", "user-4" )
                .build();

        DatabaseClient databaseClient = mock( DatabaseClient.class );
        when( databaseClient.executeQuery( any() ) ).thenReturn( AwaitHandle.forReturnValue( results ) );

        // setup config
        ExportToCsvConfig config = ExportToCsvConfig.builder()
                .destination( tempDirectory.get() )
                .connectionConfig( mock( ConnectionConfig.class ) )
                .formatting( Formatting.builder().delimiter( Delimiter.TAB ).build() )
                .build();

        // return columns from mappings
        ColumnToCsvFieldMappings mappings = mock( ColumnToCsvFieldMappings.class );

        when( mappings.columns() ).thenReturn(
                asList(
                        columnUtil.keyColumn( table, "id", ColumnType.PrimaryKey ),
                        columnUtil.column( table, "username", ColumnType.Data ) ) );

        // create writer under test
        CsvFileWriter writer = new CsvFileWriter( config, databaseClient );

        // when
        Path exportFile = writer.writeExportFile(
                mappings,
                mock( DatabaseExportSqlSupplier.class ),
                table.fullName(),
                new WriteRowWithNullsStrategy() );

        // then
        List<String> contents = Files.readAllLines( exportFile );
        assertEquals( asList( "1\tuser-1", "2\t", "3\t" ), contents );
    }
}
