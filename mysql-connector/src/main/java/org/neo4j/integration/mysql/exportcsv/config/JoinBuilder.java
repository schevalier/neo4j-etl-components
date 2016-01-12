package org.neo4j.integration.mysql.exportcsv.config;

import org.neo4j.integration.neo4j.importcsv.config.CsvField;
import org.neo4j.integration.neo4j.importcsv.config.IdSpace;
import org.neo4j.integration.neo4j.importcsv.config.QuoteChar;

class JoinBuilder implements Join.Builder, Join.Builder.SetParent, Join.Builder.SetChild, Join.Builder.SetQuote
{
    Column parent;
    Column child;
    QuoteChar quote;

    @Override
    public SetChild parent( TableName table, String foreignKey )
    {
        parent = Column.builder()
                .table( table )
                .name( foreignKey )
                .mapsTo( CsvField.startId( new IdSpace( table.simpleName() ) ) )
                .build();
        return this;
    }

    @Override
    public SetQuote child( TableName table, String primaryKey )
    {
        child = Column.builder()
                .table( table )
                .name( primaryKey )
                .mapsTo( CsvField.endId( new IdSpace( table.simpleName() ) ) )
                .build();
        return this;
    }

    @Override
    public Join.Builder quoteCharacter( QuoteChar quote )
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
