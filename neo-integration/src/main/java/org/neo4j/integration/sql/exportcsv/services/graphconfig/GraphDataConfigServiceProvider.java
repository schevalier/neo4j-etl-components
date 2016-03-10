package org.neo4j.integration.sql.exportcsv.services.graphconfig;

import org.neo4j.integration.sql.exportcsv.CsvFilesToGraphDataConfigService;
import org.neo4j.integration.sql.metadata.DatabaseObjectServiceProvider;
import org.neo4j.integration.sql.metadata.Join;
import org.neo4j.integration.sql.metadata.JoinTable;
import org.neo4j.integration.sql.metadata.Table;

public class GraphDataConfigServiceProvider implements DatabaseObjectServiceProvider<CsvFilesToGraphDataConfigService>
{
    @Override
    public CsvFilesToGraphDataConfigService tableService( Table table )
    {
        return new TableCsvFilesToGraphDataConfigService();
    }

    @Override
    public CsvFilesToGraphDataConfigService joinService( Join join )
    {
        return new JoinCsvFilesToGraphDataConfigService();
    }

    @Override
    public CsvFilesToGraphDataConfigService joinTableService( JoinTable joinTable )
    {
        return new JoinTableCsvFilesToGraphDataConfigService();
    }
}
