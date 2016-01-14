package org.neo4j.integration.mysql.metadata;

public enum ColumnType
{
    PrimaryKey
            {
                @Override
                String name( Column column )
                {
                    return column.fullName();
                }
            },
    ForeignKey
            {
                @Override
                String name( Column column )
                {
                    return column.fullName();
                }
            },
    Data
            {
                @Override
                String name( Column column )
                {
                    return column.fullName();
                }
            },
    Literal
            {
                @Override
                String name( Column column )
                {
                    return column.simpleName();
                }
            };

    abstract String name( Column column );
}
