package org.neo4j.mysql;

import java.nio.file.Path;

import org.neo4j.command_line.Commands;
import org.neo4j.mysql.config.ExportConfig;

import static org.neo4j.utils.StringListBuilder.stringList;

public class ExportFile
{
    private final ExportConfig config;

    public ExportFile( ExportConfig config )
    {
        this.config = config;
    }

    public Path create( String exportId ) throws Exception
    {
        String delimiter = config.formatting().delimiter().value();

        Path exportFile = config.destination().resolve( exportId );
        Commands.commands( "chmod", "0777", exportFile.toAbsolutePath().getParent().toString() ).execute().await();

        String sql = "SELECT " +
                stringList( config.table().columnNames(), delimiter ) +
                " INTO OUTFILE '" +
                exportFile.toString() +
                "' FIELDS TERMINATED BY '" +
                delimiter +
                "' OPTIONALLY ENCLOSED BY ''" +
                " ESCAPED BY '\\\\'" +
                " LINES TERMINATED BY '\\n'" +
                " STARTING BY ''" +
                " FROM " + config.table().name();

        SqlRunner sqlRunner = new SqlRunner( config.connectionConfig(), sql );
        sqlRunner.execute().await();

        return exportFile;
    }
}
