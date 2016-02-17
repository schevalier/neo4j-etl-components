package org.neo4j.integration.sql.exportcsv;

import org.neo4j.integration.sql.exportcsv.mapping.ColumnToCsvFieldMappings;

public interface DatabaseExportSqlSupplier
{
    String sql( ColumnToCsvFieldMappings mappings );
}
