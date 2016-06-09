package org.neo4j.integration.neo4j.importcsv.fields;

import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import org.neo4j.integration.neo4j.importcsv.config.formatting.Formatter;

import static java.lang.String.format;

class StartId implements CsvField
{
    static CsvField fromJson( JsonNode node )
    {
        String idSpace = node.path( "id-space" ).textValue();
        if ( StringUtils.isNotEmpty( idSpace ) )
        {
            return new StartId( new IdSpace( idSpace ) );
        }
        else
        {
            return new StartId();
        }
    }

    private final Optional<IdSpace> idSpace;

    StartId()
    {
        this( null );
    }

    StartId( IdSpace idSpace )
    {
        this.idSpace = Optional.ofNullable( idSpace );
    }

    @Override
    public String value( Formatter formatter )
    {
        return idSpace.isPresent() ? format( ":START_ID(%s)", idSpace.get().value() ) : ":START_ID";
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
