package org.neo4j.integration.sql.exportcsv;

import org.neo4j.integration.sql.DatabaseClient;
import org.neo4j.integration.sql.exportcsv.io.CsvFileWriter;

public interface DatabaseExportService
{
    CsvFileWriter createExportFileWriter( ExportToCsvConfig config, DatabaseClient databaseClient );

    DatabaseExportSqlSupplier sqlSupplier();
}
