package org.neo4j.integration.sql.exportcsv.mysql;

import java.nio.file.Path;

import org.neo4j.integration.sql.exportcsv.ExportSqlSupplier;
import org.neo4j.integration.sql.exportcsv.mapping.ColumnToCsvFieldMappings;
import org.neo4j.integration.neo4j.importcsv.config.Formatting;

import static org.neo4j.integration.util.StringListBuilder.stringList;

public class MySqlTableExportSqlSupplier implements ExportSqlSupplier
{
    private final Formatting formatting;

    public MySqlTableExportSqlSupplier( Formatting formatting )
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
