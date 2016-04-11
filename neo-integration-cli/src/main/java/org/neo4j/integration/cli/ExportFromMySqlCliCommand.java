package org.neo4j.integration.cli;

import java.nio.file.Paths;
import java.util.Optional;

import io.airlift.airline.Command;
import io.airlift.airline.Option;
import io.airlift.airline.OptionType;
import org.apache.commons.lang3.StringUtils;

import org.neo4j.integration.commands.Environment;
import org.neo4j.integration.commands.ExportFromMySqlCommand;
import org.neo4j.integration.neo4j.importcsv.config.Delimiter;
import org.neo4j.integration.neo4j.importcsv.config.QuoteChar;

@Command(name = "mysql-export", description = "Export from MySQL.")
public class ExportFromMySqlCliCommand implements Runnable
{
    @SuppressWarnings("FieldCanBeLocal")
    @Option(type = OptionType.COMMAND,
            name = {"-h", "--host"},
            description = "Host to use for connection to MySQL.",
            title = "name",
            required = false)
    private String host = "localhost";

    @SuppressWarnings("FieldCanBeLocal")
    @Option(type = OptionType.COMMAND,
            name = {"-p", "--port"},
            description = "Port number to use for connection to MySQL.",
            title = "#",
            required = false)
    private int port = 3306;

    @Option(type = OptionType.COMMAND,
            name = {"-u", "--user"},
            description = "User for login to MySQL.",
            title = "name",
            required = true)
    private String user;

    @Option(type = OptionType.COMMAND,
            name = {"--password"},
            description = "Password for login to MySQL.",
            title = "name",
            required = true)
    private String password;

    @Option(type = OptionType.COMMAND,
            name = {"-d", "--database"},
            description = "MySQL database.",
            title = "name",
            required = true)
    private String database;

    @Option(type = OptionType.COMMAND,
            name = {"--import-tool"},
            description = "Path to directory containing Neo4j import tool.",
            title = "directory",
            required = true)
    private String importToolDirectory;

    @Option(type = OptionType.COMMAND,
            name = {"--csv-directory"},
            description = "Path to directory for intermediate CSV files.",
            title = "directory",
            required = true)
    private String csvRootDirectory;

    @Option(type = OptionType.COMMAND,
            name = {"--destination"},
            description = "Path to destination store directory (this will overwrite any exiting directory).",
            title = "directory",
            required = true)
    private String destinationDirectory;

    @SuppressWarnings("FieldCanBeLocal")
    @Option(type = OptionType.COMMAND,
            name = {"--force"},
            description = "Force delete destination store directory if it already exists.",
            title = "boolean")
    private boolean force = false;

    @SuppressWarnings("FieldCanBeLocal")
    @Option(type = OptionType.COMMAND,
            name = {"--delimiter"},
            description = "Delimiter to separate fields in CSV",
            title = "delimiter",
            required = false)
    private String delimiter;

    @SuppressWarnings("FieldCanBeLocal")
    @Option(type = OptionType.COMMAND,
            name = {"--quote"},
            description = "Character to treat as quotation character for values in CSV data",
            title = "quote",
            required = false)
    private String quote;

    @Override
    public void run()
    {
        try
        {
            Delimiter delimiter = new Delimiter( Optional.ofNullable( this.delimiter ).orElse( "," ) );
            QuoteChar quoteChar = QuoteChar.DOUBLE_QUOTES;
            if ( StringUtils.isNotEmpty( quote ) )
            {
                quoteChar = new QuoteChar( quote, quote );
            }

            new ExportFromMySqlCommand(
                    host,
                    port,
                    user,
                    password,
                    database,
                    delimiter,
                    quoteChar,
                    new Environment(
                            Paths.get( importToolDirectory ),
                            Paths.get( destinationDirectory ),
                            Paths.get( csvRootDirectory ),
                            force ) )
                    .execute();
        }
        catch ( Exception e )
        {
            e.printStackTrace( System.err );
            System.exit( -1 );
        }
    }
}
