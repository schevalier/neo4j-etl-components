package org.neo4j.integration.sql.metadata;

import java.util.Collection;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import org.neo4j.integration.util.Preconditions;

import static java.lang.String.format;
import static java.util.Arrays.asList;

public class Join implements DatabaseObject
{
    public static Builder.SetLeftSource builder()
    {
        return new JoinBuilder();
    }

    private final JoinKey left;
    private final JoinKey right;
    private final TableName startTable;

    public Join( JoinKey left, JoinKey right, TableName startTable )
    {
        this.left = Preconditions.requireNonNull( left, "Left" );
        this.right = Preconditions.requireNonNull( right, "Right" );
        this.startTable = Preconditions.requireNonNull( startTable, "StartTable" );
    }

    Join( JoinBuilder builder )
    {
        this( new JoinKey(
                        Preconditions.requireNonNull( builder.leftSource, "LeftSource" ),
                        Preconditions.requireNonNull( builder.leftTarget, "LeftTarget" ) ),
                new JoinKey(
                        Preconditions.requireNonNull( builder.rightSource, "RightSource" ),
                        Preconditions.requireNonNull( builder.rightTarget, "RightTarget" ) ),
                builder.startTable );
    }

    public boolean childTableRepresentsStartOfRelationship()
    {
        return startTable.equals( right.target().table() );
    }

    public boolean parentTableRepresentsStartOfRelationship()
    {
        return startTable.equals( left.source().table() );
    }

    public Column leftSource()
    {
        return left.source();
    }

    public Column rightSource()
    {
        return right.source();
    }

    public Column leftTarget()
    {
        return left.target();
    }

    public Column rightTarget()
    {
        return right.target();
    }

    public Collection<TableName> tableNames()
    {
        return asList( left.source().table(), right.target().table() );
    }

    @Override
    public String descriptor()
    {
        return format( "%s_%s", left.source().table().fullName(), rightTarget().table().fullName() );
    }

    @Override
    public <T> T createService( DatabaseObjectServiceProvider<T> databaseObjectServiceProvider )
    {
        return databaseObjectServiceProvider.joinService( this );
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString( this );
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals( Object o )
    {
        return EqualsBuilder.reflectionEquals( this, o );
    }

    @Override
    public int hashCode()
    {
        return HashCodeBuilder.reflectionHashCode( this );
    }

    public interface Builder
    {
        interface SetLeftSource
        {
            SetLeftTarget leftSource( TableName table, String column, ColumnType columnType );
        }

        interface SetLeftTarget
        {
            SetRightSource leftTarget( TableName table, String column, ColumnType columnType );
        }

        interface SetRightSource
        {
            SetRightTarget rightSource( TableName table, String column, ColumnType columnType );
        }

        interface SetRightTarget
        {
            SetStartTable rightTarget( TableName table, String column, ColumnType columnType );
        }

        interface SetStartTable
        {
            Builder startTable( TableName startTable );
        }

        Join build();
    }
}
