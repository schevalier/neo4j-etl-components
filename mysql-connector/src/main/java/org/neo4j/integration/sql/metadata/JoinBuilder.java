package org.neo4j.integration.sql.metadata;

import org.neo4j.integration.util.Preconditions;

class JoinBuilder implements Join.Builder.SetParentTable,
        Join.Builder.SetPrimaryKey,
        Join.Builder.SetForeignKey,
        Join.Builder.SetChildTable,
        Join.Builder
{
    private TableName parentTable;
    Column primaryKey;
    Column foreignKey;
    TableName childTable;

    @Override
    public SetPrimaryKey parentTable( TableName parent )
    {
        this.parentTable = Preconditions.requireNonNull( parent, "Parent table" );
        return this;
    }

    @Override
    public SetForeignKey primaryKey( String primaryKey )
    {
        this.primaryKey = Column.builder()
                .table( parentTable )
                .name( primaryKey )
                .type( ColumnType.PrimaryKey )
                .build();
        return this;
    }

    @Override
    public SetChildTable foreignKey( String foreignKey )
    {
        this.foreignKey = Column.builder()
                .table( parentTable )
                .name( foreignKey )
                .type( ColumnType.ForeignKey )
                .build();
        return this;
    }

    @Override
    public Join.Builder childTable( TableName childTable )
    {
        this.childTable = childTable;
        return this;
    }

    @Override
    public Join build()
    {
        return new Join( this );
    }
}
