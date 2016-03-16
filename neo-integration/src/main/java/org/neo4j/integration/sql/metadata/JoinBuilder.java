package org.neo4j.integration.sql.metadata;

class JoinBuilder implements
        Join.Builder.SetLeftSource,
        Join.Builder.SetLeftTarget,
        Join.Builder.SetRightSource,
        Join.Builder.SetRightTarget,
        Join.Builder.SetStartTable,
        Join.Builder
{
    Column leftSource;
    Column leftTarget;
    Column rightSource;
    Column rightTarget;
    TableName startTable;

    @Override
    public Join.Builder startTable( TableName startTable )
    {
        this.startTable = startTable;
        return this;
    }

    @Override
    public SetLeftTarget leftSource( TableName table, String column, ColumnType columnType )
    {
        this.leftSource = createColumn( table, column, columnType );
        return this;
    }


    @Override
    public SetRightSource leftTarget( TableName table, String column, ColumnType columnType )
    {
        this.leftTarget = createColumn( table, column, columnType );
        return this;
    }

    @Override
    public SetRightTarget rightSource( TableName table, String column, ColumnType columnType )
    {
        this.rightSource = createColumn( table, column, columnType );
        return this;
    }

    @Override
    public SetStartTable rightTarget( TableName table, String column, ColumnType columnType )
    {
        this.rightTarget = createColumn( table, column, columnType );
        return this;
    }

    @Override
    public Join build()
    {
        return new Join( this );
    }

    private SimpleColumn createColumn( TableName table, String column, ColumnType columnType )
    {
        return new SimpleColumn(
                table,
                table.fullyQualifiedColumnName( column ),
                column,
                columnType,
                SqlDataType.KEY_DATA_TYPE );
    }
}
