package org.neo4j.mysql.config;

import org.neo4j.ingest.config.Field;
import org.neo4j.ingest.config.IdSpace;
import org.neo4j.ingest.config.QuoteChar;

class JoinBuilder implements Join.Builder, Join.Builder.SetParent, Join.Builder.SetChild, Join.Builder.SetQuote
{
    Column parent;
    Column child;
    QuoteChar quote;

    @Override
    public SetChild parent( TableName table, String column )
    {
        parent = Column.builder()
                .table( table )
                .name( column )
                .mapsTo( Field.startId( new IdSpace( table.simpleName() ) ) )
                .build();
        return this;
    }

    @Override
    public SetQuote child( TableName table, String column )
    {
        child = Column.builder()
                .table( table )
                .name( column )
                .mapsTo( Field.endId( new IdSpace( table.simpleName() ) ) )
                .build();
        return this;
    }

    @Override
    public Join.Builder quote( QuoteChar quote )
    {
        this.quote = quote;
        return this;
    }

    @Override
    public Join build()
    {
        return new Join( this );
    }
}
