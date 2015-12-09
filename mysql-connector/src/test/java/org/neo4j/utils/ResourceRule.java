package org.neo4j.utils;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class ResourceRule<T> implements TestRule
{
    private final Resource<T> resource;

    public ResourceRule( Resource<T> resource )
    {
        this.resource = resource;
    }

    public T get()
    {
        return resource.get();
    }

    @Override
    public Statement apply( final Statement base, Description description )
    {
        return new Statement()
        {
            @Override
            public void evaluate() throws Throwable
            {
                try
                {
                    resource.get();
                    base.evaluate();
                }
                finally
                {
                    resource.close();
                }
            }
        };
    }
}
