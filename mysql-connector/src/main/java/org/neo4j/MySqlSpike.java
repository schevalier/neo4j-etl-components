package org.neo4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.neo4j.integration.io.Pipe;
import org.neo4j.integration.neo4j.importcsv.ImportCommand;
import org.neo4j.integration.neo4j.importcsv.config.Formatting;
import org.neo4j.integration.neo4j.importcsv.config.GraphConfig;
import org.neo4j.integration.neo4j.importcsv.config.ImportConfig;
import org.neo4j.integration.neo4j.importcsv.fields.IdType;
import org.neo4j.integration.process.Commands;
import org.neo4j.integration.sql.SqlRunner;
import org.neo4j.integration.sql.exportcsv.ExportResultsToImportConfigMapper;
import org.neo4j.integration.sql.exportcsv.ExportToCsv;
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

import static java.lang.String.format;

public class MySqlSpike
{
    private static final String EXPORT_SQL = "LOAD DATA INFILE '%s' INTO TABLE javabase.test FIELDS TERMINATED " +
            "BY " +
            "'\\t' OPTIONALLY ENCLOSED BY '' ESCAPED BY '\\\\' LINES TERMINATED BY '\\n' STARTING BY ''";

    private static final String IMPORT_SQL = "SELECT id, data INTO OUTFILE '%s' FIELDS TERMINATED " +
            "BY " +
            "'\\t' OPTIONALLY ENCLOSED BY '' ESCAPED BY '\\\\' LINES TERMINATED BY '\\n' STARTING BY '' FROM javabase" +
            ".test";

    public static void main( String[] args ) throws Exception
    {
        Formatting formatting = Formatting.DEFAULT;

        ConnectionConfig connectionConfig = new ConnectionConfig(
                URI.create( "jdbc:mysql://localhost:3306/javabase" ),
                "java",
                "password" );

        printDbInfo( connectionConfig );

        GraphConfig graphConfig = doExport( formatting, connectionConfig );

        doImport( formatting, graphConfig );
    }

    private static void doImport( Formatting formatting,
                                  GraphConfig graphConfig ) throws Exception
    {
        ImportConfig importConfig = ImportConfig.builder()
                .importToolDirectory( Paths.get( "/Users/iansrobinson/neo4j-enterprise-3.0.0-M02/bin" ) )
                .destination( Paths.get( "/Users/iansrobinson/Desktop/graph.db" ) )
                .formatting( formatting )
                .idType( IdType.Integer )
                .graphDataConfig( graphConfig )
                .build();

        new ImportCommand( importConfig ).execute();
    }

    private static GraphConfig doExport( Formatting formatting,
                                             ConnectionConfig connectionConfig ) throws Exception
    {
        TableName person = new TableName( "javabase.Person" );
        TableName address = new TableName( "javabase.Address" );

        try ( SqlRunner sqlRunner = new SqlRunner( connectionConfig ) )
        {
            TableMetadataProducer tableMetadataProducer = new TableMetadataProducer( sqlRunner );

            Collection<Table> tables1 = tableMetadataProducer.createMetadataFor( person );
            Collection<Table> tables2 = tableMetadataProducer.createMetadataFor( address );

            Collection<Join> joins =
                    new JoinMetadataProducer( sqlRunner ).createMetadataFor( new TableNamePair( person, address ) );

            ExportToCsvConfig config = ExportToCsvConfig.builder()
                    .destination( Paths.get( "/Users/iansrobinson/Desktop" ) )
                    .connectionConfig( connectionConfig )
                    .formatting( formatting )
                    .addTables( tables1 )
                    .addTables( tables2 )
                    .addJoins( joins )
                    .build();

            ExportToCsvResults exportResults = new ExportToCsv( config, new MySqlExportProvider() ).execute();

            return new ExportResultsToImportConfigMapper( exportResults ).createImportConfig();
        }
    }

    private static void printDbInfo( ConnectionConfig connectionConfig ) throws Exception
    {
        try ( SqlRunner sqlRunner = new SqlRunner( connectionConfig ) )
        {
            Collection<Table> tables = new TableMetadataProducer( sqlRunner ).createMetadataFor( new TableName( "javabase.Person" ) );
            System.out.println( tables );

            Collection<Join> joins = new JoinMetadataProducer( sqlRunner )
                    .createMetadataFor( new TableNamePair(
                            new TableName( "javabase.Person" ),
                            new TableName( "javabase.Address" ) ) );

            for ( Join join : joins )
            {
                System.out.println( join );
            }
        }
    }

    private static void originalTest() throws IOException
    {
        String exportId = UUID.randomUUID().toString();
        String importId = UUID.randomUUID().toString();

        ConnectionConfig connectionConfig = new ConnectionConfig(
                URI.create( "jdbc:mysql://localhost:3306/javabase" ),
                "java",
                "password" );

        try ( Pipe pipe = new Pipe( exportId );
              SqlRunner sqlRunner = new SqlRunner( connectionConfig ) )
        {
            CompletableFuture<OutputStream> out =
                    pipe.out( sqlRunner.execute( format( EXPORT_SQL, pipe.name() ) ).toFuture() );

            try ( Writer writer = new OutputStreamWriter( out.get() ) )
            {
                writer.write( "50\tsometext\n" );
                Thread.sleep( 1000 );
                writer.write( "51\tsometext\n" );
                Thread.sleep( 1000 );
                writer.write( "52\tsometext\n" );
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }

        File importFile = new File( importId );

        try ( SqlRunner sqlRunner = new SqlRunner( connectionConfig ) )
        {
            Commands.commands( "chmod", "0777", importFile.getAbsoluteFile().getParent() ).execute().await();

            sqlRunner.execute( format( IMPORT_SQL, importFile.getAbsolutePath() ) ).await();

            try ( BufferedReader reader =
                          new BufferedReader( new InputStreamReader( new FileInputStream( importFile ) ) ) )
            {
                String line;

                while ( (line = reader.readLine()) != null && !line.equals( "" ) )
                {
                    System.out.println( line );
                }
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
        finally
        {
            Files.deleteIfExists( importFile.toPath() );
        }
    }
}
