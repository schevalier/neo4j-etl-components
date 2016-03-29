package org.neo4j.integration.sql.exportcsv.mysql.schema;

import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;

import org.neo4j.integration.sql.DatabaseClient;
import org.neo4j.integration.sql.metadata.ColumnType;
import org.neo4j.integration.sql.metadata.Join;
import org.neo4j.integration.sql.metadata.JoinTable;
import org.neo4j.integration.sql.metadata.JoinTableInfo;
import org.neo4j.integration.sql.metadata.MetadataProducer;
import org.neo4j.integration.sql.metadata.Table;
import org.neo4j.integration.util.Loggers;

import static java.lang.String.format;

public class JoinTableMetadataProducer implements MetadataProducer<JoinTableInfo, JoinTable>
{
    private final JoinMetadataProducer joinMetadataProducer;
    private final TableMetadataProducer tableMetadataProducer;

    public JoinTableMetadataProducer( DatabaseClient databaseClient )
    {
        this.joinMetadataProducer = new JoinMetadataProducer( databaseClient );
        this.tableMetadataProducer = new TableMetadataProducer( databaseClient, c -> c == ColumnType.Data );
    }

    @Override
    public Collection<JoinTable> createMetadataFor( JoinTableInfo source ) throws Exception
    {
        Loggers.Default.log( Level.INFO, format( "Generating join table metadata for join table [%s <- %s -> %s]",
                source.tableOne(), source.table(), source.tableTwo() ) );
        Collection<Join> joins = joinMetadataProducer.createMetadataFor( source );
        Collection<Table> tables = tableMetadataProducer.createMetadataFor( source.joinTableName() );

        return Collections.singletonList(
                new JoinTable( joins.stream().findFirst().get(), tables.stream().findFirst().get() ) );
    }
}
