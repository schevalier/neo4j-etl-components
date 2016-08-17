package org.neo4j.etl.sql.metadata;

public interface DatabaseObject
{
    String descriptor();

    <T> T invoke( DatabaseObjectServiceProvider<T> databaseObjectServiceProvider );
}
