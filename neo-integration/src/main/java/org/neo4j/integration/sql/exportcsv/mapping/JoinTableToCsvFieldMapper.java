package org.neo4j.integration.sql.exportcsv.mapping;

import org.neo4j.integration.neo4j.importcsv.config.Formatting;
import org.neo4j.integration.neo4j.importcsv.config.QuoteChar;
import org.neo4j.integration.neo4j.importcsv.fields.CsvField;
import org.neo4j.integration.neo4j.importcsv.fields.IdSpace;
import org.neo4j.integration.sql.metadata.Column;
import org.neo4j.integration.sql.metadata.ColumnRole;
import org.neo4j.integration.sql.metadata.JoinTable;
import org.neo4j.integration.sql.metadata.SimpleColumn;
import org.neo4j.integration.sql.metadata.SqlDataType;
import org.neo4j.integration.sql.metadata.TableName;

class JoinTableToCsvFieldMapper implements DatabaseObjectToCsvFieldMapper<JoinTable>
{
    private final Formatting formatting;
    private final RelationshipNameResolver relationshipNameResolver;

    JoinTableToCsvFieldMapper( Formatting formatting, RelationshipNameResolver relationshipNameResolver )
    {
        this.formatting = formatting;
        this.relationshipNameResolver = relationshipNameResolver;
    }

    @Override
    public ColumnToCsvFieldMappings createMappings( JoinTable joinTable )
    {
        ColumnToCsvFieldMappings.Builder builder = ColumnToCsvFieldMappings.builder();

        CsvField to1 = CsvField.startId( new IdSpace( joinTable.join().keyOneTargetColumn().table().fullName() ) );
        builder.add( new ColumnToCsvFieldMapping( joinTable.join().keyOneSourceColumn(), to1 ) );

        CsvField to2 = CsvField.endId( new IdSpace( joinTable.join().keyTwoTargetColumn().table().fullName() ) );
        builder.add( new ColumnToCsvFieldMapping( joinTable.join().keyTwoSourceColumn(), to2 ) );

        TableName table = joinTable.joinTableName();

        String resolvedName = relationshipNameResolver.resolve( table.simpleName(),
                joinTable.join().keyTwoSourceColumn().alias() );
        String relationshipType = formatting.relationshipFormatter().format( resolvedName );

        SimpleColumn from = new SimpleColumn( table,
                QuoteChar.DOUBLE_QUOTES.enquote( relationshipType ),
                relationshipType,
                ColumnRole.Literal,
                SqlDataType.RELATIONSHIP_TYPE_DATA_TYPE );

        builder.add( new ColumnToCsvFieldMapping( from, CsvField.relationshipType() ) );

        addProperties( joinTable, builder );

        return builder.build();
    }

    private void addProperties( JoinTable joinTable, ColumnToCsvFieldMappings.Builder builder )
    {
        for ( Column column : joinTable.columns() )
        {
            switch ( column.role() )
            {
                case PrimaryKey:
                    CsvField id = CsvField.id( new IdSpace( joinTable.joinTableName().fullName() ) );
                    builder.add( new ColumnToCsvFieldMapping( column, id ) );
                    break;
                case Data:
                    column.addData( builder );
                    break;
                default:
                    // Do nothing
                    break;
            }
        }
    }
}
