package org.neo4j.integration.sql.exportcsv.mysql;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import org.neo4j.integration.io.AwaitHandle;
import org.neo4j.integration.neo4j.importcsv.config.Formatting;
import org.neo4j.integration.sql.SqlRunner;
import org.neo4j.integration.sql.exportcsv.ExportSqlSupplier;
import org.neo4j.integration.sql.exportcsv.io.CsvFileWriter;
import org.neo4j.integration.sql.exportcsv.config.ExportToCsvConfig;
import org.neo4j.integration.sql.exportcsv.mapping.ColumnToCsvFieldMappings;
import org.neo4j.integration.sql.metadata.ConnectionConfig;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CsvFileWriterTest
{
    @Test
    public void shouldCreateExportFilePathAndPassItToSqlSupplier() throws Exception
    {
        // given
        String sql = "SELECT ...";
        Path expectedPath = Paths.get( "/test/movies.csv" );

        SqlRunner sqlRunner = mock( SqlRunner.class );
        when( sqlRunner.execute( sql ) ).thenReturn( AwaitHandle.noOp() );

        ColumnToCsvFieldMappings mappings = mock( ColumnToCsvFieldMappings.class );

        ExportSqlSupplier sqlSupplier = mock( ExportSqlSupplier.class );
        when( sqlSupplier.sql( mappings, expectedPath ) ).thenReturn( sql );

        ExportToCsvConfig config = ExportToCsvConfig.builder()
                .destination( Paths.get( "/test" ) )
                .connectionConfig( mock( ConnectionConfig.class ) )
                .formatting( Formatting.DEFAULT )
                .build();

        CsvFileWriter writer = new CsvFileWriter( config, sqlRunner );

        // when
        Path exportFile = writer.writeExportFile( mappings, sqlSupplier, "movies" );

        // then
        verify( sqlSupplier ).sql( mappings, expectedPath );
        assertEquals( expectedPath, exportFile );
    }
}
