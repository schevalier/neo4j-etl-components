package org.neo4j.integration.sql.exportcsv;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.neo4j.integration.neo4j.importcsv.config.Formatting;
import org.neo4j.integration.sql.ConnectionConfig;
import org.neo4j.integration.sql.metadata.DatabaseObject;
import org.neo4j.integration.sql.metadata.Join;
import org.neo4j.integration.sql.metadata.JoinTable;
import org.neo4j.integration.sql.metadata.Table;
import org.neo4j.integration.sql.metadata.TableName;
import org.neo4j.integration.util.Preconditions;

import static java.lang.String.format;

import static org.neo4j.integration.util.StringListBuilder.stringList;

public class ExportToCsvConfig
{
    public static Builder.SetDestination builder()
    {
        return new ExportToCsvConfigBuilder();
    }

    private final Path destination;

    private final ConnectionConfig connectionConfig;
    private final Formatting formatting;
    private final Collection<Table> tables;
    private final Collection<Join> joins;
    private final Collection<JoinTable> joinTables;

    ExportToCsvConfig( ExportToCsvConfigBuilder builder )
    {
        this.destination = Preconditions.requireNonNull( builder.destination, "Destination" );
        this.connectionConfig = Preconditions.requireNonNull( builder.connectionConfig, "ConnectionConfig" );
        this.formatting = Preconditions.requireNonNull( builder.formatting, "Formatting" );
        this.tables = Preconditions.requireNonNull( builder.tables, "Tables" );
        this.joins = Preconditions.requireNonNull( builder.joins, "Joins" );
        this.joinTables = Preconditions.requireNonNull( builder.joinTables, "JoinTables" );

        validate();
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

    public Collection<DatabaseObject> databaseObjects()
    {
        Collection<DatabaseObject> results = new ArrayList<>();
        results.addAll( tables );
        results.addAll( joins );
        results.addAll( joinTables );
        return results;
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

        Builder addTables( Collection<Table> tables );

        Builder addJoin( Join join );

        Builder addJoins( Collection<Join> joins );

        Builder addJoinTable( JoinTable joinTable );

        Builder addJoinTables( Collection<JoinTable> joinTables );

        ExportToCsvConfig build();
    }
}
