package org.neo4j.integration.cli.mysql;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import io.airlift.airline.Command;
import io.airlift.airline.Option;
import io.airlift.airline.OptionType;
import org.apache.commons.lang3.StringUtils;

import org.neo4j.integration.commands.mysql.CreateCsvResources;
import org.neo4j.integration.environment.CsvDirectorySupplier;
import org.neo4j.integration.neo4j.importcsv.config.Delimiter;
import org.neo4j.integration.neo4j.importcsv.config.Formatting;
import org.neo4j.integration.neo4j.importcsv.config.QuoteChar;
import org.neo4j.integration.sql.ConnectionConfig;
import org.neo4j.integration.sql.DatabaseType;
import org.neo4j.integration.sql.exportcsv.mysql.MySqlExportSqlSupplier;
import org.neo4j.integration.util.CliRunner;

@Command(name = "create-csv-resources", description = "Create MySQL to CSV mapping files.")
public class CreateCsvResourcesCli implements Runnable
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
            name = {"--csv-directory"},
            description = "Path to directory for intermediate CSV files.",
            title = "directory",
            required = true)
    private String csvRootDirectory;

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

    @SuppressWarnings("FieldCanBeLocal")
    @Option(type = OptionType.COMMAND,
            name = {"--debug"},
            description = "Print detailed diagnostic output.")
    private boolean debug = false;

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

            Formatting formatting = Formatting.builder().delimiter( delimiter ).quote( quoteChar ).build();

            ConnectionConfig connectionConfig = ConnectionConfig.forDatabase( DatabaseType.MySQL )
                    .host( host )
                    .port( port )
                    .database( database )
                    .username( user )
                    .password( password )
                    .build();

            new CreateCsvResources(
                    new CreateCsvResourcesEventHandler(),
                    new CsvDirectorySupplier( Paths.get( csvRootDirectory ) ).supply(),
                    connectionConfig,
                    formatting,
                    new MySqlExportSqlSupplier() ).call();
        }
        catch ( Exception e )
        {
            e.printStackTrace( System.err );
            System.exit( -1 );
        }
    }

    private static class CreateCsvResourcesEventHandler implements CreateCsvResources.Events
    {
        @Override
        public void onCreatingCsvMappings()
        {
            CliRunner.print( "Creating MySQL to CSV mappings..." );
        }

        @Override
        public void onMappingsCreated( Path mappingsFile )
        {
            CliRunner.printResult( mappingsFile );
        }
    }
}
