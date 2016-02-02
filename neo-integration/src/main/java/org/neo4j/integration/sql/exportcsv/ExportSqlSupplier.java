package org.neo4j.integration.sql.exportcsv;

import java.nio.file.Path;

import org.neo4j.integration.sql.exportcsv.mapping.ColumnToCsvFieldMappings;

public interface ExportSqlSupplier
{
    String sql( ColumnToCsvFieldMappings mappings, Path exportFile );
}
