package org.neo4j.mysql.config;

import java.nio.file.Path;
import java.util.Objects;

import org.neo4j.ingest.config.Formatting;
import org.neo4j.utils.Preconditions;

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
        this.destination = Preconditions.requireNonNull( builder.destination, "Destination" );
        this.connectionConfig = Preconditions.requireNonNull( builder.connectionConfig, "Connection" );
        this.formatting = Preconditions.requireNonNull( builder.formatting, "Formatting" );
        this.table = Preconditions.requireNonNull( builder.table, "Table" );
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
