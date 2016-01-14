package org.neo4j.integration.mysql.exportcsv.sql;

import java.nio.file.Path;

import org.neo4j.integration.mysql.exportcsv.mapping.ColumnToCsvFieldMappings;
import org.neo4j.integration.neo4j.importcsv.config.Formatting;

import static org.neo4j.integration.util.StringListBuilder.stringList;

public class JoinExportSqlSupplier implements ExportSqlSupplier
{
    private final Formatting formatting;

    public JoinExportSqlSupplier( Formatting formatting )
    {
        this.formatting = formatting;
    }

    @Override
    public String sql( ColumnToCsvFieldMappings mappings, Path exportFile )
    {
        String delimiter = formatting.delimiter().value();

        return "SELECT " +
                stringList( mappings.columns(), delimiter ) +
                " INTO OUTFILE '" +
                exportFile.toString() +
                "' FIELDS TERMINATED BY '" +
                delimiter +
                "' OPTIONALLY ENCLOSED BY ''" +
                " ESCAPED BY '\\\\'" +
                " LINES TERMINATED BY '\\n'" +
                " STARTING BY ''" +
                " FROM " + stringList( mappings.tableNames(), ", " );
    }
}
