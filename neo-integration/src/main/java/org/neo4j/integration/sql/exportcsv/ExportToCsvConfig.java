package org.neo4j.integration.sql.exportcsv;

import java.nio.file.Path;

import org.neo4j.integration.neo4j.importcsv.config.formatting.Formatting;
import org.neo4j.integration.sql.ConnectionConfig;
import org.neo4j.integration.util.Preconditions;

public class ExportToCsvConfig
{
    public static Builder.SetDestination builder()
    {
        return new ExportToCsvConfigBuilder();
    }

    private final Path destination;
    private final ConnectionConfig connectionConfig;
    private final Formatting formatting;

    ExportToCsvConfig( ExportToCsvConfigBuilder builder )
    {
        this.destination = Preconditions.requireNonNull( builder.destination, "Destination" );
        this.connectionConfig = Preconditions.requireNonNull( builder.connectionConfig, "ConnectionConfig" );
        this.formatting = Preconditions.requireNonNull( builder.formatting, "Formatting" );
    }

    public Path destination()
    {
        return destination;
    }

    public ConnectionConfig connectionConfig()
    {
        return connectionConfig;
    }

    public Formatting formatting()
    {
        return formatting;
    }

    public interface Builder
    {
        interface SetDestination
        {
            SetMySqlConnectionConfig destination( Path directory );
        }

        interface SetMySqlConnectionConfig
        {
            SetFormatting connectionConfig( ConnectionConfig config );
        }

        interface SetFormatting
        {
            Builder formatting( Formatting formatting );
        }

        ExportToCsvConfig build();
    }
}
