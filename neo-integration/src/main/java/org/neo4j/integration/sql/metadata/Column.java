package org.neo4j.integration.sql.metadata;

import com.fasterxml.jackson.databind.JsonNode;

import org.neo4j.integration.neo4j.importcsv.config.Formatter;
import org.neo4j.integration.neo4j.importcsv.fields.Neo4jDataType;
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

    SqlDataType sqlDataType();

    String selectFrom( RowAccessor row );

    String aliasedColumn();

    void addTo( ColumnToCsvFieldMappings.Builder builder, Formatter formatter );

    JsonNode toJson();
}
