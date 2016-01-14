package org.neo4j.integration.mysql.exportcsv.sql;

import java.nio.file.Path;

import org.neo4j.integration.mysql.exportcsv.config.ColumnToCsvFieldMappings;
import org.neo4j.integration.neo4j.importcsv.config.Delimiter;

import static org.neo4j.integration.util.StringListBuilder.stringList;

public class JoinSqlSupplier implements SqlSupplier
{
    private final ColumnToCsvFieldMappings mappings;

    public JoinSqlSupplier( ColumnToCsvFieldMappings mappings )
    {
        this.mappings = mappings;
    }

    @Override
    public String sql( Path exportFile, Delimiter delimiter )
    {
        return "SELECT " +
                stringList( mappings.columns(), delimiter.value() ) +
                " INTO OUTFILE '" +
                exportFile.toString() +
                "' FIELDS TERMINATED BY '" +
                delimiter.value() +
                "' OPTIONALLY ENCLOSED BY ''" +
                " ESCAPED BY '\\\\'" +
                " LINES TERMINATED BY '\\n'" +
                " STARTING BY ''" +
                " FROM " + stringList( mappings.tableNames(), ", " );
    }
}
