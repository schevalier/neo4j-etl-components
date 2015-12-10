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
        String pipe = UUID.randomUUID().toString();

        try ( PipeReader reader = new SqlRunner( format( SQL, pipe ) ) )
        {
            try ( Writer writer = new NamedPipe( pipe, reader ).open() )
            {
                writer.write( "14\thello world\n" );
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
