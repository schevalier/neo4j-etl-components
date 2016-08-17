package org.neo4j.etl.sql.metadata;

public interface DatabaseObjectServiceProvider<T>
{
    T tableService( Table table );

    T joinService( Join join );

    T joinTableService( JoinTable joinTable );
}
