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

    public Table createTable()
    {
        Table.Builder tableBuilder = Table.builder().name( tableName );

        columns().forEach( tableBuilder::addColumn );

        return tableBuilder.build();
    }

    public JoinTable createJoinTable()
    {
        if ( !representsJoinTable() )
        {
            throw new IllegalStateException( "TableInfo does not represent a join table" );
        }

        List<JoinKey> joinKeys = foreignKeys().stream()
                .sorted( ( o1, o2 ) -> o1.sourceColumn().name().compareTo( o2.sourceColumn().name() ) )
                .collect( Collectors.toList() );
        return new JoinTable( new Join( joinKeys.get( 0 ), joinKeys.get( 1 ) ), createTable() );
    }

    public Collection<Join> createJoins()
    {
        if ( representsJoinTable() )
        {
            throw new IllegalStateException( "TableInfo represents a join table" );
        }

        if ( !primaryKey.isPresent() && !foreignKeys.isEmpty() )
        {
            throw new IllegalStateException( "Unsupported: foreign key in a table that has no primary key, " +
                    "and which is not a join table." );
        }

        Column primaryKeyColumn = primaryKey.get();

        return foreignKeys.stream()
                .map( fk -> fk.createJoinForCollocatedPrimaryKey( primaryKeyColumn ) )
                .collect( Collectors.toList() );
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
        List<String> primaryKeyNames = primaryKeyNames();
        List<String> foreignKeyNames = foreignKeyNames();

        Stream<Column> columnStream = columns.stream()
                .filter( c -> !primaryKeyNames.contains( c.name() ) )
                .filter( c -> !foreignKeyNames.contains( c.name() ) );

        if ( primaryKey.isPresent() )
        {
            return Stream.concat( Stream.of( primaryKey.get() ), columnStream ).collect( Collectors.toList() );
        }
        else
        {
            return columnStream.collect( Collectors.toList() );
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
