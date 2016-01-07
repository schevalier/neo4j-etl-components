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

        Field field = new Field( null, fieldType );

        // when
        field.validate();

        // then
        verify( fieldType ).validate( false );
    }

    @Test
    public void shouldTreatEmptyStringFieldNameAsNullForValidationPurposes()
    {
        // given
        FieldType fieldType = mock( FieldType.class );

        Field field = new Field( "", fieldType );

        // when
        field.validate();

        // then
        verify( fieldType ).validate( false );
    }

    @Test
    public void shouldTreatWhitespaceStringFieldNameAsNullForValidationPurposes()
    {
        // given
        FieldType fieldType = mock( FieldType.class );

        Field field = new Field( " \t", fieldType );

        // when
        field.validate();

        // then
        verify( fieldType ).validate( false );
    }
}
