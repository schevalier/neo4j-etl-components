package org.neo4j.integration.mysql.exportcsvnew.config;

import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.neo4j.integration.mysql.exportcsvnew.metadata.Column;
import org.neo4j.integration.mysql.exportcsvnew.metadata.ColumnType;
import org.neo4j.integration.mysql.exportcsvnew.metadata.Table;
import org.neo4j.integration.neo4j.importcsv.config.CsvField;
import org.neo4j.integration.neo4j.importcsv.config.Delimiter;
import org.neo4j.integration.neo4j.importcsv.config.IdSpace;
import org.neo4j.integration.neo4j.importcsv.config.QuoteChar;

import static org.neo4j.integration.util.StringListBuilder.stringList;

public class ColumnToCsvFieldMappings
{
    public static ColumnToCsvFieldMappings forTable( Table table, QuoteChar quote )
    {
        ColumnToCsvFieldMappings config = new ColumnToCsvFieldMappings();

        for ( Column column : table.columns() )
        {
            switch ( column.type() )
            {
                case PrimaryKey:
                    config.addMapping( column, CsvField.id( new IdSpace( table.name().fullName() ) ) );
                    break;
                case Data:
                    config.addMapping( column, CsvField.data( column.name() ) );
                    break;
                default:
                    // Do nothing
                    break;
            }
        }

        config.addMapping(
                Column.builder()
                        .table( table.name() )
                        .name( quote.enquote( table.name().simpleName() ) )
                        .type( ColumnType.Literal )
                        .build(),
                CsvField.label() );

        return config;
    }


    private final Map<Column, CsvField> mappings = new LinkedHashMap<>();

    public void addMapping( Column from, CsvField to )
    {
        mappings.put( from, to );
    }

    public Collection<CsvField> fields()
    {
        return mappings.values();
    }

    public String sql( Path exportFile, Delimiter delimiter )
    {
        return "SELECT " +
                stringList( columns(), delimiter.value() ) +
                " INTO OUTFILE '" +
                exportFile.toString() +
                "' FIELDS TERMINATED BY '" +
                delimiter.value() +
                "' OPTIONALLY ENCLOSED BY ''" +
                " ESCAPED BY '\\\\'" +
                " LINES TERMINATED BY '\\n'" +
                " STARTING BY ''" +
                " FROM " + stringList( tableNames(), ", " );
    }

    private Collection<String> columns()
    {
        return mappings.keySet().stream().map( Column::name ).collect( Collectors.toSet() );
    }

    private Collection<String> tableNames()
    {
        return mappings.keySet().stream().map( c -> c.table().fullName() ).collect( Collectors.toSet() );
    }
}
