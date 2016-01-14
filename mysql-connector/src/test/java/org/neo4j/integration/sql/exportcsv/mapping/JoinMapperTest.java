package org.neo4j.integration.sql.exportcsv.mapping;

import java.util.Collection;

import org.junit.Test;

import org.neo4j.integration.neo4j.importcsv.config.Formatting;
import org.neo4j.integration.neo4j.importcsv.fields.CsvField;
import org.neo4j.integration.neo4j.importcsv.fields.IdSpace;
import org.neo4j.integration.sql.metadata.Join;
import org.neo4j.integration.sql.metadata.TableName;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

public class JoinMapperTest
{
    @Test
    public void shouldCreateMappingsForJoin()
    {
        // given
        Join join = Join.builder()
                .parentTable( new TableName( "test.Person" ) )
                .primaryKey( "id" )
                .foreignKey( "addressId" )
                .childTable( new TableName( "test.Address" ) )
                .build();

        JoinMapper mapper = new JoinMapper( Formatting.DEFAULT );

        // when
        ColumnToCsvFieldMappings mappings = mapper.createMappings( join );

        // then
        Collection<CsvField> fields = mappings.fields();

        assertThat( fields, contains(
                CsvField.startId( new IdSpace( "test.Person" ) ),
                CsvField.endId( new IdSpace( "test.Address" ) ),
                CsvField.relationshipType() ) );
    }
}
