package org.neo4j.mysql;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;

import org.neo4j.io.FileBasedStreamRecorder;
import org.neo4j.io.StreamContents;

public class MySqlSpike
{
    private static final String SQL = "LOAD DATA LOCAL INFILE 'data.txt' INTO TABLE javabase.test FIELDS TERMINATED " +
            "BY " +
            "'\\t' ENCLOSED BY '' ESCAPED BY '\\\\' LINES TERMINATED BY '\\n' STARTING BY ''";

    public static void main( String[] args )
    {
        try
        {
//            new FileBasedStreamRecorder(  ).
//            new InMemoryStreamRecorder(  ).

            File file = new File( "/Users/iansrobinson/Desktop/out.txt" );

            FileBasedStreamRecorder recorder = new FileBasedStreamRecorder( file );

            PipedOutputStream output = new PipedOutputStream();
            InputStream input = new PipedInputStream( output );

            StreamContents contents = recorder.start( input );

            OutputStreamWriter writer = new OutputStreamWriter( output );
            writer.write( "name: ian robinson\n" );
            writer.write( "age: 21\n" );
            writer.write( "email: iansrobinson@gmail.com\n" );
            writer.flush();
            writer.close();

            Thread.sleep( 100 );

            System.out.println( contents.value() );

//            Commands commands = Commands.forCommands()
//                    .inheritWorkingDirectory()
//                    .failOnNonZeroExitValue()
//                    .noTimeout()
//                    .inheritEnvironment()
//                    .noRedirection()
//                    .build();
//
//            Result result = commands.execute();
//
//            SqlRunner sqlRunner = new SqlRunner( SQL );
//            sqlRunner.start();
//
//            Thread.sleep( 1000 );
//
//            sqlRunner.terminate();
//            sqlRunner.rethrowExceptions();

        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }

    static class SqlRunner extends Thread
    {
        private final String sql;
        private volatile boolean allowContinue = true;

        private volatile Exception ex;

        SqlRunner( String sql )
        {
            this.sql = sql;
        }

        public void run()
        {

            String url = "jdbc:mysql://localhost:3306/javabase";
            String username = "java";
            String password = "password";

            System.out.println( "Connecting to database..." );

            try ( Connection connection = DriverManager.getConnection( url, username, password ) )
            {
                System.out.println( "Connected to database" );
                connection.createStatement().execute( sql );

                while ( allowContinue )
                {
                    Thread.sleep( 100 );
                }
            }
            catch ( Exception e )
            {
                ex = e;
            }
        }

        public void terminate()
        {
            allowContinue = false;
        }

        void rethrowExceptions() throws Exception
        {
            if ( ex != null )
            {
                throw ex;
            }
        }
    }
}
