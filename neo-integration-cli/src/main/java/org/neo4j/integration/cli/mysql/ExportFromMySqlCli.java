package org.neo4j.integration.cli.mysql;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import com.github.rvesse.airline.annotations.Arguments;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;
import com.github.rvesse.airline.annotations.OptionType;
import com.github.rvesse.airline.annotations.restrictions.MutuallyExclusiveWith;
import com.github.rvesse.airline.annotations.restrictions.Required;
import org.apache.commons.lang3.StringUtils;

import org.neo4j.integration.commands.mysql.CreateCsvResources;
import org.neo4j.integration.commands.mysql.ExportFromMySql;
import org.neo4j.integration.environment.CsvDirectorySupplier;
import org.neo4j.integration.environment.DestinationDirectorySupplier;
import org.neo4j.integration.environment.Environment;
import org.neo4j.integration.environment.ImportToolDirectorySupplier;
import org.neo4j.integration.neo4j.importcsv.config.formatting.Formatting;
import org.neo4j.integration.neo4j.importcsv.config.formatting.ImportToolOptions;
import org.neo4j.integration.sql.ConnectionConfig;
import org.neo4j.integration.sql.DatabaseType;
import org.neo4j.integration.sql.exportcsv.mapping.CsvResources;
import org.neo4j.integration.sql.exportcsv.mapping.FilterOptions;
import org.neo4j.integration.sql.exportcsv.mysql.MySqlExportSqlSupplier;
import org.neo4j.integration.util.CliRunner;

@Command(name = "export", description = "Export from MySQL.")
public class ExportFromMySqlCli implements Runnable
{
    @SuppressWarnings("FieldCanBeLocal")
    @Option(type = OptionType.COMMAND,
            name = {"-h", "--host"},
            description = "Host to use for connection to MySQL.",
            title = "name")
    private String host = "localhost";

    @SuppressWarnings("FieldCanBeLocal")
    @Option(type = OptionType.COMMAND,
            name = {"-p", "--port"},
            description = "Port number to use for connection to MySQL.",
            title = "#")
    private int port = 3306;

    @Required
    @Option(type = OptionType.COMMAND,
            name = {"-u", "--user"},
            description = "User for login to MySQL.",
            title = "name")
    private String user;

    @Option(type = OptionType.COMMAND,
            name = {"--password"},
            description = "Password for login to MySQL.",
            title = "name")
    private String password;

    @Required
    @Option(type = OptionType.COMMAND,
            name = {"-d", "--database"},
            description = "MySQL database.",
            title = "name")
    private String database;

    @Required
    @Option(type = OptionType.COMMAND,
            name = {"--import-tool"},
            description = "Path to directory containing Neo4j import tool.",
            title = "directory")
    private String importToolDirectory;

    @SuppressWarnings("FieldCanBeLocal")
    @Option(type = OptionType.COMMAND,
            name = {"--options-file"},
            description = "Path to file containing Neo4j import tool options.",
            title = "file")
    private String importToolOptionsFile = "";

    @Required
    @Option(type = OptionType.COMMAND,
            name = {"--csv-directory"},
            description = "Path to directory for intermediate CSV files.",
            title = "directory")
    private String csvRootDirectory;

    @Required
    @Option(type = OptionType.COMMAND,
            name = {"--destination"},
            description = "Path to destination store directory.",
            title = "directory")
    private String destinationDirectory;

    @SuppressWarnings("FieldCanBeLocal")
    @Option(type = OptionType.COMMAND,
            name = {"--force"},
            description = "Force delete destination store directory if it already exists.")
    private boolean force = false;

    @SuppressWarnings("FieldCanBeLocal")
    @Option(type = OptionType.COMMAND,
            name = {"--delimiter"},
            description = "Delimiter to separate fields in CSV.",
            title = "delimiter")
    private String delimiter;

    @SuppressWarnings("FieldCanBeLocal")
    @Option(type = OptionType.COMMAND,
            name = {"--quote"},
            description = "Character to treat as quotation character for values in CSV data.",
            title = "quote")
    private String quote;

    @SuppressWarnings("FieldCanBeLocal")
    @Option(type = OptionType.COMMAND,
            name = {"--debug"},
            description = "Print detailed diagnostic output.")
    private boolean debug = false;

    @Option(type = OptionType.COMMAND,
            name = {"--csv-resources"},
            description = "Path to an existing CSV resources definitions file. " +
                    "The name 'stdin' will cause the CSV resources definitions to be read from standard input.",
            title = "file|stdin")
    private String csvResourcesFile;

    @SuppressWarnings("FieldCanBeLocal")
    @Option(type = OptionType.COMMAND,
            name = {"--relationship-name", "--rel-name"},
            description = "Specifies whether to get the name for relationships from table names (table) or column " +
                    "names (column). Table is default.",
            title = "relationshipNameFrom")
    private String relationshipNameFrom = "table";

    @SuppressWarnings("FieldCanBeLocal")
    @Option(type = OptionType.COMMAND,
            name = {"--tiny-int", "--tiny"},
            description = "Specifies whether to get the convert TinyInts to byte (byte) or boolean (boolean). Byte is" +
                    " default.",
            title = "tinyIntAs")
    private String tinyIntAs = "byte";

    @SuppressWarnings("FieldCanBeLocal")
    @Arguments(description = "Specifies tables to exclude from the process.",
            title = "tablesToExclude")
    @Option(type = OptionType.COMMAND,
            name = {"--exclude", "--exc"},
            description = "Specifies tables to exclude from the process.",
            title = "tablesToExclude")
    @MutuallyExclusiveWith(tag = "exc/inc")
    private List<String> tablesToExclude = new ArrayList<String>();

    @Override
    public void run()
    {
        try
        {
            ConnectionConfig connectionConfig = ConnectionConfig.forDatabase( DatabaseType.MySQL )
                    .host( host )
                    .port( port )
                    .database( database )
                    .username( user )
                    .password( password )
                    .build();

            Environment environment = new Environment(
                    new ImportToolDirectorySupplier( Paths.get( importToolDirectory ) ).supply(),
                    new DestinationDirectorySupplier( Paths.get( destinationDirectory ), force ).supply(),
                    new CsvDirectorySupplier( Paths.get( csvRootDirectory ) ).supply(),
                    ImportToolOptions.initialiseFromFile( Paths.get( importToolOptionsFile ) ) );

            ImportToolOptions importToolOptions = environment.importToolOptions();

            Formatting formatting = Formatting.builder()
                    .delimiter( importToolOptions.getDelimiter( delimiter ) )
                    .quote( importToolOptions.getQuoteCharacter( quote ) )
                    .build();

            CsvResources csvResources = createCsvResources( connectionConfig, formatting );

            new ExportFromMySql(
                    new ExportMySqlEventHandler(),
                    csvResources,
                    connectionConfig,
                    formatting,
                    environment ).call();
        }
        catch ( Exception e )
        {
            CliRunner.handleException( e, debug );
        }
    }

    private CsvResources createCsvResources( ConnectionConfig connectionConfig, Formatting formatting ) throws Exception
    {
        Callable<CsvResources> createCsvResources;

        if ( StringUtils.isNotEmpty( csvResourcesFile ) )
        {
            createCsvResources = CreateCsvResourcesCli.csvResourcesFromFile( csvResourcesFile );
        }
        else
        {
            createCsvResources = new CreateCsvResources(
                    new CreateCsvResourcesEventHandler(),
                    emptyOutputStream(),
                    connectionConfig,
                    formatting,
                    new MySqlExportSqlSupplier(),
                    new FilterOptions( tinyIntAs, relationshipNameFrom, tablesToExclude ) );
        }

        return createCsvResources.call();
    }

    private OutputStream emptyOutputStream()
    {
        return new OutputStream()
        {
            @Override
            public void write( int b ) throws IOException
            {
                // Do nothing
            }
        };
    }

}
