package org.neo4j.integration.sql.exportcsv.mapping;

import org.neo4j.integration.neo4j.importcsv.config.Formatting;
import org.neo4j.integration.neo4j.importcsv.config.QuoteChar;
import org.neo4j.integration.neo4j.importcsv.fields.CsvField;
import org.neo4j.integration.neo4j.importcsv.fields.IdSpace;
import org.neo4j.integration.sql.metadata.Column;
import org.neo4j.integration.sql.metadata.ColumnRole;
import org.neo4j.integration.sql.metadata.ColumnValueSelectionStrategy;
import org.neo4j.integration.sql.metadata.Join;
import org.neo4j.integration.sql.metadata.SimpleColumn;
import org.neo4j.integration.sql.metadata.SqlDataType;

class JoinToCsvFieldMapper implements DatabaseObjectToCsvFieldMapper<Join>
{
    private final Formatting formatting;
    private RelationshipNameResolver relationshipNameResolver;

    JoinToCsvFieldMapper( Formatting formatting, RelationshipNameResolver relationshipNameResolver )
    {
        this.formatting = formatting;
        this.relationshipNameResolver = relationshipNameResolver;
    }

    @Override
    public ColumnToCsvFieldMappings createMappings( Join join )
    {
        ColumnToCsvFieldMappings.Builder builder = ColumnToCsvFieldMappings.builder();

        CsvField from = CsvField.startId( new IdSpace( join.keyOneSourceColumn().table().fullName() ) );
        builder.add( new ColumnToCsvFieldMapping( join.keyOneSourceColumn(), from ) );

        CsvField to = CsvField.endId( new IdSpace( join.keyTwoTargetColumn().table().fullName() ) );
        builder.add( new ColumnToCsvFieldMapping( join.keyTwoSourceColumn(), to ) );

        String tableName = join.keyTwoTargetColumn().table().simpleName();
        String columnName = join.keyTwoSourceColumn().alias();

        String relationshipType =
                formatting.relationshipFormatter().format( relationshipNameResolver.resolve( tableName, columnName ) );

        Column relationshipTypeColumn = new SimpleColumn( join.keyOneSourceColumn().table(),
                QuoteChar.DOUBLE_QUOTES.enquote( relationshipType ),
                relationshipType,
                ColumnRole.Literal,
                SqlDataType.RELATIONSHIP_TYPE_DATA_TYPE, ColumnValueSelectionStrategy.SelectColumnValue );
        builder.add( new ColumnToCsvFieldMapping( relationshipTypeColumn, CsvField.relationshipType() ) );

        return builder.build();
    }
}
