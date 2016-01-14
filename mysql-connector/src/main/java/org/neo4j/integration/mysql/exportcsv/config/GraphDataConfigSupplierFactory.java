package org.neo4j.integration.mysql.exportcsv.config;

import java.nio.file.Path;
import java.util.Collection;

import org.neo4j.integration.mysql.metadata.Join;
import org.neo4j.integration.mysql.metadata.Table;
import org.neo4j.integration.neo4j.importcsv.config.GraphDataConfigSupplier;
import org.neo4j.integration.neo4j.importcsv.config.NodeConfig;
import org.neo4j.integration.neo4j.importcsv.config.RelationshipConfig;

public class GraphDataConfigSupplierFactory
{
    public GraphDataConfigSupplier supplierFor( Table table, Collection<Path> files )
    {
        return NodeConfig.builder()
                .addInputFiles( files )
                .addLabel( table.name().simpleName() )
                .build();
    }

    public GraphDataConfigSupplier supplierFor( Join join, Collection<Path> files )
    {
        return RelationshipConfig.builder()
                .addInputFiles( files )
                .build();
    }
}
