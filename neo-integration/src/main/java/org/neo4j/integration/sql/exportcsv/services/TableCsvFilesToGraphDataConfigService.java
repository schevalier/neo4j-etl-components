package org.neo4j.integration.sql.exportcsv.services;

import java.nio.file.Path;
import java.util.Collection;

import org.neo4j.integration.neo4j.importcsv.config.GraphDataConfig;
import org.neo4j.integration.neo4j.importcsv.config.NodeConfig;
import org.neo4j.integration.sql.exportcsv.CsvFilesToGraphDataConfigService;
import org.neo4j.integration.sql.metadata.Table;

class TableCsvFilesToGraphDataConfigService implements CsvFilesToGraphDataConfigService
{
    private final Table table;

    public TableCsvFilesToGraphDataConfigService( Table table )
    {
        this.table = table;
    }

    @Override
    public GraphDataConfig createGraphDataConfig( Collection<Path> csvFiles )
    {
        return importConfig -> importConfig.addNodeConfig( NodeConfig.builder()
                .addInputFiles( csvFiles )
                .addLabel( table.name().simpleName() )
                .build() );
    }
}
