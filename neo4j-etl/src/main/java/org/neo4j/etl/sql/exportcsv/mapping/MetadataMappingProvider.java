package org.neo4j.etl.sql.exportcsv.mapping;

import org.neo4j.etl.neo4j.importcsv.config.GraphObjectType;
import org.neo4j.etl.neo4j.importcsv.config.formatting.Formatting;
import org.neo4j.etl.sql.exportcsv.DatabaseExportSqlSupplier;
import org.neo4j.etl.sql.exportcsv.io.TinyIntResolver;
import org.neo4j.etl.sql.metadata.DatabaseObjectServiceProvider;
import org.neo4j.etl.sql.metadata.Join;
import org.neo4j.etl.sql.metadata.JoinTable;
import org.neo4j.etl.sql.metadata.Table;

public class MetadataMappingProvider implements DatabaseObjectServiceProvider<MetadataMapping>
{
    private final Formatting formatting;
    private final DatabaseExportSqlSupplier sqlSupplier;
    private final TinyIntResolver tinyIntResolver;
    private RelationshipNameResolver relationshipNameResolver;

    public MetadataMappingProvider( Formatting formatting,
                                    DatabaseExportSqlSupplier sqlSupplier,
                                    RelationshipNameResolver relationshipNameResolver,
                                    TinyIntResolver tinyIntResolver )
    {
        this.formatting = formatting;
        this.sqlSupplier = sqlSupplier;
        this.relationshipNameResolver = relationshipNameResolver;
        this.tinyIntResolver = tinyIntResolver;
    }

    @Override
    public MetadataMapping tableService( Table table )
    {
        ColumnToCsvFieldMappings mappings =
                new TableToCsvFieldMapper( formatting, tinyIntResolver ).createMappings( table );

        return new MetadataMapping( table.descriptor(), GraphObjectType.Node, sqlSupplier.sql( mappings ), mappings );
    }

    @Override
    public MetadataMapping joinService( Join join )
    {
        ColumnToCsvFieldMappings mappings =
                new JoinToCsvFieldMapper( formatting, relationshipNameResolver )
                        .createMappings( join );

        return new MetadataMapping(
                join.descriptor(),
                GraphObjectType.Relationship,
                sqlSupplier.sql( mappings ),
                mappings );
    }

    @Override
    public MetadataMapping joinTableService( JoinTable joinTable )
    {
        ColumnToCsvFieldMappings mappings =
                new JoinTableToCsvFieldMapper( formatting, relationshipNameResolver, tinyIntResolver )
                        .createMappings( joinTable );

        return new MetadataMapping(
                joinTable.descriptor(),
                GraphObjectType.Relationship,
                sqlSupplier.sql( mappings ),
                mappings );
    }
}
