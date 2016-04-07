package org.neo4j.integration.sql.exportcsv;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.neo4j.integration.neo4j.importcsv.config.Formatting;
import org.neo4j.integration.sql.ConnectionConfig;
import org.neo4j.integration.sql.Credentials;
import org.neo4j.integration.sql.exportcsv.mapping.CsvResource;
import org.neo4j.integration.util.Preconditions;

public class ExportToCsvConfig
{
    public static Builder.SetDestination builder()
    {
        return new ExportToCsvConfigBuilder();
    }

    public static ExportToCsvConfig fromJson( JsonNode root, Credentials credentials )
    {
        Builder builder = builder()
                .destination( Paths.get( root.path( "destination" ).textValue() ) )
                .connectionConfig( ConnectionConfig.fromJson( root.path( "connection-config" ), credentials ) )
                .formatting( Formatting.fromJson( root.path( "formatting" ) ) );

        ArrayNode csvResources = (ArrayNode) root.path( "csv-resources" );
        for ( JsonNode csvResource : csvResources )
        {
            builder.addCsvResource( CsvResource.fromJson( csvResource ) );
        }

        return builder.build();
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

    public JsonNode toJson()
    {
        ObjectNode root = JsonNodeFactory.instance.objectNode();

        root.put( "destination", destination.toString() );
        root.set( "connection-config", connectionConfig.toJson() );
        root.set( "formatting", formatting.toJson() );

        ArrayNode array = JsonNodeFactory.instance.arrayNode();

        for ( CsvResource csvResource : csvResources )
        {
            array.add( csvResource.toJson() );
        }

        root.set( "csv-resources", array );

        return root;
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

        Builder addCsvResource( CsvResource csvResource );

        ExportToCsvConfig build();
    }
}
