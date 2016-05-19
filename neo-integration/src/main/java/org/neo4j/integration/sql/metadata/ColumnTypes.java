package org.neo4j.integration.sql.metadata;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;
import java.util.stream.Collectors;

class ColumnTypes
{
    private final Map<String, String> columnTypes;

    ColumnTypes( Map<String, String> columnTypes )
    {
        this.columnTypes = columnTypes;
    }

    SqlDataType getSqlDataType( String column )
    {
        return SqlDataType.parse( columnTypes.get( column ) );
    }

    Collection<Column> toColumns( TableName table )
    {
        return columnTypes.entrySet().stream()
                .map( e -> new SimpleColumn(
                        table,
                        e.getKey(),
                        EnumSet.of(ColumnRole.Data),
                        SqlDataType.parse( e.getValue() ) ) )
                .filter( c -> !c.sqlDataType().skipImport() )
                .collect( Collectors.toList() );
    }

}
