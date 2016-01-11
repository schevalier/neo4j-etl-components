package org.neo4j.mysql.config;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;

import org.neo4j.ingest.config.Formatting;
import org.neo4j.utils.Preconditions;

public class ExportConfig implements ExportProperties
{
    public static Builder.SetDestination builder()
    {
        return new ExportConfigBuilder();
    }

    private final Path destination;
    private final MySqlConnectionConfig connectionConfig;
    private final Formatting formatting;
    private final Collection<Table> tables;
    private final Collection<Join> joins;

    public ExportConfig( ExportConfigBuilder builder )
    {
        this.destination = Preconditions.requireNonNull( builder.destination, "Destination" );
        this.connectionConfig = Preconditions.requireNonNull( builder.connectionConfig, "Connection" );
        this.formatting = Preconditions.requireNonNull( builder.formatting, "Formatting" );
        this.tables = Collections.unmodifiableCollection( Preconditions.requireNonNull( builder.tables, "Tables" ) );
        this.joins = Collections.unmodifiableCollection( Preconditions.requireNonNull( builder.joins, "Joins" ) );
    }

    @Override
    public Path destination()
    {
        return destination;
    }

    @Override
    public MySqlConnectionConfig connectionConfig()
    {
        return connectionConfig;
    }

    @Override
    public Formatting formatting()
    {
        return formatting;
    }

    public Collection<Table> tables()
    {
        return tables;
    }

    public Collection<Join> joins()
    {
        return joins;
    }

    public interface Builder
    {
        interface SetDestination
        {
            SetMySqlConnectionConfig destination( Path directory );
        }

        interface SetMySqlConnectionConfig
        {
            SetFormatting mySqlConnectionConfig( MySqlConnectionConfig config );
        }

        interface SetFormatting
        {
            Builder formatting( Formatting formatting );
        }

        Builder addTable( Table table );

        Builder addJoin( Join join );


        ExportConfig build();
    }
}
