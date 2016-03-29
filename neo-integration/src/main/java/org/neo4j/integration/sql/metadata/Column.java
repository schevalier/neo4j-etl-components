package org.neo4j.integration.sql.metadata;

import org.neo4j.integration.neo4j.importcsv.config.Formatter;
import org.neo4j.integration.sql.RowAccessor;
import org.neo4j.integration.sql.exportcsv.mapping.ColumnToCsvFieldMappings;

public interface Column
{
    TableName table();

    // Fully-qualified column name, or literal value
    String name();

    // Column alias
    String alias();

    ColumnType type();

    SqlDataType dataType();

    String selectFrom( RowAccessor row );

    String aliasedColumn();

    void addTo( ColumnToCsvFieldMappings.Builder builder, Formatter formatter );
}
