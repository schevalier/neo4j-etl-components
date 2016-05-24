package org.neo4j.integration.neo4j.importcsv.config;

import java.util.stream.Collectors;

import edu.washington.cs.knowitall.morpha.MorphaStemmer;
import org.apache.commons.lang3.StringUtils;

import static java.util.Arrays.asList;

public class DefaultRelationshipFormatter implements Formatter
{
    @Override
    public String format( String value )
    {
        return asList( StringUtils.splitByCharacterTypeCamelCase( value ) ).stream()
                .flatMap( word -> asList( word.split( "_" ) ).stream() )
                .flatMap( word -> asList( word.split( "\0" ) ).stream() )
                .map( s -> MorphaStemmer.stem( s.toLowerCase() ) )
                .map( String::toUpperCase )
                .collect( Collectors.joining( "_" ) );
    }
}
