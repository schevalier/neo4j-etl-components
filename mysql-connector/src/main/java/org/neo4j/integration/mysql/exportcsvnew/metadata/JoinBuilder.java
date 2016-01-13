package org.neo4j.integration.mysql.exportcsvnew.metadata;

class JoinBuilder implements Join.Builder.SetParent, Join.Builder.SetChild, Join.Builder
{
    Column parent;
    Column child;

    @Override
    public SetChild parent( Column parent )
    {
        this.parent = parent;
        return this;
    }

    @Override
    public Join.Builder child( Column child )
    {
        this.child = child;
        return this;
    }

    @Override
    public Join build()
    {
        return new Join( this );
    }
}
