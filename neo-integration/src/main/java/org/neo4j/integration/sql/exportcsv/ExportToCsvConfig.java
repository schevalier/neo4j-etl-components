package org.neo4j.integration.sql.exportcsv;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.neo4j.integration.neo4j.importcsv.config.Formatting;
import org.neo4j.integration.sql.ConnectionConfig;
import org.neo4j.integration.sql.exportcsv.mapping.CsvResource;
import org.neo4j.integration.sql.metadata.DatabaseObject;
import org.neo4j.integration.sql.metadata.Join;
import org.neo4j.integration.sql.metadata.JoinTable;
import org.neo4j.integration.sql.metadata.Table;
import org.neo4j.integration.sql.metadata.TableName;
import org.neo4j.integration.util.Preconditions;

import static java.lang.String.format;

public class ExportToCsvConfig
{
    public static Builder.SetDestination builder()
    {
        return new ExportToCsvConfigBuilder();
    }

    private final Path destination;
    private final ConnectionConfig connectionConfig;
    private final Formatting formatting;
    private final Collection<CsvResource> csvResources;

    ExportToCsvConfig( ExportToCsvConfigBuilder builder )
    {
        this.destination = Preconditions.requireNonNull( builder.destination, "Destination" );
        this.connectionConfig = Preconditions.requireNonNull( builder.connectionConfig, "ConnectionConfig" );
        this.formatting = Preconditions.requireNonNull( builder.formatting, "Formatting" );
        this.csvResources = Preconditions.requireNonNull( builder.csvResources, "CsvResources" );
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

    public Collection<CsvResource> csvResources()
    {
        return csvResources;
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

        Builder addCsvResource(CsvResource csvResource);

        ExportToCsvConfig build();
    }
}
