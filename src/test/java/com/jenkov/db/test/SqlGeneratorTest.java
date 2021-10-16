package com.jenkov.db.test;

import com.jenkov.db.impl.SqlGenerator;
import com.jenkov.db.impl.mapping.ObjectMapper;
import com.jenkov.db.impl.mapping.ObjectMapping;
import com.jenkov.db.impl.mapping.ObjectMappingFactory;
import com.jenkov.db.itf.ISqlGenerator;
import com.jenkov.db.itf.PersistenceException;
import com.jenkov.db.itf.mapping.IObjectMapping;
import com.jenkov.db.itf.mapping.IGetterMapping;
import com.jenkov.db.test.objects.CompoundPkObject;
import com.jenkov.db.test.objects.PersistentObject;
import com.jenkov.db.util.JdbcUtil;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Jakob Jenkov,  Jenkov Development
 */
public class SqlGeneratorTest {

    protected ISqlGenerator  generator  = null;
    protected IObjectMapping mapping    = null;
    protected IObjectMapping compoundMapping    = null;
    protected Connection     connection = null;

    public void setUp() throws Exception{
        this.generator  = new SqlGenerator();
        this.connection = Environment.getConnection();
        this.mapping = new ObjectMapper(new ObjectMappingFactory()).mapToTable(PersistentObject.class, null, this.connection, (String) null, null);
        this.compoundMapping = new ObjectMapper(new ObjectMappingFactory()).mapToTable(CompoundPkObject.class, null, this.connection, (String) null, null);
    }

    public void tearDown(){
        this.generator  = null;
        JdbcUtil.closeIgnore(this.connection);
    }

    public void testGenerateReadByPrimaryKeyStatement() throws Exception {
        String readByPrimaryKeySql =
                this.generator.generateReadByPrimaryKeyStatement(this.mapping).toLowerCase();

        assertTrue(readByPrimaryKeySql.startsWith("select "), "should start with select");
        assertTrue(readByPrimaryKeySql.indexOf("id") > -1, "should contain 'id' in select statement");
        assertTrue(readByPrimaryKeySql.indexOf("name") > -1, "should contain 'name' in select statement");
        assertTrue(readByPrimaryKeySql.indexOf("persistent_object") > -1, "should contain 'persistent_object' in select statement");
        assertTrue(readByPrimaryKeySql.indexOf("objectValue") == -1, "should not contain 'objectValue' in select statement");
        assertTrue(readByPrimaryKeySql.indexOf(" where id = ?") > -1, "should contain ' where id = ?' in select statement");
    }

    public void testGenerateReadListByPrimaryKeysStatement() throws Exception {
        String readByPrimaryKeySql =
                this.generator.generateReadListByPrimaryKeysStatement(this.mapping, 1).toLowerCase();

        assertTrue(readByPrimaryKeySql.startsWith("select "), "should start with select");
        assertTrue(readByPrimaryKeySql.indexOf("id") > -1, "should contain 'id' in select statement");
        assertTrue(readByPrimaryKeySql.indexOf("name") > -1, "should contain 'name' in select statement");
        assertTrue(readByPrimaryKeySql.indexOf("persistent_object") > -1, "should contain 'persistent_object' in select statement");
        assertTrue(readByPrimaryKeySql.indexOf("objectValue") == -1, "should not contain 'objectValue' in select statement");
        assertTrue(readByPrimaryKeySql.indexOf(" where (id = ?)") > -1, "should contain ' where (id = ?)' in select statement");


        readByPrimaryKeySql =
                this.generator.generateReadListByPrimaryKeysStatement(this.mapping, 3).toLowerCase();

        assertTrue(readByPrimaryKeySql.startsWith("select "), "should start with select");
        assertTrue(readByPrimaryKeySql.indexOf("id") > -1, "should contain 'id' in select statement");
        assertTrue(readByPrimaryKeySql.indexOf("name") > -1, "should contain 'name' in select statement");
        assertTrue(readByPrimaryKeySql.indexOf("persistent_object") > -1, "should contain 'persistent_object' in select statement");
        assertTrue(readByPrimaryKeySql.indexOf("objectValue") == -1, "should not contain 'objectValue' in select statement");
        assertTrue(readByPrimaryKeySql.indexOf(" where (id = ?) or (id = ?) or (id = ?)") > -1 ,
                "should contain ' where (id = ?) or (id = ?) or (id = ?)' in select statement"
        );


        try {
            readByPrimaryKeySql =
                    this.generator.generateReadListByPrimaryKeysStatement(this.mapping, 0).toLowerCase();
            fail("should not be able to generate sql with 0 primary key count");
        } catch (PersistenceException e) {
        }


    }

    public void testGenerateInsertStatement() throws Exception{
        String insertSql = this.generator.generateInsertStatement(this.mapping).toLowerCase();

        String beginning = "insert into persistent_object (";

        assertEquals(beginning, insertSql.substring(0, beginning.length()).toLowerCase(), "wrong sql generated for PersistentObject");
        assertTrue(insertSql.indexOf("id") > -1, "should contain id in insert statement");
        assertTrue(insertSql.indexOf("name") > -1, "should contain name in insert statement");
        assertTrue(insertSql.indexOf("objectvalue") > -1, "should contain object value in insert statement");
        assertTrue(insertSql.indexOf("some_boolean") > -1, "should contain some_boolean in insert statement");
        assertTrue(insertSql.indexOf(" values (?, ?, ?, ?, ?)") > -1, "should contain value statement in insert statement");

        IObjectMapping mapping2 = new ObjectMapping();
        try{
            insertSql = this.generator.generateInsertStatement(mapping2);
            fail("generator should throw exception on invalid object method");
        } catch(PersistenceException e){
            //ignore, an exception is supposed to be thrown without a table name in the object method
        }
    }

    public void testGenerateInsertStatement_autoGeneratedPrimaryKeys() throws PersistenceException {
        IGetterMapping getterMapping = this.compoundMapping.getGetterMapping("NAME") == null ?
                this.compoundMapping.getGetterMapping("name") : this.compoundMapping.getGetterMapping("NAME");

        getterMapping.setAutoGenerated(true);
        String insertSql = this.generator.generateInsertStatement(this.compoundMapping);
        assertTrue(insertSql.indexOf("(?, ?, )") ==  -1);
        assertFalse(insertSql.indexOf("(?, ?)") == -1);
    }

    public void testGenerateUpdateStatement() throws Exception{
        String updateSql = this.generator.generateUpdateStatement(this.mapping).toLowerCase();
        String beginning = "update persistent_object ";

        assertEquals("wrong sql generated for PersistentObject", beginning, updateSql.substring(0, beginning.length()));
        assertTrue(updateSql.indexOf("id = ?") > -1, "should contain id in update statement");
        assertTrue(updateSql.indexOf("name = ?") > -1, "should contain name in update statement");
        assertTrue(updateSql.indexOf("objectvalue = ?") > -1,"should contain object value in update statement" );
        assertTrue(updateSql.indexOf("autocolumn = ?") > -1, "should contain auto column in update statement");
        assertTrue(updateSql.indexOf("where id = ?") > -1, "should contain where clause in update statement");

        IObjectMapping mapping2 = new ObjectMapping();
        try{
            updateSql = this.generator.generateUpdateStatement(mapping2);
            fail("generator should throw exception on invalid object method");
        } catch(PersistenceException e){
            //ignore, an exception is supposed to be thrown without a table name in the object method
        }
    }

    public void testGenerateUpdateStatement_autoGeneratedPrimaryKeys() throws Exception{
        IGetterMapping getterMapping = this.compoundMapping.getGetterMapping("NAME") == null ?
                this.compoundMapping.getGetterMapping("name") : this.compoundMapping.getGetterMapping("NAME");
        String updateSql = this.generator.generateUpdateStatement(this.compoundMapping);

        getterMapping.setAutoGenerated(true);

//        System.out.println("updateSql = " + updateSql);

        assertTrue (updateSql.indexOf("ID = ?, ID2 = ?,  where ID") ==  -1 || updateSql.indexOf("id = ?, id2 = ?,  where id") == -1);
        assertFalse(updateSql.indexOf("ID = ?, ID2 = ?, NAME = ? where ID")   == -1 && updateSql.indexOf("id = ?, id2 = ?, name = ? where id") ==  -1);
    }



    public void testGenerateUpdateStatementWithAutoGeneratedColumns() throws Exception {
        if(this.mapping.getGetterMapping("autoColumn") != null){
            this.mapping.getGetterMapping("autoColumn").setAutoGenerated(true);
        }
        if(this.mapping.getGetterMapping("autocolumn") != null){
            this.mapping.getGetterMapping("autocolumn").setAutoGenerated(true);
        }
        if(this.mapping.getGetterMapping("AUTOCOLUMN") != null){
            this.mapping.getGetterMapping("AUTOCOLUMN").setAutoGenerated(true);
        }
        String updateSql = this.generator.generateUpdateStatement(this.mapping).toLowerCase();
        String beginning = "update persistent_object ";

        assertEquals("wrong sql generated for PersistentObject", beginning, updateSql.substring(0, beginning.length()));
        assertTrue(updateSql.indexOf("id = ?") > -1, "should contain id in update statement");
        assertTrue(updateSql.indexOf("name = ?") > -1, "should contain name in update statement");
        assertTrue(updateSql.indexOf("objectvalue = ?") > -1, "should contain object value in update statement");
        assertFalse(updateSql.indexOf("autocolumn = ?") > -1, "should NOT contain auto column in update statement");
        assertTrue(updateSql.indexOf("where id = ?") > -1, "should contain where clause in update statement");
    }

    public void testGenerateUpdateStatement_compoundKeys() throws Exception{
        String sql = this.generator.generateUpdateStatement(this.compoundMapping);
        if(Environment.isHsqldb()){
            assertEquals("sql should be this",
                "update COMPOUND_PK_OBJECTS set ID = ?, ID2 = ?, NAME = ? where ID = ? and ID2 = ?", sql);
        } else  {
            assertEquals("sql should be this",
                "update compound_pk_objects set id = ?, id2 = ?, name = ? where id = ? and id2 = ?", sql.toLowerCase());
        }
    }


    public void testGenerateDeleteStatement() throws Exception{
        String deleteSql = this.generator.generateDeleteStatement(this.mapping).toLowerCase();
        String beginning = "delete from persistent_object where id = ?";

        assertEquals("wrong sql generated for PersistentObject",
                beginning, deleteSql.substring(0, beginning.length()).toLowerCase());

        IObjectMapping mapping2 = new ObjectMapping();
        try{
            deleteSql = this.generator.generateDeleteStatement(mapping2);
            fail("generator should throw exception on invalid object method");
        } catch(PersistenceException e){
            //ignore, an exception is supposed to be thrown without a table name in the object method
        }
    }

    public void testGenerateDeleteStatement_compoundKeys() throws Exception{
        String sql = this.generator.generateDeleteStatement(this.compoundMapping);
        if(Environment.isHsqldb()){
            assertEquals("sql should be this",
                "delete from COMPOUND_PK_OBJECTS where ID = ? and ID2 = ?", sql);
        } else  {
            assertEquals("sql should be this",
                "delete from compound_pk_objects where id = ? and id2 = ?", sql.toLowerCase());
        }
    }


}
