package org.neo4j.integration.sql.exportcsv.io;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;

import org.neo4j.integration.io.AwaitHandle;
import org.neo4j.integration.neo4j.importcsv.config.GraphObjectType;
import org.neo4j.integration.neo4j.importcsv.config.formatting.Formatting;
import org.neo4j.integration.sql.ConnectionConfig;
import org.neo4j.integration.sql.DatabaseClient;
import org.neo4j.integration.sql.QueryResults;
import org.neo4j.integration.sql.StubQueryResults;
import org.neo4j.integration.sql.exportcsv.ColumnUtil;
import org.neo4j.integration.sql.exportcsv.ExportToCsvConfig;
import org.neo4j.integration.sql.exportcsv.mapping.ColumnToCsvFieldMappings;
import org.neo4j.integration.sql.exportcsv.mapping.MetadataMapping;
import org.neo4j.integration.sql.exportcsv.mapping.TinyIntAs;
import org.neo4j.integration.sql.metadata.ColumnRole;
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
    public void shouldCreateCsvFileAndWriteResultsToIt() throws Exception
    {
        // given

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

        TableName table = new TableName( "users" );
        ColumnToCsvFieldMappings mappings = mock( ColumnToCsvFieldMappings.class );
        when( mappings.columns() ).thenReturn(
                asList(
                        columnUtil.keyColumn( table, "id", ColumnRole.PrimaryKey ),
                        columnUtil.column( table, "username", ColumnRole.Data ) ) );

        MetadataMapping resource = new MetadataMapping( table.fullName(), GraphObjectType.Node, "SELECT ...",
                mappings );

        // when
        CsvFileWriter writer = new CsvFileWriter( config, databaseClient, new TinyIntResolver( TinyIntAs.BYTE ) );
        Path exportFile = writer.writeExportFile( resource );

        // then
        List<String> contents = Files.readAllLines( exportFile );
        assertEquals( asList( "\"1\",\"user-1\"", "\"2\",\"user-2\"" ), contents );
    }
}
