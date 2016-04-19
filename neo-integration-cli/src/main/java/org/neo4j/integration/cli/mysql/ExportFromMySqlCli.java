package org.neo4j.integration.cli.mysql;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

import com.github.rvesse.airline.annotations.Arguments;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;
import com.github.rvesse.airline.annotations.OptionType;
import com.github.rvesse.airline.annotations.restrictions.Required;
import org.apache.commons.lang3.StringUtils;

import org.neo4j.integration.commands.mysql.CreateCsvResources;
import org.neo4j.integration.commands.mysql.ExportFromMySql;
import org.neo4j.integration.environment.CsvDirectorySupplier;
import org.neo4j.integration.environment.DestinationDirectorySupplier;
import org.neo4j.integration.environment.Environment;
import org.neo4j.integration.environment.ImportToolDirectorySupplier;
import org.neo4j.integration.neo4j.importcsv.config.Formatting;
import org.neo4j.integration.neo4j.importcsv.config.ImportToolOptions;
import org.neo4j.integration.sql.ConnectionConfig;
import org.neo4j.integration.sql.DatabaseType;
import org.neo4j.integration.sql.exportcsv.mapping.CsvResources;
import org.neo4j.integration.sql.exportcsv.mysql.MySqlExportSqlSupplier;
import org.neo4j.integration.util.CliRunner;

import static java.lang.String.format;

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

    @Required
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
            description = "Path to destination store directory (this will overwrite any exiting directory).",
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
    @Arguments(description = "Path to an existing CSV resources definitions file. " +
            "The name 'stdin' will cause the CSV resources definitions to be read from standard input.",
            title = "file|stdin")
    private String csvResourcesFile;

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
//        try ( Reader reader = new InputStreamReader( System.in ); BufferedReader buffer = new BufferedReader( reader ) )
//        {
//            if (StringUtils.isNotEmpty( csvResourcesFile ))
//            {
//                if (csvResourcesFile.equalsIgnoreCase( "stdin" ))
//                {
//                    createCsvResources = CreateCsvResources.load( buffer );
//                }
//                else
//                {
//                    createCsvResources = CreateCsvResources.load( csvResourcesFile );
//                }
//            }
//            else
//            {
                createCsvResources = new CreateCsvResources(
                        new CreateCsvResourcesEventHandler(),
                        new OutputStream()
                        {
                            @Override
                            public void write( int b ) throws IOException
                            {
                                // Do nothing
                            }
                        },
                        connectionConfig,
                        formatting,
                        new MySqlExportSqlSupplier() );
//            }
//        }
        return createCsvResources.call();
    }

    private static class ExportMySqlEventHandler implements ExportFromMySql.Events
    {
        @Override
        public void onExportingToCsv( Path csvDirectory )
        {
            CliRunner.print( "Exporting from MySQL to CSV..." );
            CliRunner.print( format( "CSV directory: %s", csvDirectory ) );
        }

        @Override
        public void onCreatingNeo4jStore()
        {
            CliRunner.print( "Creating Neo4j store from CSV..." );
        }

        @Override
        public void onExportComplete( Path destinationDirectory )
        {
            CliRunner.printResult( destinationDirectory );
        }
    }

    private static class CreateCsvResourcesEventHandler implements CreateCsvResources.Events
    {
        @Override
        public void onCreatingCsvResourcesFile()
        {
            CliRunner.print( "Creating MySQL to CSV mappings..." );
        }

        @Override
        public void onCsvResourcesCreated()
        {
            // Do nothing
        }
    }
}
