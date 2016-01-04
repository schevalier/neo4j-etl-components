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
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.neo4j.command_line.Commands;
import org.neo4j.io.Pipe;
import org.neo4j.mysql.SqlRunner;

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

    public static void main( String[] args ) throws IOException
    {
        String exportId = UUID.randomUUID().toString();
        String importId = UUID.randomUUID().toString();

        try ( Pipe pipe = new Pipe( exportId ) )
        {
            SqlRunner sqlRunner = new SqlRunner( format( EXPORT_SQL, pipe.name() ) );
            CompletableFuture<OutputStream> out = pipe.out( sqlRunner.execute() );

            try ( Writer writer = new OutputStreamWriter( out.get() ) )
            {
                writer.write( "39\tsometext\n" );
                Thread.sleep( 1000 );
                writer.write( "40\tsometext\n" );
                Thread.sleep( 1000 );
                writer.write( "41\tsometext\n" );
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }

        File importFile = new File( importId );

        try
        {
            SqlRunner sqlRunner = new SqlRunner( format( IMPORT_SQL, importFile.getAbsolutePath() ) );
            Commands.commands( "chmod", "0777", importFile.getAbsoluteFile().getParent() ).execute().await();

            sqlRunner.execute().get();

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
