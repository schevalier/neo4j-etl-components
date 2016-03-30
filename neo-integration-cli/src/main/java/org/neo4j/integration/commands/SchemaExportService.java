package org.neo4j.integration.commands;

import org.neo4j.integration.sql.DatabaseClient;

public class SchemaExportService
{

    private final DatabaseInspector databaseInspector;

    public SchemaExportService( DatabaseClient databaseClient )
    {
        databaseInspector = new DatabaseInspector( databaseClient );
    }

    public SchemaExport inspect() throws Exception
    {
        return databaseInspector.buildSchemaExport();
    }

}
