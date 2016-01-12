package org.neo4j.ingest.config;

import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class CsvFieldTest
{
    @Test
    public void shouldTreatNullFieldNameAsNullForValidationPurposes()
    {
        // given
        CsvFieldType fieldType = mock( CsvFieldType.class );


        // when
        new CsvField( null, fieldType );

        // then
        verify( fieldType ).validate( false );
    }

    @Test
    public void shouldTreatEmptyStringFieldNameAsNullForValidationPurposes()
    {
        // given
        CsvFieldType fieldType = mock( CsvFieldType.class );


        // when
        new CsvField( "", fieldType );

        // then
        verify( fieldType ).validate( false );
    }

    @Test
    public void shouldTreatWhitespaceStringFieldNameAsNullForValidationPurposes()
    {
        // given
        CsvFieldType fieldType = mock( CsvFieldType.class );


        // when
        new CsvField( " \t", fieldType );

        // then
        verify( fieldType ).validate( false );
    }
}
