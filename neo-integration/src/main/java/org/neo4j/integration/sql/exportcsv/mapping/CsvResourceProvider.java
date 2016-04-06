package org.neo4j.integration.sql.exportcsv.mapping;

import org.neo4j.integration.neo4j.importcsv.config.Formatting;
import org.neo4j.integration.neo4j.importcsv.config.GraphObjectType;
import org.neo4j.integration.sql.exportcsv.DatabaseExportSqlSupplier;
import org.neo4j.integration.sql.metadata.DatabaseObjectServiceProvider;
import org.neo4j.integration.sql.metadata.Join;
import org.neo4j.integration.sql.metadata.JoinTable;
import org.neo4j.integration.sql.metadata.Table;

public class CsvResourceProvider implements DatabaseObjectServiceProvider<CsvResource>
{
    private final Formatting formatting;
    private final DatabaseExportSqlSupplier sqlSupplier;

    public CsvResourceProvider( Formatting formatting, DatabaseExportSqlSupplier sqlSupplier )
    {
        this.formatting = formatting;
        this.sqlSupplier = sqlSupplier;
    }

    @Override
    public CsvResource tableService( Table table )
    {
        ColumnToCsvFieldMappings mappings = new TableToCsvFieldMapper( formatting ).createMappings( table );

        return new CsvResource( table.descriptor(), GraphObjectType.Node, sqlSupplier.sql( mappings ), mappings );
    }

    @Override
    public CsvResource joinService( Join join )
    {
        ColumnToCsvFieldMappings mappings = new JoinToCsvFieldMapper( formatting ).createMappings( join );

        return new CsvResource(
                join.descriptor(),
                GraphObjectType.Relationship,
                sqlSupplier.sql( mappings ),
                mappings);
    }

    @Override
    public CsvResource joinTableService( JoinTable joinTable )
    {
        ColumnToCsvFieldMappings mappings = new JoinTableToCsvFieldMapper( formatting ).createMappings( joinTable );

        return new CsvResource(
                joinTable.descriptor(),
                GraphObjectType.Relationship,
                sqlSupplier.sql( mappings ),
                mappings);
    }
}
