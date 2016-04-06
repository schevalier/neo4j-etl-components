package org.neo4j.integration.sql.metadata;

public enum ColumnType
{
    PrimaryKey,
    ForeignKey,
    CompositeKey,
    Data,
    Literal
            {
                @Override
                public String fullyQualifiedColumnName( TableName tableName, String columnName )
                {
                    return columnName;
                }
            };

    public String fullyQualifiedColumnName( TableName tableName, String columnName )
    {
        return tableName.fullyQualifiedColumnName( columnName );
    }
}
