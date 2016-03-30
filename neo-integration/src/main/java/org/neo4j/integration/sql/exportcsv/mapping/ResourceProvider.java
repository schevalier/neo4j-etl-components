package org.neo4j.integration.sql.exportcsv.mapping;

import org.neo4j.integration.neo4j.importcsv.config.Formatting;
import org.neo4j.integration.neo4j.importcsv.config.GraphObjectType;
import org.neo4j.integration.sql.exportcsv.DatabaseExportSqlSupplier;
import org.neo4j.integration.sql.exportcsv.io.RowStrategy;
import org.neo4j.integration.sql.metadata.DatabaseObjectServiceProvider;
import org.neo4j.integration.sql.metadata.Join;
import org.neo4j.integration.sql.metadata.JoinTable;
import org.neo4j.integration.sql.metadata.Table;

public class ResourceProvider implements DatabaseObjectServiceProvider<Resource>
{
    private final Formatting formatting;
    private final DatabaseExportSqlSupplier sqlSupplier;

    public ResourceProvider( Formatting formatting, DatabaseExportSqlSupplier sqlSupplier )
    {
        this.formatting = formatting;
        this.sqlSupplier = sqlSupplier;
    }

    @Override
    public Resource tableService( Table table )
    {
        ColumnToCsvFieldMappings mappings = new TableToCsvFieldMapper( formatting ).createMappings( table );

        return new Resource(
                table.descriptor(), GraphObjectType.Node,
                sqlSupplier.sql( mappings ),
                mappings,
                RowStrategy.WriteRowWithNullKey );
    }

    @Override
    public Resource joinService( Join join )
    {
        ColumnToCsvFieldMappings mappings = new JoinToCsvFieldMapper( formatting ).createMappings( join );

        return new Resource(
                join.descriptor(), GraphObjectType.Relationship,
                sqlSupplier.sql( mappings ),
                mappings,
                RowStrategy.IgnoreRowWithNullKey );
    }

    @Override
    public Resource joinTableService( JoinTable joinTable )
    {
        ColumnToCsvFieldMappings mappings = new JoinTableToCsvFieldMapper( formatting ).createMappings( joinTable );

        return new Resource(
                joinTable.descriptor(), GraphObjectType.Relationship,
                sqlSupplier.sql( mappings ),
                mappings,
                RowStrategy.IgnoreRowWithNullKey );
    }
}
