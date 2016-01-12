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

import org.neo4j.command_line.Commands;
import org.neo4j.ingest.ImportCommand;
import org.neo4j.ingest.config.CsvField;
import org.neo4j.ingest.config.DataType;
import org.neo4j.ingest.config.Formatting;
import org.neo4j.ingest.config.GraphDataConfig;
import org.neo4j.ingest.config.GraphDataConfigSupplier;
import org.neo4j.ingest.config.IdType;
import org.neo4j.ingest.config.ImportConfig;
import org.neo4j.io.Pipe;
import org.neo4j.mysql.ExportCommand;
import org.neo4j.mysql.SqlRunner;
import org.neo4j.mysql.config.ConnectionConfig;
import org.neo4j.mysql.config.Join;
import org.neo4j.mysql.config.RelationalDatabaseExportConfig;
import org.neo4j.mysql.config.Table;
import org.neo4j.mysql.config.TableName;

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

        GraphDataConfig graphDataConfig = doExport( formatting, connectionConfig );

        doImport( formatting, graphDataConfig );
    }

    private static void doImport( Formatting formatting,
                                  GraphDataConfig graphDataConfig ) throws Exception
    {
        ImportConfig importConfig = ImportConfig.builder()
                .importToolDirectory( Paths.get( "/Users/iansrobinson/neo4j-enterprise-3.0.0-M02/bin" ) )
                .destination( Paths.get( "/Users/iansrobinson/Desktop/graph.db" ) )
                .formatting( formatting )
                .idType( IdType.Integer )
                .graphDataConfig( graphDataConfig )
                .build();

        new ImportCommand( importConfig ).execute();
    }

    private static GraphDataConfig doExport( Formatting formatting,
                                             ConnectionConfig connectionConfig ) throws Exception
    {
        TableName person = new TableName( "javabase.Person" );
        TableName address = new TableName( "javabase.Address" );

        RelationalDatabaseExportConfig config = RelationalDatabaseExportConfig.builder()
                .destination( Paths.get( "/Users/iansrobinson/Desktop" ) )
                .connectionConfig( connectionConfig )
                .formatting( formatting )
                .addTable( Table.builder()
                        .name( person )
                        .id( "id" )
                        .addColumn( "username", CsvField.data( "username", DataType.String ) )
                        .build() )
                .addTable( Table.builder()
                        .name( address )
                        .id( "id" )
                        .addColumn( "postcode", CsvField.data( "postcode", DataType.String ) )
                        .build() )
                .addJoin( Join.builder()
                        .parent( person, "id" )
                        .child( address, "id" )
                        .quoteCharacter( formatting.quoteCharacter() )
                        .build() )
                .build();

        return new ExportCommand( config ).execute();
    }

    private static void originalTest() throws IOException
    {
        String exportId = UUID.randomUUID().toString();
        String importId = UUID.randomUUID().toString();

        ConnectionConfig connectionConfig = new ConnectionConfig(
                URI.create( "jdbc:mysql://localhost:3306/javabase" ),
                "java",
                "password" );

        try ( Pipe pipe = new Pipe( exportId ) )
        {
            SqlRunner sqlRunner = new SqlRunner( connectionConfig, format( EXPORT_SQL, pipe.name() ) );
            CompletableFuture<OutputStream> out = pipe.out( sqlRunner.execute().toFuture() );

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

        try
        {
            SqlRunner sqlRunner = new SqlRunner( connectionConfig, format( IMPORT_SQL, importFile.getAbsolutePath() ) );
            Commands.commands( "chmod", "0777", importFile.getAbsoluteFile().getParent() ).execute().await();

            sqlRunner.execute().await();

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
