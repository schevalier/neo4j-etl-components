package org.neo4j.mysql.config;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;

import org.neo4j.ingest.config.Formatting;

class ExportConfigBuilder implements ExportConfig.Builder,
        ExportConfig.Builder.SetDestination,
        ExportConfig.Builder.SetMySqlConnectionConfig,
        ExportConfig.Builder.SetFormatting
{
    final Collection<Table> tables = new ArrayList<>();
    final Collection<Join> joins = new ArrayList<>();
    Path destination;
    MySqlConnectionConfig connectionConfig;
    Formatting formatting;

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
    public ExportConfig.Builder formatting( Formatting formatting )
    {
        this.formatting = formatting;
        return this;
    }

    @Override
    public ExportConfig.Builder addTable( Table table )
    {
        tables.add( table );
        return this;
    }

    @Override
    public ExportConfig.Builder addJoin( Join join )
    {
        joins.add( join );
        return this;
    }

    @Override
    public ExportConfig build()
    {
        return new ExportConfig( this );
    }
}
