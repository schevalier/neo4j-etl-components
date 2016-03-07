package org.neo4j.integration.sql.metadata;

public interface MetadataServiceProvider<T>
{
    T tableService( Table table );

    T joinService( Join join );

    T joinTableService( JoinTable joinTable );
}
