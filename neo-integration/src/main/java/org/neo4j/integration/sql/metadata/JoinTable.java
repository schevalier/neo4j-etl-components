package org.neo4j.integration.sql.metadata;

import java.util.Collection;

import org.neo4j.integration.util.Preconditions;

public class JoinTable implements DatabaseObject
{
    public static Builder.SetStartForeignKey builder()
    {
        return new JoinTableBuilder();
    }

    private final Column startForeignKey;

    private final Column startPrimaryKey;
    private final Column endPrimaryKey;
    private final Column endForeignKey;
    private final Collection<Column> columns;


    public JoinTable( JoinTableBuilder builder )
    {
        this.startForeignKey = Preconditions.requireNonNull( builder.startForeignKey, "StartForeignKey" );
        this.startPrimaryKey = Preconditions.requireNonNull( builder.startPrimaryKey, "StartPrimaryKey" );
        this.endPrimaryKey = Preconditions.requireNonNull( builder.endPrimaryKey, "EndPrimaryKey" );
        this.endForeignKey = Preconditions.requireNonNull( builder.endForeignKey, "EndForeignKey" );
        this.columns = builder.columns;
    }

    @Override
    public String descriptor()
    {
        return startForeignKey.table().simpleName();
    }

    @Override
    public <T> T createService( DatabaseObjectServiceProvider<T> databaseObjectServiceProvider )
    {
        return databaseObjectServiceProvider.joinTableService( this );
    }

    public Column startForeignKey()
    {
        return startForeignKey;
    }

    public Column startPrimaryKey()
    {
        return startPrimaryKey;
    }

    public Column endForeignKey()
    {
        return endForeignKey;
    }

    public Column endPrimaryKey()
    {
        return endPrimaryKey;
    }

    public Collection<Column> columns()
    {
        return columns;
    }

    public TableName joinTableName()
    {
        return startForeignKey.table();
    }

    public interface Builder
    {
        interface SetStartForeignKey
        {
            SetStartPrimaryKey startForeignKey( Column startForeignKey );
        }

        interface SetStartPrimaryKey
        {
            SetEndForeignKey connectsToStartTablePrimaryKey( Column startPrimaryKey );
        }

        interface SetEndForeignKey
        {
            SetEndPrimaryKey endForeignKey( Column endForeignKey );
        }

        interface SetEndPrimaryKey
        {
            Builder connectsToEndTablePrimaryKey( Column endPrimaryKey );
        }

        Builder addColumn( Column column );

        JoinTable build();
    }
}
