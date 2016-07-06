package org.neo4j.integration.sql.exportcsv.mapping;

import org.neo4j.integration.neo4j.importcsv.config.GraphObjectType;
import org.neo4j.integration.neo4j.importcsv.config.formatting.Formatting;
import org.neo4j.integration.sql.exportcsv.DatabaseExportSqlSupplier;
import org.neo4j.integration.sql.metadata.DatabaseObjectServiceProvider;
import org.neo4j.integration.sql.metadata.Join;
import org.neo4j.integration.sql.metadata.JoinTable;
import org.neo4j.integration.sql.metadata.Table;

public class MetadataMappingProvider implements DatabaseObjectServiceProvider<MetadataMapping>
{
    private final Formatting formatting;
    private final DatabaseExportSqlSupplier sqlSupplier;
    private RelationshipNameResolver relationshipNameResolver;

    public MetadataMappingProvider( Formatting formatting,
                                    DatabaseExportSqlSupplier sqlSupplier,
                                    RelationshipNameResolver relationshipNameResolver )
    {
        this.formatting = formatting;
        this.sqlSupplier = sqlSupplier;
        this.relationshipNameResolver = relationshipNameResolver;
    }

    @Override
    public MetadataMapping tableService( Table table )
    {
        ColumnToCsvFieldMappings mappings = new TableToCsvFieldMapper( formatting ).createMappings( table );

        return new MetadataMapping( table.descriptor(), GraphObjectType.Node, sqlSupplier.sql( mappings ), mappings );
    }

    @Override
    public MetadataMapping joinService( Join join )
    {
        ColumnToCsvFieldMappings mappings = new JoinToCsvFieldMapper( formatting, relationshipNameResolver )
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
                new JoinTableToCsvFieldMapper( formatting, relationshipNameResolver ).createMappings( joinTable );

        return new MetadataMapping(
                joinTable.descriptor(),
                GraphObjectType.Relationship,
                sqlSupplier.sql( mappings ),
                mappings );
    }
}
