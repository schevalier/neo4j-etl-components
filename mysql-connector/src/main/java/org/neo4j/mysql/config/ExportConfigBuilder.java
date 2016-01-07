package org.neo4j.mysql.config;

import java.nio.file.Path;

import org.neo4j.ingest.config.Formatting;

class ExportConfigBuilder implements ExportConfig.Builder,
        ExportConfig.Builder.SetDestination,
        ExportConfig.Builder.SetMySqlConnectionConfig,
        ExportConfig.Builder.SetFormatting,
        ExportConfig.Builder.SetTable
{
    Path destination;
    MySqlConnectionConfig connectionConfig;
    Formatting formatting;
    Table table;

    @Override
    public SetMySqlConnectionConfig destination( Path directory )
    {
        this.destination = directory;
        return this;
    }

    @Override
    public SetFormatting mySqlConnectionConfig( MySqlConnectionConfig config )
    {
        this.connectionConfig = config;
        return this;
    }

    @Override
    public SetTable formatting( Formatting formatting )
    {
        this.formatting = formatting;
        return this;
    }

    @Override
    public ExportConfig.Builder table( Table table )
    {
        this.table = table;
        return this;
    }

    @Override
    public ExportConfig build()
    {
        return new ExportConfig( this );
    }
}
