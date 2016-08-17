package org.neo4j.etl.neo4j.importcsv.fields;

import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import org.neo4j.etl.neo4j.importcsv.config.formatting.Formatter;

import static java.lang.String.format;

class EndId implements CsvField
{
    static CsvField fromJson( JsonNode node )
    {
        String idSpace = node.path( "id-space" ).textValue();
        if ( StringUtils.isNotEmpty( idSpace ) )
        {
            return new EndId( new IdSpace( idSpace ) );
        }
        else
        {
            return new EndId();
        }
    }

    private final Optional<IdSpace> idSpace;

    EndId()
    {
        this( null );
    }

    EndId( IdSpace idSpace )
    {
        this.idSpace = Optional.ofNullable( idSpace );
    }

    @Override
    public String value( Formatter formatter )
    {
        return idSpace.isPresent() ? format( ":END_ID(%s)", idSpace.get().value() ) : ":END_ID";
    }

    @Override
    public JsonNode toJson()
    {
        ObjectNode root = JsonNodeFactory.instance.objectNode();

        root.put( "type", getClass().getSimpleName() );
        root.put( "id-space", idSpace.isPresent() ? idSpace.get().value() : "" );

        return root;
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals( Object o )
    {
        return EqualsBuilder.reflectionEquals( this, o );
    }

    @Override
    public int hashCode()
    {
        return HashCodeBuilder.reflectionHashCode( this );
    }
}
