package org.neo4j.integration.commands;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

import io.airlift.airline.Command;
import io.airlift.airline.Option;
import io.airlift.airline.OptionType;

import org.neo4j.integration.SqlToGraphConfigMapper;
import org.neo4j.integration.neo4j.importcsv.ImportFromCsvCommand;
import org.neo4j.integration.neo4j.importcsv.config.Formatting;
import org.neo4j.integration.neo4j.importcsv.config.GraphConfig;
import org.neo4j.integration.neo4j.importcsv.config.ImportConfig;
import org.neo4j.integration.neo4j.importcsv.fields.IdType;
import org.neo4j.integration.sql.DatabaseType;
import org.neo4j.integration.sql.DatabaseClient;
import org.neo4j.integration.sql.exportcsv.ExportToCsvCommand;
import org.neo4j.integration.sql.exportcsv.ExportToCsvResults;
import org.neo4j.integration.sql.exportcsv.config.ExportToCsvConfig;
import org.neo4j.integration.sql.exportcsv.mysql.MySqlExportProvider;
import org.neo4j.integration.sql.exportcsv.mysql.schema.JoinMetadataProducer;
import org.neo4j.integration.sql.exportcsv.mysql.schema.TableMetadataProducer;
import org.neo4j.integration.sql.ConnectionConfig;
import org.neo4j.integration.sql.metadata.Join;
import org.neo4j.integration.sql.metadata.Table;
import org.neo4j.integration.sql.metadata.TableName;
import org.neo4j.integration.sql.metadata.TableNamePair;

import static java.lang.String.format;

@Command(name = "mysql-export",
        description = "Export from MySQL.")
public class ExportFromMySqlCommand implements Runnable
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
    private String importToolPath;

    @Option(type = OptionType.COMMAND,
            name = {"--destination"},
            description = "Path to destination directory.",
            title = "directory",
            required = true)
    private String destinationPath;

    @Option(type = OptionType.COMMAND,
            name = {"--parent"},
            description = "Parent MySQL table.",
            title = "name",
            required = true)
    private String parentTable;

    @Option(type = OptionType.COMMAND,
            name = {"--child"},
            description = "Child MySQL table.",
            title = "name",
            required = true)
    private String childTable;

    @Override
    public void run()
    {
        try
        {
            OutputDirectories outputDirectories = OutputDirectories.create( Paths.get( destinationPath ) );

            print( "Creating output directories..." );
            print( format( "  %s", outputDirectories.csvDirectory() ) );
            print( format( "  %s", outputDirectories.storeDirectory() ) );

            Formatting formatting = Formatting.DEFAULT;

            ConnectionConfig connectionConfig =  ConnectionConfig.forDatabase( DatabaseType.MySQL)
                    .host( host )
                    .port( port )
                    .database( database )
                    .username( user )
                    .password( password )
                    .build();

            print( "Exporting from MySQL to CSV..." );

            ExportToCsvResults exportResults = doExport( outputDirectories, formatting, connectionConfig );
            GraphConfig graphConfig = new SqlToGraphConfigMapper( exportResults ).createGraphConfig();

            print( "Creating Neo4j store from CSV..." );

            doImport( outputDirectories, formatting, graphConfig );

            print( "Done" );
            printResult( outputDirectories.storeDirectory() );
        }
        catch ( Exception e )
        {
            print( "Error while exporting from MySQL" );
            e.printStackTrace( System.err );
            System.exit( -1 );
        }
    }

    private void doImport( OutputDirectories outputDirectories,
                           Formatting formatting,
                           GraphConfig graphConfig ) throws Exception
    {
        ImportConfig importConfig = ImportConfig.builder()
                .importToolDirectory( Paths.get( importToolPath ) )
                .destination( outputDirectories.storeDirectory() )
                .formatting( formatting )
                .idType( IdType.Integer )
                .graphDataConfig( graphConfig )
                .build();

        new ImportFromCsvCommand( importConfig ).execute();
    }

    private ExportToCsvResults doExport( OutputDirectories outputDirectories,
                                         Formatting formatting,
                                         ConnectionConfig connectionConfig ) throws Exception
    {
        TableName person = new TableName( database, parentTable );
        TableName address = new TableName( database, childTable );

        try ( DatabaseClient databaseClient = new DatabaseClient( connectionConfig ) )
        {
            TableMetadataProducer tableMetadataProducer = new TableMetadataProducer( databaseClient );

            Collection<Table> tables1 = tableMetadataProducer.createMetadataFor( person );
            Collection<Table> tables2 = tableMetadataProducer.createMetadataFor( address );

            Collection<Join> joins =
                    new JoinMetadataProducer( databaseClient ).createMetadataFor( new TableNamePair( person, address ) );

            ExportToCsvConfig config = ExportToCsvConfig.builder()
                    .destination( outputDirectories.csvDirectory() )
                    .connectionConfig( connectionConfig )
                    .formatting( formatting )
                    .addTables( tables1 )
                    .addTables( tables2 )
                    .addJoins( joins )
                    .build();

            return new ExportToCsvCommand( config, new MySqlExportProvider() ).execute();
        }
    }

    private void print( Object message )
    {
        System.err.println( message );
    }

    private void printResult( Object message )
    {
        System.out.println( message );
    }

    private static class OutputDirectories
    {
        public static OutputDirectories create( Path rootDirectory ) throws IOException
        {
            Files.createDirectories( rootDirectory );

            int index = 1;

            Path outputDirectory = rootDirectory.resolve( format( "output-%03d", index++ ) );

            while ( Files.exists( outputDirectory ) )
            {
                outputDirectory = rootDirectory.resolve( format( "output-%03d", index++ ) );
            }

            Files.createDirectories( outputDirectory );

            Path csvDirectory = outputDirectory.resolve( "csv" );
            Path storeDirectory = outputDirectory.resolve( "graph.db" );

            Files.createDirectories( csvDirectory );
            Files.createDirectories( storeDirectory );

            return new OutputDirectories( csvDirectory, storeDirectory );
        }

        private final Path csvDirectory;
        private final Path storeDirectory;

        private OutputDirectories( Path csvDirectory, Path storeDirectory )
        {
            this.csvDirectory = csvDirectory;
            this.storeDirectory = storeDirectory;
        }

        public Path csvDirectory()
        {
            return csvDirectory;
        }

        public Path storeDirectory()
        {
            return storeDirectory;
        }
    }
}
