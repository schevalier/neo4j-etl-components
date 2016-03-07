package org.neo4j.integration.sql.metadata;

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

    public JoinTable( JoinTableBuilder builder )
    {
        this.startForeignKey = Preconditions.requireNonNull( builder.startForeignKey, "StartForeignKey" );
        this.startPrimaryKey = Preconditions.requireNonNull( builder.startPrimaryKey, "StartPrimaryKey" );
        this.endPrimaryKey = Preconditions.requireNonNull( builder.endPrimaryKey, "EndPrimaryKey" );
        this.endForeignKey = Preconditions.requireNonNull( builder.endForeignKey, "EndForeignKey" );
    }

    @Override
    public String descriptor()
    {
        return startForeignKey.table().simpleName();
    }

    @Override
    public <T> T createService( MetadataServiceProvider<T> metadataServiceProvider )
    {
        return metadataServiceProvider.joinTableService( this );
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

        JoinTable build();
    }
}