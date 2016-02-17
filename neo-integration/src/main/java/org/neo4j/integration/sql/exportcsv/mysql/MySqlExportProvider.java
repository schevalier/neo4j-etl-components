package org.neo4j.integration.sql.exportcsv.mysql;

import java.nio.file.Path;
import java.util.Collection;

import org.neo4j.integration.neo4j.importcsv.io.HeaderFileWriter;
import org.neo4j.integration.sql.DatabaseClient;
import org.neo4j.integration.sql.exportcsv.DatabaseExportProvider;
import org.neo4j.integration.sql.exportcsv.ExportToCsvResult;
import org.neo4j.integration.sql.exportcsv.config.ExportToCsvConfig;
import org.neo4j.integration.sql.exportcsv.io.CsvFileWriter;
import org.neo4j.integration.sql.exportcsv.io.CsvFilesWriter;
import org.neo4j.integration.sql.exportcsv.mapping.JoinMapper;
import org.neo4j.integration.sql.exportcsv.mapping.TableMapper;
import org.neo4j.integration.sql.metadata.DatabaseObject;
import org.neo4j.integration.sql.metadata.Join;
import org.neo4j.integration.sql.metadata.Table;

import static java.lang.String.format;

public class MySqlExportProvider implements DatabaseExportProvider
{
    @Override
    public CsvFileWriter createExportFileWriter( ExportToCsvConfig config, DatabaseClient databaseClient )
    {
        return new CsvFileWriter( config, databaseClient );
    }

    @Override
    public ExportToCsvResult exportDatabaseObject( DatabaseObject databaseObject,
                                                   HeaderFileWriter headerFileWriter,
                                                   CsvFileWriter csvFileWriter,
                                                   ExportToCsvConfig config ) throws Exception
    {
        if ( databaseObject instanceof Table )
        {
            Table table = (Table) databaseObject;

            Collection<Path> files = new CsvFilesWriter<Table>( headerFileWriter, csvFileWriter )
                    .write( table, new TableMapper( config.formatting() ), new MySqlExportSqlSupplier() );

            return new ExportToCsvResult( table, files );
        }
        else if ( databaseObject instanceof Join )
        {
            Join join = (Join) databaseObject;

            Collection<Path> files = new CsvFilesWriter<Join>( headerFileWriter, csvFileWriter )
                    .write( join, new JoinMapper( config.formatting() ), new MySqlExportSqlSupplier() );

            return new ExportToCsvResult( join, files );
        }
        else
        {
            throw new IllegalArgumentException(
                    format( "Unrecognized database object: %s", databaseObject.getClass().getSimpleName() ) );
        }
    }
}
