package org.neo4j.integration.sql.metadata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;

class KeyCollection
{
    private final Optional<Column> primaryKey;
    private final Collection<JoinKey> foreignKeys;
    private final Collection<Column> columns;

    KeyCollection( Optional<Column> primaryKey, Collection<JoinKey> foreignKeys, Collection<Column> columns )
    {
        this.primaryKey = primaryKey;
        this.foreignKeys = foreignKeys;
        this.columns = columns;
    }

    Optional<Column> primaryKey()
    {
        return primaryKey;
    }

    Collection<JoinKey> foreignKeys()
    {
        return foreignKeys;
    }

    Collection<Column> columns()
    {
        return columns;
    }

    Collection<Column> columnsLessForeignKeys()
    {
        List<String> foreignKeyNames = foreignKeys.stream()
                .map( jk -> jk.sourceColumn().name().split( CompositeColumn.SEPARATOR ) )
                .flatMap( Stream::of )
                .collect( Collectors.toList() );

        return columns.stream()
                .filter( c -> !foreignKeyNames.contains( c.name() ) )
                .collect( Collectors.toList() );
    }

    boolean representsJoinTable()
    {
        if ( foreignKeys.size() == 2 )
        {
            if ( primaryKey.isPresent() )
            {
                List<String> primaryKeyNames = new ArrayList<>(
                        asList( primaryKey.get().name().split( CompositeColumn.SEPARATOR ) ) );
                List<String> foreignKeyNames = foreignKeys.stream()
                        .flatMap( joinKey ->
                                Stream.of( joinKey.sourceColumn().name().split( CompositeColumn.SEPARATOR ) ) )
                        .collect( Collectors.toList() );

                primaryKeyNames.removeAll( foreignKeyNames );

                return primaryKeyNames.isEmpty();
            }
            else
            {
                return true;
            }
        }
        else
        {
            return false;
        }
    }
}
