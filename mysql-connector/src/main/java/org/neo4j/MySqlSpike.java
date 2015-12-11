package org.neo4j;

import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.UUID;

import org.neo4j.mysql.NamedPipe;
import org.neo4j.mysql.PipeReader;
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
    }
}
