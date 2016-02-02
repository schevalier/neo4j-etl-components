package org.neo4j.integration.util;

import java.util.ArrayList;
import java.util.Collection;

public class CollectionUtils
{
    public static <T> T last(Collection<? extends T> collection)
    {
        ArrayList<T> list = new ArrayList<>( collection );
        return list.isEmpty() ? null : list.get( list.size() - 1 );
    }

    public static <T> T first(Collection<? extends T> collection)
    {
        ArrayList<T> list = new ArrayList<>( collection );
        return list.isEmpty() ? null : list.get( 0 );
    }
}
