package org.neo4j.integration.sql.exportcsv.mysql;

import java.util.stream.Collectors;

import org.neo4j.integration.sql.exportcsv.DatabaseExportSqlSupplier;
import org.neo4j.integration.sql.exportcsv.mapping.ColumnToCsvFieldMappings;
import org.neo4j.integration.sql.metadata.Column;

public class MySqlExportSqlSupplier implements DatabaseExportSqlSupplier
{
    @Override
    public String sql( ColumnToCsvFieldMappings mappings )
    {
        return "SELECT " +
                mappings.columns().stream().map( Column::aliasedColumn ).collect( Collectors.joining( ", " ) ) +
                " FROM " + mappings.tableNames().stream().collect( Collectors.joining( ", " ) );
    }
}
