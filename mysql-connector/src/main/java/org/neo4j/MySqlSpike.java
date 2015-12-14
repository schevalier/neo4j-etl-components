package org.neo4j;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.neo4j.mysql.NamedPipe;
import org.neo4j.mysql.PipeReader;
import org.neo4j.mysql.SqlRunner;
import org.neo4j.pipes.Pipe;

import static java.lang.String.format;

public class MySqlSpike
{
    private static final String SQL = "LOAD DATA LOCAL INFILE '%s' INTO TABLE javabase.test FIELDS TERMINATED " +
            "BY " +
            "'\\t' ENCLOSED BY '' ESCAPED BY '\\\\' LINES TERMINATED BY '\\n' STARTING BY ''";

    public static void main( String[] args )
    {
        String pipeName = UUID.randomUUID().toString();

        try ( PipeReader reader = new SqlRunner( format( SQL, pipeName ) ) )
        {

            try ( NamedPipe pipe = new NamedPipe( pipeName, reader );
                  Writer writer = new OutputStreamWriter( pipe.open() ) )
            {
                writer.write( "21\thello alan\n" );
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }

//        Pipe pipe = new Pipe( pipeName );
//        try
//        {
//            CompletableFuture<InputStream> in = pipe.in();
//            CompletableFuture<OutputStream> out = pipe.out();
//
//            try ( Writer writer = new OutputStreamWriter( out.get() );
//                  BufferedReader reader = new BufferedReader( new InputStreamReader( in.get() ) ) )
//            {
//                writer.write( "sometext\n" );
//                writer.flush();
//                System.out.println( reader.readLine() );
//            }
//
//
//        }
//        catch ( Exception e )
//        {
//            e.printStackTrace();
//        }
    }
}
