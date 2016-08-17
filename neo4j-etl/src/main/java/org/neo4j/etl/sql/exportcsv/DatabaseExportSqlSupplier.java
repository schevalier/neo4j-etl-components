package org.neo4j.etl.sql.exportcsv;

import org.neo4j.etl.sql.exportcsv.mapping.ColumnToCsvFieldMappings;

public interface DatabaseExportSqlSupplier
{
    String sql( ColumnToCsvFieldMappings mappings );
}
