package org.neo4j.integration.sql.exportcsv.mysql;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;

import org.neo4j.integration.io.AwaitHandle;
import org.neo4j.integration.neo4j.importcsv.config.Formatting;
import org.neo4j.integration.sql.Results;
import org.neo4j.integration.sql.DatabaseClient;
import org.neo4j.integration.sql.StubResults;
import org.neo4j.integration.sql.exportcsv.ExportSqlSupplier;
import org.neo4j.integration.sql.exportcsv.config.ExportToCsvConfig;
import org.neo4j.integration.sql.exportcsv.io.CsvFileWriter;
import org.neo4j.integration.sql.exportcsv.mapping.ColumnToCsvFieldMappings;
import org.neo4j.integration.sql.metadata.Column;
import org.neo4j.integration.sql.metadata.ColumnType;
import org.neo4j.integration.sql.ConnectionConfig;
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

    @Test
    public void shouldCreateCsvFile() throws Exception
    {
        // given
        TableName table = new TableName( "users" );

        // setup sql runner
        Results results = StubResults.builder()
                .columns( "id", "username" )
                .addRow( "1", "user-1" )
                .addRow( "2", "user-2" )
                .build();

        DatabaseClient databaseClient = mock( DatabaseClient.class );
        when( databaseClient.execute( any() ) ).thenReturn( AwaitHandle.forReturnValue( results ) );

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
                        Column.builder()
                                .table( table )
                                .name( "id" )
                                .alias( "id" )
                                .type( ColumnType.PrimaryKey )
                                .build(),
                        Column.builder()
                                .table( table )
                                .name( "username" )
                                .alias( "username" )
                                .type( ColumnType.PrimaryKey )
                                .build() ) );

        // create writer under test
        CsvFileWriter writer = new CsvFileWriter( config, databaseClient );

        // when
        Path exportFile = writer.writeExportFile( mappings, mock( ExportSqlSupplier.class ), table.fullName() );

        // then
        List<String> contents = Files.readAllLines( exportFile );
        assertEquals( contents, asList( "1,user-1", "2,user-2" ) );
    }
}
