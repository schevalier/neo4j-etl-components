package org.neo4j.integration.sql.exportcsv.mysql;

import java.nio.file.Path;
import java.util.Collection;

import org.neo4j.integration.neo4j.importcsv.io.HeaderFileWriter;
import org.neo4j.integration.sql.DatabaseClient;
import org.neo4j.integration.sql.exportcsv.DatabaseExportService;
import org.neo4j.integration.sql.exportcsv.ExportToCsvConfig;
import org.neo4j.integration.sql.exportcsv.ExportToCsvResults;
import org.neo4j.integration.sql.exportcsv.io.CsvFileWriter;
import org.neo4j.integration.sql.exportcsv.io.CsvFilesWriter;
import org.neo4j.integration.sql.exportcsv.mapping.JoinTableToCsvFieldMapper;
import org.neo4j.integration.sql.exportcsv.mapping.JoinToCsvFieldMapper;
import org.neo4j.integration.sql.exportcsv.mapping.TableToCsvFieldMapper;
import org.neo4j.integration.sql.metadata.DatabaseObject;
import org.neo4j.integration.sql.metadata.Join;
import org.neo4j.integration.sql.metadata.JoinTable;
import org.neo4j.integration.sql.metadata.Table;

import static java.lang.String.format;

public class MySqlExportService implements DatabaseExportService
{

    private final MySqlExportSqlSupplier sqlSupplier;

    public MySqlExportService()
    {
        sqlSupplier = new MySqlExportSqlSupplier();
    }

    @Override
    public CsvFileWriter createExportFileWriter( ExportToCsvConfig config, DatabaseClient databaseClient )
    {
        return new CsvFileWriter( config, databaseClient );
    }

    @Override
    public ExportToCsvResults.ExportToCsvResult exportDatabaseObjectToCsv( DatabaseObject databaseObject,
                                                                           HeaderFileWriter headerFileWriter,
                                                                           CsvFileWriter csvFileWriter,
                                                                           ExportToCsvConfig config ) throws Exception
    {
        if ( databaseObject instanceof Table )
        {
            Table table = (Table) databaseObject;

            Collection<Path> files = new CsvFilesWriter<Table>( headerFileWriter, csvFileWriter )
                    .write( table, new TableToCsvFieldMapper( config.formatting() ), sqlSupplier );

            return new ExportToCsvResults.ExportToCsvResult( table, files );
        }
        else if ( databaseObject instanceof Join )
        {
            Join join = (Join) databaseObject;

            Collection<Path> files = new CsvFilesWriter<Join>( headerFileWriter, csvFileWriter )
                    .write( join, new JoinToCsvFieldMapper( config.formatting() ), sqlSupplier );

            return new ExportToCsvResults.ExportToCsvResult( join, files );
        }
        else if ( databaseObject instanceof JoinTable )
        {
            JoinTable joinTable = (JoinTable) databaseObject;

            Collection<Path> files = new CsvFilesWriter<JoinTable>( headerFileWriter, csvFileWriter )
                    .write( joinTable, new JoinTableToCsvFieldMapper( config.formatting() ), sqlSupplier );

            return new ExportToCsvResults.ExportToCsvResult( joinTable, files );
        }
        else
        {
            throw new IllegalArgumentException(
                    format( "Unrecognized database object: %s", databaseObject.getClass().getSimpleName() ) );
        }
    }
}
