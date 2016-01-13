package org.neo4j.integration.mysql.exportcsvnew.config;

import org.neo4j.integration.mysql.exportcsvnew.metadata.Column;
import org.neo4j.integration.mysql.exportcsvnew.metadata.ColumnType;
import org.neo4j.integration.mysql.exportcsvnew.metadata.Join;
import org.neo4j.integration.neo4j.importcsv.config.CsvField;
import org.neo4j.integration.neo4j.importcsv.config.IdSpace;
import org.neo4j.integration.neo4j.importcsv.config.QuoteChar;

public class JoinMapper implements Mapper<Join>
{
    @Override
    public ColumnToCsvFieldMappings createExportCsvConfigFor( Join join, QuoteChar quote )
    {
        ColumnToCsvFieldMappings config = new ColumnToCsvFieldMappings();

        config.addMapping( join.parentKey(), CsvField.startId( new IdSpace( join.parentKey().table().fullName() ) ) );
        config.addMapping( join.childKey(), CsvField.startId( new IdSpace( join.childKey().table().fullName() ) ) );
        config.addMapping(
                Column.builder()
                        .table( join.childKey().table() )
                        .name( quote.enquote( join.childKey().table().simpleName() ) )
                        .type( ColumnType.Literal ).build(),
                CsvField.relationshipType() );

        return config;
    }
}
