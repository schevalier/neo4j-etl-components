package org.neo4j.integration.sql.exportcsv;

import java.nio.file.Path;

import org.neo4j.integration.sql.exportcsv.mapping.ColumnToCsvFieldMappings;

public interface ExportFileWriter
{
    Path writeExportFile( ColumnToCsvFieldMappings mappings,
                          ExportSqlSupplier sqlSupplier,
                          String filenamePrefix ) throws Exception;
}
