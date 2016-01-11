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
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.neo4j.command_line.Commands;
import org.neo4j.ingest.ImportCommand;
import org.neo4j.ingest.config.ConfigSupplier;
import org.neo4j.ingest.config.DataType;
import org.neo4j.ingest.config.Field;
import org.neo4j.ingest.config.Formatting;
import org.neo4j.ingest.config.IdType;
import org.neo4j.ingest.config.ImportConfig;
import org.neo4j.ingest.config.NodeConfig;
import org.neo4j.ingest.config.RelationshipConfig;
import org.neo4j.io.Pipe;
import org.neo4j.mysql.ExportJoinCommand;
import org.neo4j.mysql.ExportTableCommand;
import org.neo4j.mysql.SqlRunner;
import org.neo4j.mysql.config.ExportConfig;
import org.neo4j.mysql.config.Join;
import org.neo4j.mysql.config.MySqlConnectionConfig;
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

        MySqlConnectionConfig connectionConfig = new MySqlConnectionConfig(
                "jdbc:mysql://localhost:3306/javabase",
                "java",
                "password" );

        Collection<ConfigSupplier> configSuppliers = doExport( formatting, connectionConfig );

        doImport( formatting, configSuppliers );
    }

    private static void doImport( Formatting formatting, Collection<ConfigSupplier> configSuppliers ) throws Exception
    {
        ImportConfig.Builder builder = ImportConfig.builder()
                .importToolDirectory( Paths.get( "/Users/iansrobinson/neo4j-enterprise-3.0.0-M02/bin" ) )
                .destination( Paths.get( "/Users/iansrobinson/Desktop/graph.db" ) )
                .formatting( formatting )
                .idType( IdType.Integer );

        for ( ConfigSupplier configSupplier : configSuppliers )
        {
            configSupplier.addConfigTo( builder );
        }

        ImportCommand importCommand = new ImportCommand( builder.build() );
        importCommand.execute();
    }

    private static Collection<ConfigSupplier> doExport( Formatting formatting, MySqlConnectionConfig connectionConfig )
            throws Exception
    {
        TableName personTable = new TableName( "javabase.Person" );
        TableName addressTable = new TableName( "javabase.Address" );

        ExportConfig config = ExportConfig.builder()
                .destination( Paths.get( "/Users/iansrobinson/Desktop" ) )
                .mySqlConnectionConfig( connectionConfig )
                .formatting( formatting )
                .addTable( Table.builder()
                        .name( personTable )
                        .id( "id" )
                        .addColumn( "username", Field.data( "username", DataType.String ) )
                        .build() )
                .addTable( Table.builder()
                        .name( addressTable )
                        .id( "id" )
                        .addColumn( "postcode", Field.data( "postcode", DataType.String ) )
                        .build() )
                .addJoin( Join.builder()
                        .parent( personTable, "id" )
                        .child( addressTable, "id" )
                        .quote( formatting.quote() )
                        .build() )
                .build();

        Collection<ConfigSupplier> configSuppliers = new ArrayList<>();

        for ( Table table : config.tables() )
        {
            ExportTableCommand exportTableCommand = new ExportTableCommand( config, table );
            Collection<Path> files = exportTableCommand.execute();

            configSuppliers.add( NodeConfig.builder()
                    .addInputFiles( files )
                    .addLabel( table.name().simpleName() )
                    .build() );
        }

        for ( Join join : config.joins() )
        {
            ExportJoinCommand exportJoinCommand = new ExportJoinCommand( config, join );
            Collection<Path> files = exportJoinCommand.execute();

            configSuppliers.add( RelationshipConfig.builder()
                    .addInputFiles( files )
                    .build() );
        }

        return configSuppliers;
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
