package org.neo4j.integration.sql.exportcsv.mysql;

import java.util.stream.Collectors;

import org.neo4j.integration.sql.exportcsv.DatabaseExportSqlSupplier;
import org.neo4j.integration.sql.exportcsv.mapping.ColumnToCsvFieldMappings;

public class MySqlExportSqlSupplier implements DatabaseExportSqlSupplier
{
    @Override
    public String sql( ColumnToCsvFieldMappings mappings )
    {
        return "SELECT " +
                mappings.aliasedColumns().stream().collect( Collectors.joining(", " )) +
                " FROM " + mappings.tableNames().stream().collect( Collectors.joining(", " ));
    }
}
