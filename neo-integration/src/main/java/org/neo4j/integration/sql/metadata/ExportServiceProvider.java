package org.neo4j.integration.sql.metadata;

public interface ExportServiceProvider<T>
{
    T tableExportService( Table table );

    T joinExportService( Join join );

    T joinTableExportService( JoinTable joinTable );
}
