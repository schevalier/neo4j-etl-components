package org.neo4j.integration.mysql.exportcsvnew.config;

import org.neo4j.integration.mysql.exportcsvnew.metadata.Join;
import org.neo4j.integration.neo4j.importcsv.config.QuoteChar;

public interface Mapper<T>
{
    ColumnToCsvFieldMappings createExportCsvConfigFor( T source, QuoteChar quote );
}
