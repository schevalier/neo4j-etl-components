package org.neo4j.mysql.config;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.neo4j.ingest.config.Formatting;
import org.neo4j.utils.Preconditions;

import static java.lang.String.format;

import static org.neo4j.utils.StringListBuilder.stringList;

public class RelationalDatabaseExportConfig implements ExportProperties
{
    public static Builder.SetDestination builder()
    {
        return new RelationalDatabaseExportConfigBuilder();
    }

    private final Path destination;
    private final ConnectionConfig connectionConfig;
    private final Formatting formatting;
    private final Collection<Table> tables;
    private final Collection<Join> joins;

    RelationalDatabaseExportConfig( RelationalDatabaseExportConfigBuilder builder )
    {
        this.destination = Preconditions.requireNonNull( builder.destination, "Destination" );
        this.connectionConfig = Preconditions.requireNonNull( builder.connectionConfig, "Connection" );
        this.formatting = Preconditions.requireNonNull( builder.formatting, "Formatting" );
        this.tables = Collections.unmodifiableCollection( Preconditions.requireNonNull( builder.tables, "Tables" ) );
        this.joins = Collections.unmodifiableCollection( Preconditions.requireNonNull( builder.joins, "Joins" ) );

        validate();
    }

    private void validate()
    {
        List<TableName> allTableNames = tables.stream().map( Table::name ).collect( Collectors.toList() );

        joins.forEach(
                join -> join.tableNames().forEach(
                        tableName ->
                        {
                            if ( !allTableNames.contains( tableName ) )
                            {
                                throw new IllegalStateException(
                                        format( "Config is missing table definition '%s' for join [%s]",
                                                tableName.fullName(),
                                                stringList( join.tableNames(), " -> ", TableName::fullName ) ) );
                            }
                        } ) );

    }

    @Override
    public Path destination()
    {
        return destination;
    }

    @Override
    public ConnectionConfig connectionConfig()
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
            SetFormatting connectionConfig( ConnectionConfig config );
        }

        interface SetFormatting
        {
            Builder formatting( Formatting formatting );
        }

        Builder addTable( Table table );

        Builder addJoin( Join join );


        RelationalDatabaseExportConfig build();
    }
}
