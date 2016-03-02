package org.neo4j.integration.sql.exportcsv.mapping;

import org.neo4j.integration.neo4j.importcsv.config.Formatting;
import org.neo4j.integration.neo4j.importcsv.fields.CsvField;
import org.neo4j.integration.neo4j.importcsv.fields.IdSpace;
import org.neo4j.integration.sql.metadata.Column;
import org.neo4j.integration.sql.metadata.ColumnType;
import org.neo4j.integration.sql.metadata.JoinTable;
import org.neo4j.integration.sql.metadata.SqlDataType;
import org.neo4j.integration.sql.metadata.TableName;

public class JoinTableToCsvFieldMapper implements DatabaseObjectToCsvFieldMapper<JoinTable>
{
    private final Formatting formatting;

    public JoinTableToCsvFieldMapper( Formatting formatting )
    {
        this.formatting = formatting;
    }

    @Override
    public ColumnToCsvFieldMappings createMappings( JoinTable joinTable )
    {
        ColumnToCsvFieldMappings.Builder builder = ColumnToCsvFieldMappings.builder();

        builder.add( joinTable.startForeignKey(),
                CsvField.startId( new IdSpace( joinTable.startPrimaryKey().table().fullName() ) ) );
        builder.add( joinTable.endForeignKey(),
                CsvField.endId( new IdSpace( joinTable.endPrimaryKey().table().fullName() ) ) );

        TableName table = joinTable.joinTableName();

        String relationshipType = table.simpleName().toUpperCase();

        builder.add(
                Column.builder()
                        .table( table )
                        .name( formatting.quote().enquote( relationshipType ) )
                        .alias( relationshipType )
                        .columnType( ColumnType.Literal )
                        .dataType( SqlDataType.RELATIONSHIP_TYPE_DATA_TYPE )
                        .build(),
                CsvField.relationshipType() );

        return builder.build();
    }

}
