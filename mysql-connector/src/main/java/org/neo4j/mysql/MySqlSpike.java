package org.neo4j.mysql;

import java.io.Writer;
import java.util.UUID;

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
                  Writer writer = pipe.open() )
            {
                writer.write( "17\thello alan\n" );
                writer.flush();

                Thread.sleep( 1000 );
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }
}
