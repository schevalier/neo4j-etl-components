package org.neo4j.integration.util;

import java.lang.reflect.Array;

public class StringListBuilder
{
    public static <T> StringBuilder stringList( Iterable<T> items, String separator )
    {
        return stringList( items, separator, Object::toString );
    }

    public static <T> StringBuilder stringList( T[] items, String separator )
    {
        return stringList( items, separator, Object::toString );
    }

    public static <T> StringBuilder stringList( Iterable<T> items, String separator, Stringifier<T> stringifier )
    {
        StringBuilder builder = new StringBuilder();

        if (items == null)
        {
            return builder;
        }

        boolean addSeparator = false;
        for ( T item : items )
        {
            if ( addSeparator )
            {
                builder.append( separator );
            }
            builder.append( stringifier.toString( item ) );
            addSeparator = true;
        }
        return builder;
    }

    public static <T> StringBuilder stringList( T[] items, String separator, Stringifier<T> stringifier )
    {
        StringBuilder builder = new StringBuilder();

        if (items == null)
        {
            return builder;
        }

        boolean addSeparator = false;
        for ( T item : items )
        {
            if ( addSeparator )
            {
                builder.append( separator );
            }
            builder.append( stringifier.toString( item ) );
            addSeparator = true;
        }
        return builder;
    }

    public static StringBuilder stringList( Object array, String arrayDelimiter )
    {
        int length = Array.getLength( array );
        Object[] objects = new Object[length];
        for ( int i = 0; i < length; i++ )
        {
            Object o = Array.get( array, i );
            objects[i] = o;

        }
        return stringList( objects, arrayDelimiter );
    }

    public interface Stringifier<T>
    {
        String toString( T item );
    }
}
