package org.neo4j.integration.sql.exportcsv.mysql;

import java.nio.file.Path;

import org.neo4j.integration.sql.SqlRunner;
import org.neo4j.integration.sql.exportcsv.ExportFileWriter;
import org.neo4j.integration.sql.exportcsv.ExportSqlSupplier;
import org.neo4j.integration.sql.exportcsv.config.ExportToCsvConfig;
import org.neo4j.integration.sql.exportcsv.mapping.ColumnToCsvFieldMappings;

import static java.lang.String.format;

public class MySqlExportFileWriter implements ExportFileWriter
{
    private final ExportToCsvConfig config;
    private final SqlRunner sqlRunner;

    public MySqlExportFileWriter( ExportToCsvConfig config, SqlRunner sqlRunner )
    {
        this.config = config;
        this.sqlRunner = sqlRunner;
    }

    @Override
    public Path writeExportFile( ColumnToCsvFieldMappings mappings,
                                 ExportSqlSupplier sqlSupplier,
                                 String filenamePrefix ) throws Exception
    {
        Path exportFile = config.destination().resolve( format( "%s.csv", filenamePrefix ) );
        sqlRunner.execute( sqlSupplier.sql( mappings, exportFile ) ).await();

        return exportFile;
    }
}
