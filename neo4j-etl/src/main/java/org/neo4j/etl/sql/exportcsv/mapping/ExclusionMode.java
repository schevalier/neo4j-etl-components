package org.neo4j.etl.sql.exportcsv.mapping;

public enum ExclusionMode
{
    EXCLUDE, INCLUDE, NONE;

    ExclusionMode()
    {

    }

    public static ExclusionMode parse( String exclusionMode )
    {
        if ( "exclude".equalsIgnoreCase( exclusionMode ) )
        {
            return ExclusionMode.EXCLUDE;
        }
        else if ( "include".equalsIgnoreCase( exclusionMode ) )
        {
            return ExclusionMode.INCLUDE;
        }

        return ExclusionMode.NONE;
    }
}
