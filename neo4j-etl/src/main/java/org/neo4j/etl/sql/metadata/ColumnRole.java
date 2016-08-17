package org.neo4j.etl.sql.metadata;

public enum ColumnRole
{
    PrimaryKey,
    ForeignKey,
    Data,
    Literal
}
