package org.neo4j.integration.sql.metadata;

import com.fasterxml.jackson.databind.JsonNode;

import org.neo4j.integration.sql.RowAccessor;
import org.neo4j.integration.sql.exportcsv.io.TinyIntResolver;
import org.neo4j.integration.sql.exportcsv.mapping.ColumnToCsvFieldMappings;

import static java.lang.String.format;

public interface Column
{
    static Column fromJson( JsonNode root )
    {
        String type = root.path( "type" ).textValue();

        if ( type.equalsIgnoreCase( SimpleColumn.class.getSimpleName() ) )
        {
            return SimpleColumn.fromJson( root );
        }
        else if ( type.equalsIgnoreCase( CompositeColumn.class.getSimpleName() ) )
        {
            return CompositeColumn.fromJson( root );
        }
        else
        {
            throw new IllegalStateException( format( "Unrecognized column type: '%s'", type ) );
        }
    }

    TableName table();

    // Fully-qualified column name, or literal value
    String name();

    // Column alias
    String alias();

    ColumnRole role();

    SqlDataType sqlDataType();

    boolean allowAddToSelectStatement();

    String selectFrom( RowAccessor row, int rowIndex );

    String aliasedColumn();

    void addData( ColumnToCsvFieldMappings.Builder builder, TinyIntResolver tinyIntResolver );

    JsonNode toJson();

    boolean useQuotes();
}
