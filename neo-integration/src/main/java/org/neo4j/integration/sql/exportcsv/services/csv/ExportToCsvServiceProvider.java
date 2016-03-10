package org.neo4j.integration.sql.exportcsv.services.csv;

import org.neo4j.integration.sql.exportcsv.DatabaseObjectToCsvFilesService;
import org.neo4j.integration.sql.metadata.DatabaseObjectServiceProvider;
import org.neo4j.integration.sql.metadata.Join;
import org.neo4j.integration.sql.metadata.JoinTable;
import org.neo4j.integration.sql.metadata.Table;

public class ExportToCsvServiceProvider implements DatabaseObjectServiceProvider<DatabaseObjectToCsvFilesService>
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
