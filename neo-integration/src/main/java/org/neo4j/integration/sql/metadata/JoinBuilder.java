package org.neo4j.integration.sql.metadata;

import org.neo4j.integration.sql.exportcsv.mysql.MySqlDataType;
import org.neo4j.integration.util.Preconditions;

class JoinBuilder implements Join.Builder.SetParentTable,
        Join.Builder.SetPrimaryKey,
        Join.Builder.SetForeignKey,
        Join.Builder.SetChildTable,
        Join.Builder.SetStartTable,
        Join.Builder
{
    private TableName parentTable;
    Column primaryKey;
    Column foreignKey;
    TableName childTable;
    TableName startTable;

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
                .name( parentTable.fullyQualifiedColumnName( primaryKey ) )
                .alias( primaryKey )
                .columnType( ColumnType.PrimaryKey )
                .dataType( MySqlDataType.TEXT )
                .build();
        return this;
    }

    @Override
    public SetChildTable foreignKey( String foreignKey )
    {
        this.foreignKey = Column.builder()
                .table( parentTable )
                .name( parentTable.fullyQualifiedColumnName( foreignKey ) )
                .alias( foreignKey )
                .columnType( ColumnType.ForeignKey )
                .dataType( MySqlDataType.TEXT )
                .build();
        return this;
    }


    @Override
    public Join build()
    {
        return new Join( this );
    }

    @Override
    public SetStartTable childTable( TableName childTable )
    {
        this.childTable = childTable;
        return this;
    }

    @Override
    public Join.Builder startTable( TableName startTable )
    {
        this.startTable = startTable;
        return this;
    }
}
