package org.neo4j.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

public enum Loggers
{
    Default
            {
                private final Logger logger = Logger.getLogger( "Default" );

                @Override
                public Logger getLogger()
                {
                    return logger;
                }

                @Override
                public void log( Level level, String msg )
                {
                    logger.log( level, msg );
                }

                @Override
                public void log( Level level, String msg, Object param1 )
                {
                    logger.log( level, msg, param1 );
                }

                @Override
                public void log( Level level, String msg, Object[] params )
                {
                    logger.log( level, msg, params );
                }

                @Override
                public void log( Level level, String msg, Throwable thrown )
                {
                    logger.log( level, msg, thrown );
                }
            };

    public abstract Logger getLogger();

    public abstract void log( Level level, String msg );

    public abstract void log( Level level, String msg, Object param1 );

    public abstract void log( Level level, String msg, Object[] params );

    public abstract void log( Level level, String msg, Throwable thrown );
}
