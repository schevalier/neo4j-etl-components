package org.neo4j.integration.sql.exportcsv;

import org.neo4j.integration.sql.exportcsv.mapping.ColumnToCsvFieldMappings;

public interface ExportSqlSupplier
{
    String sql( ColumnToCsvFieldMappings mappings );
}
