package org.neo4j.integration.util;

import java.util.logging.Level;
import java.util.logging.Logger;

public enum Loggers
{
    Default,
    Cli,
    Sql;

    private final Logger log;

    Loggers( )
    {
        this.log = Logger.getLogger( name() );
    }

    public Logger log()
    {
        return log;
    }

    public void log( Level level, String msg )
    {
        log.log( level, msg );
    }

    public void log( Level level, String msg, Object param )
    {
        log.log( level, msg, param );
    }

    public void log( Level level, String msg, Object[] params )
    {
        log.log( level, msg, params );
    }

    public void log( Level level, String msg, Throwable thrown )
    {
        log.log( level, msg, thrown );
    }
}
