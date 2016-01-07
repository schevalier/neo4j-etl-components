package org.neo4j.mysql.config;

import java.nio.file.Path;
import java.util.Objects;

import org.neo4j.ingest.config.Formatting;

public class ExportConfig
{
    public static Builder.SetDestination builder()
    {
        return new ExportConfigBuilder();
    }

    private final Path destination;
    private final MySqlConnectionConfig connectionConfig;
    private final Formatting formatting;
    private final Table table;

    public ExportConfig( ExportConfigBuilder builder )
    {
        this.destination = Objects.requireNonNull( builder.destination, "Destination cannot be null" );
        this.connectionConfig = Objects.requireNonNull( builder.connectionConfig, "Connection config cannot be null" );
        this.formatting = Objects.requireNonNull( builder.formatting, "Formatting cannot be null" );
        this.table = Objects.requireNonNull( builder.table, "Table cannot be null" );
    }

    public Path destination()
    {
        return destination;
    }

    public MySqlConnectionConfig connectionConfig()
    {
        return connectionConfig;
    }

    public Formatting formatting()
    {
        return formatting;
    }

    public Table table()
    {
        return table;
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
            SetTable formatting( Formatting formatting );
        }

        interface SetTable
        {
            Builder table( Table table );
        }

        ExportConfig build();
    }
}
