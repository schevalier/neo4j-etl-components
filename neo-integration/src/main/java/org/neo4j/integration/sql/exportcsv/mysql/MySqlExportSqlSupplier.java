package org.neo4j.integration.sql.exportcsv.mysql;

import org.neo4j.integration.sql.exportcsv.DatabaseExportSqlSupplier;
import org.neo4j.integration.sql.exportcsv.mapping.ColumnToCsvFieldMappings;

import static org.neo4j.integration.util.StringListBuilder.stringList;

public class MySqlExportSqlSupplier implements DatabaseExportSqlSupplier
{
    @Override
    public String sql( ColumnToCsvFieldMappings mappings )
    {
        return "SELECT " +
                stringList( mappings.aliasedColumns(), ", " ) +
                " FROM " + stringList( mappings.tableNames(), ", " );
    }
}
