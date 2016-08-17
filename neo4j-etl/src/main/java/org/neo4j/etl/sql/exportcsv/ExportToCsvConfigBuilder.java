package org.neo4j.etl.sql.exportcsv;

import java.nio.file.Path;

import org.neo4j.etl.neo4j.importcsv.config.formatting.Formatting;
import org.neo4j.etl.sql.ConnectionConfig;

class ExportToCsvConfigBuilder implements ExportToCsvConfig.Builder,
        ExportToCsvConfig.Builder.SetDestination,
        ExportToCsvConfig.Builder.SetFormatting,
        ExportToCsvConfig.Builder.SetMySqlConnectionConfig
{
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
    public ExportToCsvConfig build()
    {
        return new ExportToCsvConfig( this );
    }
}
