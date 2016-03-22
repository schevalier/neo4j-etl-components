package org.neo4j.integration.sql.exportcsv.mysql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.neo4j.integration.sql.exportcsv.DatabaseExportSqlSupplier;
import org.neo4j.integration.sql.exportcsv.mapping.ColumnToCsvFieldMappings;
import org.neo4j.integration.sql.metadata.Column;
import org.neo4j.integration.sql.metadata.ColumnType;
import org.neo4j.integration.sql.metadata.CompositeKeyColumn;

import static java.lang.String.format;

public class MySqlExportSqlSupplier implements DatabaseExportSqlSupplier
{
    @Override
    public String sql( ColumnToCsvFieldMappings mappings )
    {
        Collection<String> strings = newSql( mappings );
        return "SELECT " +
                strings.stream().collect( Collectors.joining( ", " ) ) +
                " FROM " + mappings.tableNames().stream().collect( Collectors.joining( ", " ) );
    }

    private Collection<String> newSql( ColumnToCsvFieldMappings mappings )
    {
        Collection<Column> columns = mappings.columns();
        ArrayList<String> aliasedColumnsSql = new ArrayList<>();
        Map<ColumnType, List<Column>> groupByColumnType = columns.stream()
                .collect( Collectors.groupingBy( Column::type ) );
        for ( Map.Entry<ColumnType, List<Column>> columnTypeListEntry : groupByColumnType.entrySet() )
        {
            if ( columnTypeListEntry.getKey().equals( ColumnType.CompositeKey ) )
            {
                ArrayList<String> strings = buildCompositeKeyQuery( columnTypeListEntry );
                aliasedColumnsSql.addAll( strings );
            }
            else
            {
                ArrayList<String> strings = buildNonCompositeKey( columnTypeListEntry );
                aliasedColumnsSql.addAll( strings );
            }
        }
        return aliasedColumnsSql;
    }

    private ArrayList<String> buildNonCompositeKey( Map.Entry<ColumnType, List<Column>> columnTypeListEntry )
    {
        ArrayList<String> aliasedColumnsSql = new ArrayList<>();

        List<Column> value = columnTypeListEntry.getValue();
        value.stream()
                .forEach( c -> aliasedColumnsSql.add( format( "%s AS %s", c.name(), c.alias() ) ) );
        return aliasedColumnsSql;
    }

    private ArrayList<String> buildCompositeKeyQuery( Map.Entry<ColumnType, List<Column>>
                                                              columnTypeListEntry )
    {
        ArrayList<String> aliasedColumnsSql = new ArrayList<>();

        CompositeKeyColumn value = (CompositeKeyColumn) columnTypeListEntry.getValue().get( 0 );
        for ( Column column : value.columns() )
        {
            aliasedColumnsSql.add( format( "%s AS %s", column.name(), column.alias() ) );
        }
        return aliasedColumnsSql;

    }
}
