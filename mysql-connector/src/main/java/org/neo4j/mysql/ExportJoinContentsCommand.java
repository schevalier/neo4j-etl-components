package org.neo4j.mysql;

import java.nio.file.Path;

import org.neo4j.command_line.Commands;
import org.neo4j.mysql.config.ExportProperties;
import org.neo4j.mysql.config.Join;
import org.neo4j.mysql.config.TableName;

import static java.lang.String.format;

import static org.neo4j.utils.StringListBuilder.stringList;

public class ExportJoinContentsCommand
{
    private final ExportProperties properties;

    public ExportJoinContentsCommand( ExportProperties properties )
    {
        this.properties = properties;
    }

    public Path execute( Join join, String exportId ) throws Exception
    {
        String delimiter = properties.formatting().delimiter().value();

        Path exportFile = properties.destination().resolve( format( "%s.csv", exportId ) );
        Commands.commands( "chmod", "0777", exportFile.getParent().toString() ).execute().await();

        String sql = "SELECT " +
                stringList( join.columns(), delimiter ) +
                " INTO OUTFILE '" +
                exportFile.toString() +
                "' FIELDS TERMINATED BY '" +
                delimiter +
                "' OPTIONALLY ENCLOSED BY ''" +
                " ESCAPED BY '\\\\'" +
                " LINES TERMINATED BY '\\n'" +
                " STARTING BY ''" +
                " FROM " + stringList( join.tableNames(), ", ", TableName::fullName );

        SqlRunner sqlRunner = new SqlRunner( properties.connectionConfig(), sql );
        sqlRunner.execute().await();

        return exportFile;
    }
}
