package org.neo4j.integration.sql.exportcsv;

import java.nio.file.Path;
import java.util.Collection;

import org.neo4j.integration.neo4j.importcsv.io.HeaderFileWriter;
import org.neo4j.integration.sql.exportcsv.io.CsvFileWriter;

public interface DatabaseObjectToCsvFilesService
{
    Collection<Path> exportToCsv( DatabaseExportSqlSupplier sqlSupplier,
                                  HeaderFileWriter headerFileWriter,
                                  CsvFileWriter csvFileWriter,
                                  ExportToCsvConfig config ) throws Exception;
}
