package org.neo4j.integration.mysql.exportcsv.sql;

import java.nio.file.Path;

import org.neo4j.integration.mysql.exportcsv.mapping.ColumnToCsvFieldMappings;

public interface ExportSqlSupplier
{
    String sql( ColumnToCsvFieldMappings mappings, Path exportFile );
}
