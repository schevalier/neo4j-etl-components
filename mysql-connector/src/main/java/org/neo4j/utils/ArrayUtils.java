package org.neo4j.utils;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static java.util.Arrays.asList;

public class ArrayUtils
{
    public static <T> T[] prepend( T element, T[] existing )
    {
        ArrayList<T> results = new ArrayList<>( asList( existing ) );
        results.add( 0, element );

        @SuppressWarnings("unchecked")
        T[] a = (T[]) Array.newInstance( element.getClass(), results.size() );

        return results.toArray( a );
    }

    public static <T> T[] append( T element, T[] existing )
    {
        ArrayList<T> results = new ArrayList<>( asList( existing ) );
        results.add( element );

        @SuppressWarnings("unchecked")
        T[] a = (T[]) Array.newInstance( element.getClass(), results.size() );

        return results.toArray( a );
    }

}
