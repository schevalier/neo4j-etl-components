package org.neo4j.ingest.config;

import org.junit.Test;

import org.neo4j.ingest.config.Field;
import org.neo4j.ingest.config.FieldType;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class FieldTest
{
    @Test
    public void shouldTreatNullFieldNameAsNullForValidationPurposes()
    {
        // given
        FieldType fieldType = mock( FieldType.class );


        // when
        new Field( null, fieldType );

        // then
        verify( fieldType ).validate( false );
    }

    @Test
    public void shouldTreatEmptyStringFieldNameAsNullForValidationPurposes()
    {
        // given
        FieldType fieldType = mock( FieldType.class );


        // when
        new Field( "", fieldType );

        // then
        verify( fieldType ).validate( false );
    }

    @Test
    public void shouldTreatWhitespaceStringFieldNameAsNullForValidationPurposes()
    {
        // given
        FieldType fieldType = mock( FieldType.class );


        // when
        new Field( " \t", fieldType );

        // then
        verify( fieldType ).validate( false );
    }
}
