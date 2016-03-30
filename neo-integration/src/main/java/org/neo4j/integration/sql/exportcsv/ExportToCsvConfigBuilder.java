package org.neo4j.integration.sql.exportcsv;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;

import org.neo4j.integration.neo4j.importcsv.config.Formatting;
import org.neo4j.integration.sql.ConnectionConfig;
import org.neo4j.integration.sql.exportcsv.mapping.CsvResource;

class ExportToCsvConfigBuilder implements ExportToCsvConfig.Builder,
        ExportToCsvConfig.Builder.SetDestination,
        ExportToCsvConfig.Builder.SetFormatting,
        ExportToCsvConfig.Builder.SetMySqlConnectionConfig
{
    final Collection<CsvResource> csvResources = new ArrayList<>();
    Path destination;
    ConnectionConfig connectionConfig;
    Formatting formatting;

    @Override
    public ExportToCsvConfig.Builder.SetMySqlConnectionConfig destination( Path directory )
    {
        this.destination = directory;
        return this;
    }

    @Override
    public ExportToCsvConfig.Builder.SetFormatting connectionConfig( ConnectionConfig config )
    {
        this.connectionConfig = config;
        return this;
    }

    @Override
    public ExportToCsvConfig.Builder formatting( Formatting formatting )
    {
        this.formatting = formatting;
        return this;
    }

    @Override
    public ExportToCsvConfig.Builder addCsvResource( CsvResource csvResource )
    {
        csvResources.add( csvResource );
        return this;
    }

    @Override
    public ExportToCsvConfig build()
    {
        return new ExportToCsvConfig( this );
    }
}
