package org.neo4j.integration.commands;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.neo4j.integration.neo4j.importcsv.config.formatting.Formatting;
import org.neo4j.integration.sql.exportcsv.DatabaseExportSqlSupplier;
import org.neo4j.integration.sql.exportcsv.mapping.MetadataMappingProvider;
import org.neo4j.integration.sql.exportcsv.mapping.MetadataMappings;
import org.neo4j.integration.sql.exportcsv.mapping.RelationshipNameResolver;
import org.neo4j.integration.sql.metadata.Join;
import org.neo4j.integration.sql.metadata.JoinTable;
import org.neo4j.integration.sql.metadata.Table;
import org.neo4j.integration.sql.metadata.TableName;

import static java.lang.String.format;

public class SchemaExport
{
    private final Collection<Join> joins;
    private final Collection<Table> tables;
    private final Collection<JoinTable> joinTables;

    public SchemaExport( Collection<Table> tables, Collection<Join> joins, Collection<JoinTable> joinTables )
    {
        this.joins = joins;
        this.tables = tables;
        this.joinTables = joinTables;
    }

    public MetadataMappings generateMetadataMappings( Formatting formatting,
                                                      DatabaseExportSqlSupplier sqlSupplier,
                                                      RelationshipNameResolver relationshipNameResolver )
    {
        validate();

        MetadataMappingProvider metadataMappingProvider =
                new MetadataMappingProvider( formatting, sqlSupplier, relationshipNameResolver );
        MetadataMappings metadataMappings = new MetadataMappings();

        tables.forEach( o -> metadataMappings.add( o.invoke( metadataMappingProvider ) ) );
        joins.forEach( o -> metadataMappings.add( o.invoke( metadataMappingProvider ) ) );
        joinTables.forEach( o -> metadataMappings.add( o.invoke( metadataMappingProvider ) ) );

        return metadataMappings;
    }

    Collection<Table> tables()
    {
        return tables;
    }

    Collection<Join> joins()
    {
        return joins;
    }

    Collection<JoinTable> joinTables()
    {
        return joinTables;
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
                                                join.tableNames().stream()
                                                        .map( TableName::fullName )
                                                        .collect( Collectors.joining( " -> " ) ) ) );
                            }
                        } ) );

    }
}
