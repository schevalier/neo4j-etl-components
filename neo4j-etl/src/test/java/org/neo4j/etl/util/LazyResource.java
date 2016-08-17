package org.neo4j.etl.util;

import java.util.Optional;

public class LazyResource<T> implements Resource<T>
{
    private final Lifecycle<T> lifecycle;
    private Optional<T> resource = Optional.empty();

    public interface Lifecycle<T>
    {
        T create() throws Exception;

        void destroy( T t ) throws Exception;
    }

    public LazyResource( Lifecycle<T> lifecycle )
    {
        this.lifecycle = lifecycle;
    }

    @Override
    public T get()
    {
        if ( !resource.isPresent() )
        {
            resource = Optional.ofNullable( create() );
        }
        return resource.get();
    }

    @Override
    public void close() throws Exception
    {
        if ( resource.isPresent() )
        {
            destroy( resource.get() );
        }
    }

    private T create()
    {
        try
        {
            return lifecycle.create();
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    private void destroy( T t )
    {
        try
        {
            lifecycle.destroy( t );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }
}

