package org.neo4j.integration.sql.metadata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;

public class TableInfo
{
    private final TableName tableName;
    private final Optional<Column> primaryKey;
    private final Collection<JoinKey> foreignKeys;
    private final Collection<Column> columns;

    TableInfo( TableName tableName,
               Optional<Column> primaryKey,
               Collection<JoinKey> foreignKeys,
               Collection<Column> columns )
    {
        this.tableName = tableName;
        this.primaryKey = primaryKey;
        this.foreignKeys = foreignKeys;
        this.columns = columns;
    }

    public Optional<Column> primaryKey()
    {
        return primaryKey;
    }

    public Collection<JoinKey> foreignKeys()
    {
        return foreignKeys;
    }

    public boolean representsJoinTable()
    {
        if ( foreignKeys.size() == 2 )
        {
            List<String> foreignKeyNames = foreignKeyNames();

            return primaryKeyNames().stream()
                    .filter( n -> !foreignKeyNames.contains( n ) )
                    .collect( Collectors.toList() )
                    .isEmpty();
        }
        else
        {
            return false;
        }
    }

    public Table createTable( )
    {
        Table.Builder tableBuilder = Table.builder().name( tableName );

        columnsLessKeys().forEach( tableBuilder::addColumn );

        if ( primaryKey.isPresent() )
        {
            tableBuilder.addColumn( primaryKey.get() );
        }

        return tableBuilder.build();
    }

    Collection<Column> columnsLessKeys()
    {
        List<String> primaryKeyNames = primaryKeyNames();
        List<String> foreignKeyNames = foreignKeyNames();

        return columns.stream()
                .filter( c -> !primaryKeyNames.contains( c.name() ) )
                .filter( c -> !foreignKeyNames.contains( c.name() ) )
                .collect( Collectors.toList() );
    }

    private List<String> foreignKeyNames()
    {
        return foreignKeys.stream()
                .map( jk -> jk.sourceColumn().name().split( CompositeColumn.SEPARATOR ) )
                .flatMap( Stream::of )
                .collect( Collectors.toList() );
    }

    private List<String> primaryKeyNames()
    {
        return primaryKey.isPresent() ?
                new ArrayList<>( asList( primaryKey.get().name().split( CompositeColumn.SEPARATOR ) ) ) :
                Collections.emptyList();
    }
}
