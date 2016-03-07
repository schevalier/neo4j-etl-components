package org.neo4j.integration.sql.exportcsv.services;

import org.neo4j.integration.sql.exportcsv.DatabaseObjectToCsvFilesService;
import org.neo4j.integration.sql.metadata.MetadataServiceProvider;
import org.neo4j.integration.sql.metadata.Join;
import org.neo4j.integration.sql.metadata.JoinTable;
import org.neo4j.integration.sql.metadata.Table;

public class ExportToCsvServiceProvider implements MetadataServiceProvider<DatabaseObjectToCsvFilesService>
{
    @Override
    public DatabaseObjectToCsvFilesService tableService( Table table )
    {
        return new TableToCsvFilesService( table );
    }

    @Override
    public DatabaseObjectToCsvFilesService joinService( Join join )
    {
        return new JoinToCsvFilesService( join );
    }

    @Override
    public DatabaseObjectToCsvFilesService joinTableService( JoinTable joinTable )
    {
        return new JoinTableToCsvFilesService( joinTable );
    }
}
