package org.neo4j.integration.commands;

import java.net.URI;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.logging.Level;

import io.airlift.airline.Command;
import io.airlift.airline.Option;
import io.airlift.airline.OptionType;

import org.neo4j.integration.SqlToGraphConfigMapper;
import org.neo4j.integration.neo4j.importcsv.ImportFromCsvCommand;
import org.neo4j.integration.neo4j.importcsv.config.Formatting;
import org.neo4j.integration.neo4j.importcsv.config.GraphConfig;
import org.neo4j.integration.neo4j.importcsv.config.ImportConfig;
import org.neo4j.integration.neo4j.importcsv.fields.IdType;
import org.neo4j.integration.sql.SqlRunner;
import org.neo4j.integration.sql.exportcsv.ExportToCsvCommand;
import org.neo4j.integration.sql.exportcsv.ExportToCsvResults;
import org.neo4j.integration.sql.exportcsv.config.ExportToCsvConfig;
import org.neo4j.integration.sql.exportcsv.mysql.MySqlExportProvider;
import org.neo4j.integration.sql.exportcsv.mysql.schema.JoinMetadataProducer;
import org.neo4j.integration.sql.exportcsv.mysql.schema.TableMetadataProducer;
import org.neo4j.integration.sql.metadata.ConnectionConfig;
import org.neo4j.integration.sql.metadata.Join;
import org.neo4j.integration.sql.metadata.Table;
import org.neo4j.integration.sql.metadata.TableName;
import org.neo4j.integration.sql.metadata.TableNamePair;
import org.neo4j.integration.util.Loggers;

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
            description = "Path to directory containing the Neo4j import tool.",
            title = "directory",
            required = true)
    private String importToolPath;

    @Option(type = OptionType.COMMAND,
            name = {"--csv-directory"},
            description = "Path to CSV export directory.",
            title = "directory",
            required = true)
    private String csvExportPath;

    @Option(type = OptionType.COMMAND,
            name = {"--destination"},
            description = "Path to destination Neo4j store.",
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
            Formatting formatting = Formatting.DEFAULT;

            ConnectionConfig connectionConfig = new ConnectionConfig(
                    URI.create(
                            format( "jdbc:mysql://%s:%s/%s?autoReconnect=true&useSSL=false", host, port, database ) ),
                    user,
                    password );

            ExportToCsvResults exportResults = doExport( formatting, connectionConfig );
            GraphConfig graphConfig = new SqlToGraphConfigMapper( exportResults ).createGraphConfig();
            doImport( formatting, graphConfig );
        }
        catch ( Exception e )
        {
            Loggers.Cli.log( Level.SEVERE, "Error while importing from MySQL", e );
            System.exit( -1 );
        }
    }

    private void doImport( Formatting formatting,
                           GraphConfig graphConfig ) throws Exception
    {
        ImportConfig importConfig = ImportConfig.builder()
                .importToolDirectory( Paths.get( importToolPath ) )
                .destination( Paths.get( destinationPath ) )
                .formatting( formatting )
                .idType( IdType.Integer )
                .graphDataConfig( graphConfig )
                .build();

        new ImportFromCsvCommand( importConfig ).execute();
    }

    private ExportToCsvResults doExport( Formatting formatting,
                                         ConnectionConfig connectionConfig ) throws Exception
    {
        TableName person = new TableName( database, parentTable );
        TableName address = new TableName( database, childTable );

        try ( SqlRunner sqlRunner = new SqlRunner( connectionConfig ) )
        {
            TableMetadataProducer tableMetadataProducer = new TableMetadataProducer( sqlRunner );

            Collection<Table> tables1 = tableMetadataProducer.createMetadataFor( person );
            Collection<Table> tables2 = tableMetadataProducer.createMetadataFor( address );

            Collection<Join> joins =
                    new JoinMetadataProducer( sqlRunner ).createMetadataFor( new TableNamePair( person, address ) );

            ExportToCsvConfig config = ExportToCsvConfig.builder()
                    .destination( Paths.get( csvExportPath ) )
                    .connectionConfig( connectionConfig )
                    .formatting( formatting )
                    .addTables( tables1 )
                    .addTables( tables2 )
                    .addJoins( joins )
                    .build();

            return new ExportToCsvCommand( config, new MySqlExportProvider() ).execute();
        }
    }
}
