package org.neo4j.integration.sql.exportcsv;

import org.neo4j.integration.neo4j.importcsv.HeaderFileWriter;
import org.neo4j.integration.neo4j.importcsv.config.GraphDataConfig;
import org.neo4j.integration.sql.SqlRunner;
import org.neo4j.integration.sql.exportcsv.config.ExportToCsvConfig;
import org.neo4j.integration.sql.metadata.DatabaseObject;

public interface DatabaseExportProvider
{
    ExportFileWriter createExportFileWriter( ExportToCsvConfig config, SqlRunner sqlRunner );

    ExportToCsvResult exportDatabaseObject( DatabaseObject databaseObject,
                                                  HeaderFileWriter headerFileWriter,
                                                  ExportFileWriter exportFileWriter,
                                                  ExportToCsvConfig config ) throws Exception;

}
