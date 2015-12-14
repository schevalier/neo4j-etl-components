package org.neo4j;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.neo4j.mysql.Pipe;
import org.neo4j.mysql.SqlRunner;

import static java.lang.String.format;

public class MySqlSpike
{
    private static final String SQL = "LOAD DATA LOCAL INFILE '%s' INTO TABLE javabase.test FIELDS TERMINATED " +
            "BY " +
            "'\\t' ENCLOSED BY '' ESCAPED BY '\\\\' LINES TERMINATED BY '\\n' STARTING BY ''";

    public static void main( String[] args )
    {
        String pipeName = UUID.randomUUID().toString();

//        try ( PipeReader reader = new SqlRunner( format( SQL, pipeName ) ) )
//        {
//
//            try ( NamedPipe pipe = new NamedPipe( pipeName, reader );
//                  Writer writer = new OutputStreamWriter( pipe.open() ) )
//            {
//                writer.write( "21\thello alan\n" );
//            }
//        }
//        catch ( Exception e )
//        {
//            e.printStackTrace();
//        }


        try ( Pipe pipe = new Pipe( pipeName );
              SqlRunner sqlRunner = new SqlRunner( format( SQL, pipeName ) ) )
        {
            sqlRunner.open();
            CompletableFuture<OutputStream> out = pipe.out();

            try ( Writer writer = new OutputStreamWriter( out.get() ) )
            {
                writer.write( "30\tsometext\n" );
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }


}
