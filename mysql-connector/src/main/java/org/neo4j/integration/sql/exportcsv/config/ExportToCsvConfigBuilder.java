package org.neo4j.integration.sql.exportcsv.config;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;

import org.neo4j.integration.sql.metadata.ConnectionConfig;
import org.neo4j.integration.sql.metadata.Join;
import org.neo4j.integration.sql.metadata.Table;
import org.neo4j.integration.neo4j.importcsv.config.Formatting;

class ExportToCsvConfigBuilder implements ExportToCsvConfig.Builder,
        ExportToCsvConfig.Builder.SetDestination,
        ExportToCsvConfig.Builder.SetFormatting,
        ExportToCsvConfig.Builder.SetMySqlConnectionConfig
{
    final Collection<Table> tables = new ArrayList<>();
    final Collection<Join> joins = new ArrayList<>();
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
    public ExportToCsvConfig.Builder addTable( Table table )
    {
        tables.add( table );
        return this;
    }

    @Override
    public ExportToCsvConfig.Builder addJoin(  Join join )
    {
        joins.add( join );
        return this;
    }

    @Override
    public ExportToCsvConfig.Builder addJoins( Collection<Join> joins )
    {
        this.joins.addAll( joins );
        return this;
    }

    @Override
    public ExportToCsvConfig build()
    {
        return new ExportToCsvConfig( this );
    }
}
