package org.neo4j.integration.sql.metadata;

public interface Column
{
    TableName table();

    // Fully-qualified column name, or literal value
    String name();

    // Column alias
    String alias();

    ColumnType type();

    SqlDataType dataType();
}
