package org.neo4j.integration.sql.exportcsv;

import org.neo4j.integration.neo4j.importcsv.io.HeaderFileWriter;
import org.neo4j.integration.sql.DatabaseClient;
import org.neo4j.integration.sql.exportcsv.config.ExportToCsvConfig;
import org.neo4j.integration.sql.exportcsv.io.CsvFileWriter;
import org.neo4j.integration.sql.metadata.DatabaseObject;

public interface DatabaseExportProvider
{
    CsvFileWriter createExportFileWriter( ExportToCsvConfig config, DatabaseClient databaseClient );

    ExportToCsvResult exportDatabaseObject( DatabaseObject databaseObject,
                                                  HeaderFileWriter headerFileWriter,
                                                  CsvFileWriter csvFileWriter,
                                                  ExportToCsvConfig config ) throws Exception;

}
