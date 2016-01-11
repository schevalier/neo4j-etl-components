package org.neo4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.neo4j.command_line.Commands;
import org.neo4j.ingest.ImportCommand;
import org.neo4j.ingest.config.DataType;
import org.neo4j.ingest.config.Field;
import org.neo4j.ingest.config.Formatting;
import org.neo4j.ingest.config.IdType;
import org.neo4j.ingest.config.ImportConfig;
import org.neo4j.ingest.config.NodeConfig;
import org.neo4j.io.Pipe;
import org.neo4j.mysql.ExportTableCommand;
import org.neo4j.mysql.SqlRunner;
import org.neo4j.mysql.config.ExportConfig;
import org.neo4j.mysql.config.MySqlConnectionConfig;
import org.neo4j.mysql.config.Table;

import static java.lang.String.format;
import static java.util.Arrays.asList;

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

        MySqlConnectionConfig connectionConfig = new MySqlConnectionConfig(
                "jdbc:mysql://localhost:3306/javabase",
                "java",
                "password" );

        NodeConfig personNodes = exportPerson( formatting, connectionConfig );
        NodeConfig addressNodes = exportAddress( formatting, connectionConfig );


        doImport( formatting, asList( personNodes, addressNodes ) );
    }

    private static void doImport( Formatting formatting, Collection<NodeConfig> nodeConfigs ) throws Exception
    {
        ImportConfig.Builder builder = ImportConfig.builder()
                .importToolDirectory( Paths.get( "/Users/iansrobinson/neo4j-enterprise-3.0.0-M02/bin" ) )
                .destination( Paths.get( "/Users/iansrobinson/Desktop/graph.db" ) )
                .formatting( formatting )
                .idType( IdType.Integer );

        for ( NodeConfig nodeConfig : nodeConfigs )
        {
            builder.addNodeConfig( nodeConfig );
        }

        ImportCommand importCommand = new ImportCommand( builder.build() );
        importCommand.execute();
    }

//    private static NodeConfig exportRelationships( Formatting formatting, MySqlConnectionConfig connectionConfig )
//            throws Exception
//    {
//        ExportConfig config = ExportConfig.builder()
//                .destination( Paths.get( "/Users/iansrobinson/Desktop" ) )
//                .mySqlConnectionConfig( connectionConfig )
//                .formatting( formatting )
//                .table( Table.builder()
//                        .name( "javabase.Person" )
//                        .id( "id", "personId" )
//                        .addColumn( "username", Field.data( "username", DataType.String ) )
//                        .build() )
//                .build();
//
//        ExportTableCommand exportTableCommand = new ExportTableCommand( config, config.table() );
//        Collection<Path> files = exportTableCommand.execute();
//
//        return NodeConfig.builder().addInputFiles( files ).addLabel( config.table().name().simpleName() ).build();
//    }

    private static NodeConfig exportPerson( Formatting formatting, MySqlConnectionConfig connectionConfig ) throws
            Exception
    {
        ExportConfig config = ExportConfig.builder()
                .destination( Paths.get( "/Users/iansrobinson/Desktop" ) )
                .mySqlConnectionConfig( connectionConfig )
                .formatting( formatting )
                .table( Table.builder()
                        .name( "javabase.Person" )
                        .id( "id", "personId" )
                        .addColumn( "username", Field.data( "username", DataType.String ) )
                        .build() )
                .build();

        ExportTableCommand exportTableCommand = new ExportTableCommand( config, config.table() );
        Collection<Path> files = exportTableCommand.execute();

        return NodeConfig.builder().addInputFiles( files ).addLabel( config.table().name().simpleName() ).build();
    }

    private static NodeConfig exportAddress( Formatting formatting, MySqlConnectionConfig connectionConfig ) throws
            Exception
    {
        ExportConfig config = ExportConfig.builder()
                .destination( Paths.get( "/Users/iansrobinson/Desktop" ) )
                .mySqlConnectionConfig( connectionConfig )
                .formatting( formatting )
                .table( Table.builder()
                        .name( "javabase.Address" )
                        .id( "id", "addressId" )
                        .addColumn( "postcode", Field.data( "postcode", DataType.String ) )
                        .build() )
                .build();

        ExportTableCommand exportTableCommand = new ExportTableCommand( config, config.table() );
        Collection<Path> files = exportTableCommand.execute();

        return NodeConfig.builder().addInputFiles( files ).addLabel( config.table().name().simpleName() ).build();
    }

    private static void originalTest() throws IOException
    {
        String exportId = UUID.randomUUID().toString();
        String importId = UUID.randomUUID().toString();

        MySqlConnectionConfig connectionConfig = new MySqlConnectionConfig(
                "jdbc:mysql://localhost:3306/javabase",
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
