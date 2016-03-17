package org.neo4j.integration.sql.exportcsv.mysql.schema;

import java.util.Collection;
import java.util.Collections;

import org.neo4j.integration.sql.DatabaseClient;
import org.neo4j.integration.sql.metadata.ColumnType;
import org.neo4j.integration.sql.metadata.Join;
import org.neo4j.integration.sql.metadata.JoinTable;
import org.neo4j.integration.sql.metadata.JoinTableInfo;
import org.neo4j.integration.sql.metadata.MetadataProducer;
import org.neo4j.integration.sql.metadata.Table;

public class JoinTableMetadataProducer implements MetadataProducer<JoinTableInfo, JoinTable>
{
    private DatabaseClient databaseClient;

    public JoinTableMetadataProducer( DatabaseClient databaseClient )
    {
        this.databaseClient = databaseClient;
    }

    @Override
    public Collection<JoinTable> createMetadataFor( JoinTableInfo source ) throws Exception
    {
        Collection<Join> joins = new JoinMetadataProducer( databaseClient ).createMetadataFor( source );
        Collection<Table> tables =
                new TableMetadataProducer( databaseClient, c -> c == ColumnType.Data )
                        .createMetadataFor( source.joinTableName() );

        return Collections.singletonList(
                new JoinTable( joins.stream().findFirst().get(), tables.stream().findFirst().get() ) );
    }
}
