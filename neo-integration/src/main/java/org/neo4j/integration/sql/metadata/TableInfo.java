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
    private final Optional<Column> primaryKey;
    private final Collection<JoinKey> foreignKeys;
    private final Collection<Column> columns;

    TableInfo( Optional<Column> primaryKey, Collection<JoinKey> foreignKeys, Collection<Column> columns )
    {
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

    public Collection<Column> columnsLessKeys()
    {
        List<String> keyNames = primaryKeyNames();
        List<String> foreignKeyNames = foreignKeyNames();

        return columns.stream()
                .filter( c -> !keyNames.contains( c.name() ) )
                .filter( c -> !foreignKeyNames.contains( c.name() ) )
                .collect( Collectors.toList() );
    }

    public boolean representsJoinTable()
    {
        if ( foreignKeys.size() == 2 )
        {
            List<String> primaryKeyNames = primaryKeyNames();

            primaryKeyNames.removeAll( foreignKeyNames() );

            return primaryKeyNames.isEmpty();
        }
        else
        {
            return false;
        }
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
