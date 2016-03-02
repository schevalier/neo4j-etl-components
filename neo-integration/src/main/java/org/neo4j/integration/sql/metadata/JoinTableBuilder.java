package org.neo4j.integration.sql.metadata;

class JoinTableBuilder implements
        JoinTable.Builder.SetStartForeignKey,
        JoinTable.Builder.SetStartPrimaryKey,
        JoinTable.Builder.SetEndForeignKey,
        JoinTable.Builder.SetEndPrimaryKey,
        JoinTable.Builder
{
    Column startForeignKey;
    Column startPrimaryKey;
    Column endPrimaryKey;
    Column endForeignKey;

    @Override
    public JoinTable.Builder.SetStartPrimaryKey startForeignKey( Column startForeignKey )
    {
        this.startForeignKey = startForeignKey;
        return this;
    }

    @Override
    public SetEndForeignKey connectsToStartTablePrimaryKey( Column startPrimaryKey )
    {
        this.startPrimaryKey = startPrimaryKey;
        return this;
    }

    @Override
    public SetEndPrimaryKey endForeignKey( Column endForeignKey )
    {
        this.endForeignKey = endForeignKey;
        return this;
    }

    @Override
    public JoinTable.Builder connectsToEndTablePrimaryKey( Column endPrimaryKey )
    {
        this.endPrimaryKey = endPrimaryKey;
        return this;
    }

    @Override
    public JoinTable build()
    {
        return new JoinTable( this );
    }
}
