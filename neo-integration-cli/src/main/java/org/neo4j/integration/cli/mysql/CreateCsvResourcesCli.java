package org.neo4j.integration.cli.mysql;

import java.nio.file.Paths;

import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;
import com.github.rvesse.airline.annotations.OptionType;
import com.github.rvesse.airline.annotations.restrictions.Required;

import org.neo4j.integration.FilterOptions;
import org.neo4j.integration.commands.mysql.CreateCsvResources;
import org.neo4j.integration.neo4j.importcsv.config.Formatting;
import org.neo4j.integration.neo4j.importcsv.config.ImportToolOptions;
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
            name = {"--options-file"},
            description = "Path to file containing Neo4j import tool options.",
            title = "file")
    private String importToolOptionsFile = "";

    @SuppressWarnings("FieldCanBeLocal")
    @Option(type = OptionType.COMMAND,
            name = {"--debug"},
            description = "Print detailed diagnostic output.")
    private boolean debug = false;

    @SuppressWarnings("FieldCanBeLocal")
    @Option(type = OptionType.COMMAND,
            name = {"--relationship-name", "--rel-name"},
            description = "Specifies whether to get the name for relationships from table names (table) or column names (column). Table is default.",
            title = "relationshipNameFrom")
    private String relationshipNameFrom = "table";

    @SuppressWarnings("FieldCanBeLocal")
    @Option(type = OptionType.COMMAND,
            name = {"--tiny-int"},
            description = "Specifies whether to get the convert TinyInts to byte (byte) or boolean (boolean). Byte is default.",
            title = "tinyIntAs")
    private String tinyIntAs = "byte";

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

            ImportToolOptions importToolOptions =
                    ImportToolOptions.initialiseFromFile( Paths.get( importToolOptionsFile ) );
            Formatting formatting = Formatting.builder()
                    .delimiter( importToolOptions.getDelimiter( this.delimiter ) )
                    .quote( importToolOptions.getQuoteCharacter( this.quote ) )
                    .build();


            new CreateCsvResources(
                    new CreateCsvResourcesEventHandler(),
                    System.out,
                    connectionConfig,
                    formatting,
                    new MySqlExportSqlSupplier(),
                    createFilterOptions() ).call();
        }
        catch ( Exception e )
        {
            CliRunner.handleException( e, debug );
        }
    }

    private FilterOptions createFilterOptions()
    {
        FilterOptions filterOptions = new FilterOptions(  );

        filterOptions.setTinyIntAs( tinyIntAs );
        filterOptions.setRelationshipNameFrom( relationshipNameFrom );

        return filterOptions;
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
