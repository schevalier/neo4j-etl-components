package org.neo4j.integration.sql.exportcsv;

import org.neo4j.integration.neo4j.importcsv.io.HeaderFileWriter;
import org.neo4j.integration.sql.DatabaseClient;
import org.neo4j.integration.sql.exportcsv.io.CsvFileWriter;
import org.neo4j.integration.sql.metadata.DatabaseObject;

public interface DatabaseExportService
{
    CsvFileWriter createExportFileWriter( ExportToCsvConfig config, DatabaseClient databaseClient );

    ExportToCsvResults.ExportToCsvResult exportDatabaseObject( DatabaseObject databaseObject,
                                                  HeaderFileWriter headerFileWriter,
                                                  CsvFileWriter csvFileWriter,
                                                  ExportToCsvConfig config ) throws Exception;

}
