package org.neo4j.etl.neo4j.importcsv.config.formatting;


import edu.washington.cs.knowitall.morpha.MorphaStemmer;
import org.apache.commons.lang3.StringUtils;

public class DefaultLabelFormatter implements Formatter
{
    @Override
    public String format( String value )
    {
        return stemWords( toUpperCamelCase( value ) );
    }

    private String stemWords( String value )
    {
        String[] words = StringUtils.splitByCharacterTypeCamelCase( value );

        StringBuilder builder = new StringBuilder();

        for ( String word : words )
        {
            builder.append( StringUtils.capitalize( MorphaStemmer.stem( word.toLowerCase() ) ) );
        }

        return builder.toString();
    }

    private String toUpperCamelCase( String value )
    {
        StringBuilder results = new StringBuilder();

        int index = 0;

        while ( index < value.length() )
        {
            char c = value.charAt( index );

            if ( c == '_' )
            {
                if ( index < value.length() - 1 )
                {
                    results.append( String.valueOf( value.charAt( index + 1 ) ).toUpperCase() );
                }
                index = index + 2;
            }
            else if ( index == 0 )
            {
                results.append( String.valueOf( c ).toUpperCase() );
                index = index + 1;
            }
            else
            {
                if ( index > 0 )
                {
                    String prev = String.valueOf( value.charAt( index - 1 ) );
                    if ( prev.equals( prev.toUpperCase() ) )
                    {
                        results.append( String.valueOf( c ).toLowerCase() );
                    }
                    else
                    {
                        results.append( c );
                    }
                }
                else
                {
                    results.append( c );
                }
                index = index + 1;
            }
        }

        return results.toString();
    }
}
