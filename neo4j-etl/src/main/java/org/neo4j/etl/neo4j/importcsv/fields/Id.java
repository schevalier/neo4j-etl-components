package org.neo4j.etl.neo4j.importcsv.fields;

import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import org.neo4j.etl.neo4j.importcsv.config.formatting.Formatter;
import org.neo4j.etl.util.Strings;

import static java.lang.String.format;

class Id implements CsvField
{
    static CsvField fromJson( JsonNode node )
    {
        String name = node.path( "name" ).textValue();
        String idSpace = node.path( "id-space" ).textValue();
        if ( StringUtils.isNotEmpty( name ) && StringUtils.isNotEmpty( idSpace ) )
        {
            return new Id( name, new IdSpace( idSpace ) );
        }
        else if ( StringUtils.isNotEmpty( name ) )
        {
            return new Id( name );
        }
        else if ( StringUtils.isNotEmpty( idSpace ) )
        {
            return new Id( new IdSpace( idSpace ) );
        }
        else
        {
            return new Id();
        }
    }

    private final Optional<String> name;
    private final Optional<IdSpace> idSpace;

    Id()
    {
        this( null, null );
    }

    Id( String name )
    {
        this( name, null );
    }

    Id( IdSpace idSpace )
    {
        this( null, idSpace );
    }

    Id( String name, IdSpace idSpace )
    {
        this.name = Optional.ofNullable( Strings.orNull( name ) );
        this.idSpace = Optional.ofNullable( idSpace );
    }

    @Override
    public String value( Formatter formatter )
    {
        if ( name.isPresent() && idSpace.isPresent() )
        {
            return format( "%s:ID(%s)", name.get(), idSpace.get().value() );
        }
        else if ( name.isPresent() )
        {
            return format( "%s:ID", name.get() );
        }
        else if ( idSpace.isPresent() )
        {
            return format( ":ID(%s)", idSpace.get().value() );
        }
        else
        {
            return ":ID";
        }
    }

    @Override
    public JsonNode toJson()
    {
        ObjectNode root = JsonNodeFactory.instance.objectNode();

        root.put( "type", getClass().getSimpleName() );
        root.put( "name", name.isPresent() ? name.get() : "" );
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
