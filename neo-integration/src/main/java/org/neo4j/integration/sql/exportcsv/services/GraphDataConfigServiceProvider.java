package org.neo4j.integration.sql.exportcsv.services;

import org.neo4j.integration.sql.exportcsv.CsvFilesToGraphDataConfigService;
import org.neo4j.integration.sql.exportcsv.services.JoinCsvFilesToGraphDataConfigService;
import org.neo4j.integration.sql.exportcsv.services.JoinTableCsvFilesToGraphDataConfigService;
import org.neo4j.integration.sql.exportcsv.services.TableCsvFilesToGraphDataConfigService;
import org.neo4j.integration.sql.metadata.Join;
import org.neo4j.integration.sql.metadata.JoinTable;
import org.neo4j.integration.sql.metadata.MetadataServiceProvider;
import org.neo4j.integration.sql.metadata.Table;

public class GraphDataConfigServiceProvider implements MetadataServiceProvider<CsvFilesToGraphDataConfigService>
{
    @Override
    public CsvFilesToGraphDataConfigService tableService( Table table )
    {
        return new TableCsvFilesToGraphDataConfigService( table );
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
