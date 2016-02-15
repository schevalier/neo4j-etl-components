package org.neo4j.integration.sql.exportcsv.mysql;

import java.nio.file.Path;
import java.util.Collection;
import java.util.stream.Collectors;

import org.neo4j.integration.neo4j.importcsv.config.Formatting;
import org.neo4j.integration.sql.exportcsv.ExportSqlSupplier;
import org.neo4j.integration.sql.exportcsv.mapping.ColumnToCsvFieldMappings;
import org.neo4j.integration.sql.metadata.Column;

import static java.lang.String.format;

import static org.neo4j.integration.util.StringListBuilder.stringList;

public class MySqlJoinExportSqlSupplier implements ExportSqlSupplier
{
    private final Formatting formatting;

    public MySqlJoinExportSqlSupplier( Formatting formatting )
    {
        this.formatting = formatting;
    }

    @Override
    public String sql( ColumnToCsvFieldMappings mappings, Path exportFile )
    {
        String delimiter = formatting.delimiter().value();

//        return "SELECT " +
//                stringList( mappings.columns(), delimiter ) +
//                " INTO OUTFILE '" +
//                exportFile.toString() +
//                "' FIELDS TERMINATED BY '" +
//                delimiter +
//                "' OPTIONALLY ENCLOSED BY ''" +
//                " ESCAPED BY '\\\\'" +
//                " LINES TERMINATED BY '\\n'" +
//                " STARTING BY ''" +
//                " FROM " + stringList( mappings.tableNames(), ", " );

        return "SELECT " +
                stringList( aliased( mappings.columns() ), delimiter ) +
                " FROM " + stringList( mappings.tableNames(), ", " );
    }

    private Collection<String> aliased( Collection<Column> columns )
    {
        return columns.stream()
                .map( c -> format( "%s AS %s", c.name(), c.alias() ) )
                .collect( Collectors.toList() );
    }
}
