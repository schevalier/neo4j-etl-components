package org.neo4j.mysql.config;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;

import org.neo4j.ingest.config.Formatting;

class ExportConfigBuilder implements RelationalDatabaseExportConfig.Builder,
        RelationalDatabaseExportConfig.Builder.SetDestination,
        RelationalDatabaseExportConfig.Builder.SetMySqlConnectionConfig,
        RelationalDatabaseExportConfig.Builder.SetFormatting
{
    final Collection<Table> tables = new ArrayList<>();
    final Collection<Join> joins = new ArrayList<>();
    Path destination;
    ConnectionConfig connectionConfig;
    Formatting formatting;

    @Override
    public SetMySqlConnectionConfig destination( Path directory )
    {
        this.destination = directory;
        return this;
    }

    @Override
    public SetFormatting connectionConfig( ConnectionConfig config )
    {
        this.connectionConfig = config;
        return this;
    }

    @Override
    public RelationalDatabaseExportConfig.Builder formatting( Formatting formatting )
    {
        this.formatting = formatting;
        return this;
    }

    @Override
    public RelationalDatabaseExportConfig.Builder addTable( Table table )
    {
        tables.add( table );
        return this;
    }

    @Override
    public RelationalDatabaseExportConfig.Builder addJoin( Join join )
    {
        joins.add( join );
        return this;
    }

    @Override
    public RelationalDatabaseExportConfig build()
    {
        return new RelationalDatabaseExportConfig( this );
    }
}
