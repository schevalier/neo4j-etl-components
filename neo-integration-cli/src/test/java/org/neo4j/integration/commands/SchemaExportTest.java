package org.neo4j.integration.commands;

import java.util.Collection;
import java.util.Collections;

import org.junit.Test;

import org.neo4j.integration.neo4j.importcsv.config.formatting.Formatting;
import org.neo4j.integration.sql.exportcsv.ColumnUtil;
import org.neo4j.integration.sql.exportcsv.DatabaseExportSqlSupplier;
import org.neo4j.integration.sql.exportcsv.io.TinyIntResolver;
import org.neo4j.integration.sql.exportcsv.mapping.RelationshipNameFrom;
import org.neo4j.integration.sql.exportcsv.mapping.RelationshipNameResolver;
import org.neo4j.integration.sql.exportcsv.mapping.TinyIntAs;
import org.neo4j.integration.sql.metadata.ColumnRole;
import org.neo4j.integration.sql.metadata.Join;
import org.neo4j.integration.sql.metadata.JoinKey;
import org.neo4j.integration.sql.metadata.JoinTable;
import org.neo4j.integration.sql.metadata.Table;
import org.neo4j.integration.sql.metadata.TableName;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

public class SchemaExportTest
{
    private final ColumnUtil columnUtil = new ColumnUtil();
    private TinyIntResolver tinyIntResolver = new TinyIntResolver( TinyIntAs.BYTE );

    @Test
    public void shouldThrowExceptionIfParentOfJoinIsNotPresentInTables()
    {
        try
        {
            // when
            TableName leftTable = new TableName( "test.Person" );
            TableName rightTable = new TableName( "test.Address" );


            Collection<Table> tables = Collections.singletonList( Table.builder()
                    .name( rightTable )
                    .addColumn( columnUtil.keyColumn( rightTable, "id", ColumnRole.PrimaryKey ) )
                    .addColumn( columnUtil.column( rightTable, "postcode", ColumnRole.Data ) )
                    .build() );

            Collection<Join> joins = Collections.singletonList( new Join(
                    new JoinKey(
                            columnUtil.keyColumn( leftTable, "id", ColumnRole.PrimaryKey ),
                            columnUtil.keyColumn( leftTable, "id", ColumnRole.PrimaryKey ) ),
                    new JoinKey(
                            columnUtil.column( leftTable, "addressId", ColumnRole.ForeignKey ),
                            columnUtil.keyColumn( rightTable, "id", ColumnRole.PrimaryKey ) )
            ) );

            SchemaExport schemaExport = new SchemaExport( tables, joins, Collections.<JoinTable>emptyList() );

            schemaExport.generateMetadataMappings( Formatting.DEFAULT, mock( DatabaseExportSqlSupplier.class ),
                    new RelationshipNameResolver( RelationshipNameFrom.TABLE_NAME ), tinyIntResolver );

            fail( "Expected IllegalStatException" );
        }
        catch ( IllegalStateException e )
        {
            // then
            assertEquals( "Config is missing table definition 'test.Person' for join [test.Person -> test.Address]",
                    e.getMessage() );
        }
    }

    @Test
    public void shouldThrowExceptionIfChildOfJoinIsNotPresentInTables()
    {
        try
        {
            // when
            TableName leftTable = new TableName( "test.Person" );
            TableName rightTable = new TableName( "test.Address" );

            Collection<Table> tables = Collections.singletonList( Table.builder()
                    .name( leftTable )
                    .addColumn( columnUtil.keyColumn( leftTable, "id", ColumnRole.PrimaryKey ) )
                    .addColumn( columnUtil.column( leftTable, "username", ColumnRole.Data ) )
                    .addColumn( columnUtil.column( leftTable, "addressId", ColumnRole.ForeignKey ) )
                    .build() );

            Collection<Join> joins = Collections.singletonList( new Join(
                    new JoinKey(
                            columnUtil.keyColumn( leftTable, "id", ColumnRole.PrimaryKey ),
                            columnUtil.keyColumn( leftTable, "id", ColumnRole.PrimaryKey ) ),
                    new JoinKey(
                            columnUtil.column( leftTable, "addressId", ColumnRole.ForeignKey ),
                            columnUtil.keyColumn( rightTable, "id", ColumnRole.PrimaryKey ) )
            ) );

            SchemaExport schemaExport = new SchemaExport( tables, joins, Collections.<JoinTable>emptyList() );

            schemaExport.generateMetadataMappings( Formatting.DEFAULT, mock( DatabaseExportSqlSupplier.class ), new
                    RelationshipNameResolver( RelationshipNameFrom.COLUMN_NAME ), tinyIntResolver );

            fail( "Expected IllegalStateException" );
        }
        catch ( IllegalStateException e )
        {
            // then
            assertEquals( "Config is missing table definition 'test.Address' for join [test.Person -> test.Address]",
                    e.getMessage() );
        }
    }
}
