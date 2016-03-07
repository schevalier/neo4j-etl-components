package org.neo4j.integration.sql.exportcsv;

import org.neo4j.integration.sql.metadata.ExportServiceProvider;
import org.neo4j.integration.sql.metadata.Join;
import org.neo4j.integration.sql.metadata.JoinTable;
import org.neo4j.integration.sql.metadata.Table;

public class ExportToCsvService implements ExportServiceProvider<ExportDatabaseObjectToCsvService>
{
    @Override
    public ExportDatabaseObjectToCsvService tableExportService( Table table )
    {
        return new ExportTableToCsvService( table );
    }

    @Override
    public ExportDatabaseObjectToCsvService joinExportService( Join join )
    {
        return new ExportJoinToCsvService( join );
    }

    @Override
    public ExportDatabaseObjectToCsvService joinTableExportService( JoinTable joinTable )
    {
        return new ExportJoinTableToCsvService( joinTable );
    }
}
