package org.neo4j.integration.sql.exportcsv.mysql;

import java.nio.file.Path;
import java.util.Collection;

import org.neo4j.integration.neo4j.importcsv.io.HeaderFileWriter;
import org.neo4j.integration.sql.DatabaseClient;
import org.neo4j.integration.sql.exportcsv.DatabaseExportService;
import org.neo4j.integration.sql.exportcsv.ExportToCsvResults;
import org.neo4j.integration.sql.exportcsv.ExportToCsvConfig;
import org.neo4j.integration.sql.exportcsv.io.CsvFileWriter;
import org.neo4j.integration.sql.exportcsv.io.CsvFilesWriter;
import org.neo4j.integration.sql.exportcsv.mapping.JoinToCsvFieldMapper;
import org.neo4j.integration.sql.exportcsv.mapping.TableToCsvFieldMapper;
import org.neo4j.integration.sql.metadata.DatabaseObject;
import org.neo4j.integration.sql.metadata.Join;
import org.neo4j.integration.sql.metadata.Table;

import static java.lang.String.format;

public class MySqlExportService implements DatabaseExportService
{
    @Override
    public CsvFileWriter createExportFileWriter( ExportToCsvConfig config, DatabaseClient databaseClient )
    {
        return new CsvFileWriter( config, databaseClient );
    }

    @Override
    public ExportToCsvResults.ExportToCsvResult exportDatabaseObject( DatabaseObject databaseObject,
                                                   HeaderFileWriter headerFileWriter,
                                                   CsvFileWriter csvFileWriter,
                                                   ExportToCsvConfig config ) throws Exception
    {
        if ( databaseObject instanceof Table )
        {
            Table table = (Table) databaseObject;

            Collection<Path> files = new CsvFilesWriter<Table>( headerFileWriter, csvFileWriter )
                    .write( table, new TableToCsvFieldMapper( config.formatting() ), new MySqlExportSqlSupplier() );

            return new ExportToCsvResults.ExportToCsvResult( table, files );
        }
        else if ( databaseObject instanceof Join )
        {
            Join join = (Join) databaseObject;

            Collection<Path> files = new CsvFilesWriter<Join>( headerFileWriter, csvFileWriter )
                    .write( join, new JoinToCsvFieldMapper( config.formatting() ), new MySqlExportSqlSupplier() );

            return new ExportToCsvResults.ExportToCsvResult( join, files );
        }
        else
        {
            throw new IllegalArgumentException(
                    format( "Unrecognized database object: %s", databaseObject.getClass().getSimpleName() ) );
        }
    }
}
