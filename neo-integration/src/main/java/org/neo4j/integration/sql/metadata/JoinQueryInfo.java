package org.neo4j.integration.sql.metadata;

public interface JoinQueryInfo
{
    TableName tableOne();

    TableName tableTwo();

    TableName table();

    String specialisedSql();
}
