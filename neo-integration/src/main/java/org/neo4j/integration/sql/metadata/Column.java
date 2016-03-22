package org.neo4j.integration.sql.metadata;

import org.neo4j.integration.sql.RowAccessor;

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
}
