package org.neo4j.integration.cli;

import java.nio.file.Paths;

import io.airlift.airline.Command;
import io.airlift.airline.Option;
import io.airlift.airline.OptionType;

import org.neo4j.integration.commands.Environment;
import org.neo4j.integration.commands.ExportFromMySqlCommand;

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

    @Option(type = OptionType.COMMAND,
            name = {"--start"},
            description = "Table representing nodes at the start of the relationship.",
            title = "name",
            required = true)
    private String startTable;

    @Option(type = OptionType.COMMAND,
            name = {"--end"},
            description = "Table representing nodes at the end of the relationship.",
            title = "name",
            required = true)
    private String endTable;

    @Option(type = OptionType.COMMAND,
            name = {"--force"},
            description = "Force delete destination store directory if it already exists.",
            title = "boolean")
    private boolean force = false;

    @Override
    public void run()
    {
        try
        {
            new ExportFromMySqlCommand(
                    host,
                    port,
                    user,
                    password,
                    database,
                    startTable,
                    endTable,
                    new Environment(
                            Paths.get( importToolDirectory ),
                            Paths.get( destinationDirectory ),
                            Paths.get( csvRootDirectory ),
                            force )
            ).execute();
        }
        catch ( Exception e )
        {
            e.printStackTrace( System.err );
            System.exit( -1 );
        }
    }
}
