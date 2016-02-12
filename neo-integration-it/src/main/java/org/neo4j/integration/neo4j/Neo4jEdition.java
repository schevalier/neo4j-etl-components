package org.neo4j.integration.neo4j;

import org.neo4j.integration.util.Partial;
import org.neo4j.integration.util.StringListBuilder;

public enum Neo4jEdition
{
    Community( "neo4j" ),
    Advanced( "neo4j-advance" ),
    Enterprise( "neo4j-enterprise" );

    private final String debPackageName;

    Neo4jEdition( String debPackageName )
    {
        this.debPackageName = debPackageName;
    }

    public String format()
    {
        return name().toLowerCase();
    }

    public static Partial<Neo4jEdition> parse( String input, String separator )
    {
        String[] bits = input.split( separator, 2 );
        if ( bits.length == 2 )
        {
            for ( Neo4jEdition e : values() )
            {
                if ( e.name().toLowerCase().equals( bits[0].toLowerCase() ) )
                {
                    return new Partial<>( e, bits[1] );
                }
            }
        }
        throw new IllegalArgumentException(
                String.format( "'%s' is not a valid package. Expected an edition in [%s] followed by a [%s].",
                        input, StringListBuilder.stringList( values(), ", " ), separator ) );
    }

    public String debPackageName()
    {
        return debPackageName;
    }
}

