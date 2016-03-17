package org.neo4j.integration.sql.metadata;

public interface JoinQueryInfo
{
    TableName startTable();

    TableName endTable();

    TableName table();

    String specialisedSql();
}
