package org.neo4j.integration.sql.exportcsv.mapping;

import java.util.Collection;
import java.util.function.BiPredicate;

import org.neo4j.integration.neo4j.importcsv.config.Formatting;
import org.neo4j.integration.neo4j.importcsv.fields.CsvField;
import org.neo4j.integration.neo4j.importcsv.fields.IdSpace;
import org.neo4j.integration.sql.RowAccessor;
import org.neo4j.integration.sql.exportcsv.io.WriteRowWithNullsStrategy;
import org.neo4j.integration.sql.metadata.Column;
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

        builder.add( join.keyOneSourceColumn(),
                CsvField.startId( new IdSpace( join.keyOneSourceColumn().table().fullName() ) ) );
        builder.add( join.keyTwoSourceColumn(),
                CsvField.endId( new IdSpace( join.keyTwoTargetColumn().table().fullName() ) ) );

        String relationshipType = join.keyTwoTargetColumn().table().simpleName().toUpperCase();

        builder.add(
                new SimpleColumn( join.keyOneSourceColumn().table(),
                        formatting.quote().enquote( relationshipType ),
                        relationshipType,
                        ColumnType.Literal,
                        SqlDataType.RELATIONSHIP_TYPE_DATA_TYPE ),
                CsvField.relationshipType() );

        return builder.build();
    }

    @Override
    public BiPredicate<RowAccessor, Collection<Column>> writeRowWithNullsStrategy()
    {
        return new WriteRowWithNullsStrategy();
    }
}
