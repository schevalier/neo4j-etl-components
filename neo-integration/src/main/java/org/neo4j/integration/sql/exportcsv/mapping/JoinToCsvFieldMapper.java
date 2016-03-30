package org.neo4j.integration.sql.exportcsv.mapping;

import org.neo4j.integration.neo4j.importcsv.config.Formatting;
import org.neo4j.integration.neo4j.importcsv.fields.CsvField;
import org.neo4j.integration.neo4j.importcsv.fields.IdSpace;
import org.neo4j.integration.sql.metadata.ColumnType;
import org.neo4j.integration.sql.metadata.Join;
import org.neo4j.integration.sql.metadata.SimpleColumn;
import org.neo4j.integration.sql.metadata.SqlDataType;

public class JoinToCsvFieldMapper implements DatabaseObjectToCsvFieldMapper<Join>
{
    private final Formatting formatting;

    public JoinToCsvFieldMapper( Formatting formatting )
    {
        this.formatting = formatting;
    }

    @Override
    public ColumnToCsvFieldMappings createMappings( Join join )
    {
        ColumnToCsvFieldMappings.Builder builder = ColumnToCsvFieldMappings.builder();

        final CsvField to = CsvField.startId( new IdSpace( join.keyOneSourceColumn().table().fullName() ) );
        builder.add(
                new ColumnToCsvFieldMapping( join.keyOneSourceColumn(), to ) );
        final CsvField to1 = CsvField.endId( new IdSpace( join.keyTwoTargetColumn().table().fullName() ) );
        builder.add(
                new ColumnToCsvFieldMapping( join.keyTwoSourceColumn(), to1 ) );

        String relationshipType =
                formatting.relationshipFormatter().format( join.keyTwoTargetColumn().table().simpleName() );

        final SimpleColumn from = new SimpleColumn( join.keyOneSourceColumn().table(),
                formatting.quote().enquote( relationshipType ),
                relationshipType,
                ColumnType.Literal,
                SqlDataType.RELATIONSHIP_TYPE_DATA_TYPE );
        builder.add(
                new ColumnToCsvFieldMapping( from, CsvField.relationshipType() ) );

        return builder.build();
    }
}
