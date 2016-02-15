package org.neo4j.integration.sql.exportcsv.mapping;

import org.neo4j.integration.sql.metadata.Column;
import org.neo4j.integration.sql.metadata.ColumnType;
import org.neo4j.integration.sql.metadata.Join;
import org.neo4j.integration.neo4j.importcsv.fields.CsvField;
import org.neo4j.integration.neo4j.importcsv.config.Formatting;
import org.neo4j.integration.neo4j.importcsv.fields.IdSpace;

public class JoinMapper implements Mapper<Join>
{
    private final Formatting formatting;

    public JoinMapper( Formatting formatting )
    {
        this.formatting = formatting;
    }

    @Override
    public ColumnToCsvFieldMappings createMappings( Join join )
    {
        ColumnToCsvFieldMappings.Builder builder = ColumnToCsvFieldMappings.builder();

        builder.add( join.primaryKey(), CsvField.startId( new IdSpace( join.primaryKey().table().fullName() ) ) );
        builder.add( join.foreignKey(), CsvField.endId( new IdSpace( join.childTable().fullName() ) ) );
        builder.add(
                Column.builder()
                        .table( join.primaryKey().table() )
                        .name( formatting.quote().enquote( join.childTable().simpleName() ) )
                        .alias( join.childTable().simpleName() )
                        .type( ColumnType.Literal ).build(),
                CsvField.relationshipType() );

        return builder.build();
    }
}
