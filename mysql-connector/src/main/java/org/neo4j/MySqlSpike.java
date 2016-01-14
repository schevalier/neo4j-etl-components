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
import java.sql.ResultSet;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.neo4j.integration.cli.Commands;
import org.neo4j.integration.io.Pipe;
import org.neo4j.integration.mysql.SqlRunner;
import org.neo4j.integration.mysql.metadata.ConnectionConfig;
import org.neo4j.integration.mysql.metadata.TableName;
import org.neo4j.integration.mysql.exportcsv.ExportToCsv;
import org.neo4j.integration.mysql.exportcsv.config.ExportToCsvConfig;
import org.neo4j.integration.mysql.metadata.ColumnType;
import org.neo4j.integration.mysql.metadata.Join;
import org.neo4j.integration.mysql.metadata.Table;
import org.neo4j.integration.neo4j.importcsv.ImportCommand;
import org.neo4j.integration.neo4j.importcsv.config.Formatting;
import org.neo4j.integration.neo4j.importcsv.config.GraphDataConfig;
import org.neo4j.integration.neo4j.importcsv.config.IdType;
import org.neo4j.integration.neo4j.importcsv.config.ImportConfig;

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

        ExportToCsvConfig config = ExportToCsvConfig.builder()
                .destination( Paths.get( "/Users/iansrobinson/Desktop" ) )
                .connectionConfig( connectionConfig )
                .formatting( formatting )
                .addTable( Table.builder()
                        .name( person )
                        .addColumn( "id", ColumnType.PrimaryKey )
                        .addColumn( "username", ColumnType.Data )
                        .addColumn( "addressId", ColumnType.ForeignKey )
                        .build() )
                .addTable( Table.builder()
                        .name( address )
                        .addColumn( "id", ColumnType.PrimaryKey )
                        .addColumn( "postcode", ColumnType.Data )
                        .build() )
                .addJoin( Join.builder()
                        .parentTable( person )
                        .primaryKey( "id" )
                        .foreignKey( "addressId" )
                        .childTable( address )
                        .build() )
                .build();

        return new ExportToCsv( config ).execute();
    }

    private static void printDbInfo( ConnectionConfig connectionConfig ) throws Exception
    {
        try ( SqlRunner sqlRunner = new SqlRunner( connectionConfig ) )
        {
            ResultSet resultSet = sqlRunner.execute( "SELECT COLUMN_NAME, DATA_TYPE, COLUMN_KEY FROM " +
                    "INFORMATION_SCHEMA" +
                    ".COLUMNS WHERE TABLE_SCHEMA = 'javabase' AND TABLE_NAME ='Person';" ).await();

            while ( resultSet.next() )
            {

                System.out.println( "Column: " + resultSet.getString( "COLUMN_NAME" ) );
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
