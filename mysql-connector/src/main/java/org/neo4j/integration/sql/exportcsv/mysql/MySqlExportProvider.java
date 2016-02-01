package org.neo4j.integration.sql.exportcsv.mysql;

import org.neo4j.integration.neo4j.importcsv.HeaderFileWriter;
import org.neo4j.integration.neo4j.importcsv.config.GraphDataConfig;
import org.neo4j.integration.sql.SqlRunner;
import org.neo4j.integration.sql.exportcsv.DatabaseExportProvider;
import org.neo4j.integration.sql.exportcsv.ExportFileWriter;
import org.neo4j.integration.sql.exportcsv.ExportToCsvResult;
import org.neo4j.integration.sql.exportcsv.config.ExportToCsvConfig;
import org.neo4j.integration.sql.metadata.DatabaseObject;
import org.neo4j.integration.sql.metadata.Join;
import org.neo4j.integration.sql.metadata.Table;

import static java.lang.String.format;

public class MySqlExportProvider implements DatabaseExportProvider
{
    @Override
    public ExportFileWriter createExportFileWriter( ExportToCsvConfig config, SqlRunner sqlRunner )
    {
        return new MySqlExportFileWriter( config, sqlRunner );
    }

    @Override
    public ExportToCsvResult exportDatabaseObject( DatabaseObject databaseObject,
                                                         HeaderFileWriter headerFileWriter,
                                                         ExportFileWriter exportFileWriter,
                                                         ExportToCsvConfig config ) throws Exception
    {
        if ( databaseObject instanceof Table )
        {
            return new ExportMySqlTable( (Table) databaseObject, headerFileWriter, exportFileWriter, config ).export();
        }
        else if ( databaseObject instanceof Join )
        {
            return new ExportMySqlJoin( (Join) databaseObject, headerFileWriter, exportFileWriter, config ).export();
        }
        else
        {
            throw new IllegalArgumentException(
                    format( "Unrecognized database object: %s", databaseObject.getClass().getSimpleName() ) );
        }
    }

}
