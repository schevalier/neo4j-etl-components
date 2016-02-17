package org.neo4j.integration.sql.exportcsv.mysql;

import org.neo4j.integration.sql.exportcsv.ExportSqlSupplier;
import org.neo4j.integration.sql.exportcsv.mapping.ColumnToCsvFieldMappings;

import static org.neo4j.integration.util.StringListBuilder.stringList;

public class MySqlExportSqlSupplier implements ExportSqlSupplier
{
    @Override
    public String sql( ColumnToCsvFieldMappings mappings )
    {
        return "SELECT " +
                stringList( mappings.aliasedColumns(), ", " ) +
                " FROM " + stringList( mappings.tableNames(), ", " );
    }
}
