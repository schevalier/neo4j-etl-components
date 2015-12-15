package org.neo4j;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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

    public static void main( String[] args )
    {
        String exportPipeName = UUID.randomUUID().toString();
        String importPipeName = UUID.randomUUID().toString();

        try ( Pipe pipe = new Pipe( exportPipeName );
              SqlRunner sqlRunner = new SqlRunner( format( EXPORT_SQL, pipe.name() ) ) )
        {
            CompletableFuture<OutputStream> out = pipe.out(sqlRunner.execute());

            try ( Writer writer = new OutputStreamWriter( out.get() ) )
            {
                writer.write( "36\tsometext\n" );
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }

//        File importFile = new File( importPipeName );
//
//        try ( //Pipe pipe = new Pipe( importPipeName );
//              SqlRunner sqlRunner = new SqlRunner( format( IMPORT_SQL, importFile.getAbsolutePath() ) ) )
//        {
//            Commands.commands( "chmod", "0777", importFile.getAbsoluteFile().getParent() ).execute().await();
//
//            //CompletableFuture<InputStream> in = pipe.in();
//            sqlRunner.execute();
//
//            while ( !importFile.exists() )
//            {
//                Thread.sleep( 100 );
//            }
//
//            try ( BufferedReader reader =
//                          new BufferedReader( new InputStreamReader( new FileInputStream( importFile ) ) ) )
//            {
//                String line;
//
//                while ( (line = reader.readLine()) != null && !line.equals( "" ) )
//                {
//                    System.out.println( line );
//                }
//            }
//        }
//        catch ( Exception e )
//        {
//            e.printStackTrace();
//        }
    }


}
