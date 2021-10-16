package com.jenkov.db.test.mapping;

import com.jenkov.db.PersistenceManager;
import com.jenkov.db.impl.mapping.*;
import com.jenkov.db.impl.mapping.method.MethodMapping;
import com.jenkov.db.itf.PersistenceException;
import com.jenkov.db.itf.mapping.*;
import com.jenkov.db.test.Environment;
import com.jenkov.db.test.objects.*;
import com.jenkov.db.util.JdbcUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.Collection;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Jakob Jenkov,  Jenkov Development
 */
public class ObjectMapperTest {

    protected IObjectMapper     mapper          = null;
    protected PersistentObject  persistentObject= null;
    protected Connection        connection      = null;
    protected PersistenceManager persistenceManager = new PersistenceManager();
    protected IObjectMappingFactory mappingFactory = new ObjectMappingFactory();

    @BeforeEach
    public void setUp() throws Exception{
        this.mapper = new ObjectMapper(new ObjectMappingFactory());
        this.persistentObject = new PersistentObject();
        this.connection = Environment.getConnection();
    }

    @AfterEach
    public void tearDown() throws Exception{
        this.mapper           = null;
        this.persistentObject = null;
        JdbcUtil.close(this.connection);
        this.connection       = null;
    }


    @Test
    public void testGetSetDbNameDeterminer() throws Exception{
        assertNotNull(this.mapper.getDbNameDeterminer(), "should have default name determiner");

        IDbNameDeterminer nameDeterminer = new DbNameDeterminer();
        this.mapper.setDbNameDeterminer(nameDeterminer);
        assertSame(nameDeterminer, this.mapper.getDbNameDeterminer(), "should be same name determiners");

        this.mapper.setDbNameDeterminer(null);
        assertNull(this.mapper.getDbNameDeterminer(), "no name determiner");
    }

    @Test
    public void testGetSetDbNameGuesser() throws Exception{
        assertNotNull(this.mapper.getDbNameGuesser(), "should have default name guesser");

        IDbNameGuesser nameGuesser = new DbNameGuesser();
        this.mapper.setDbNameGuesser(nameGuesser);
        assertSame(nameGuesser, this.mapper.getDbNameGuesser(), "should be same name guessers");

        this.mapper.setDbNameGuesser(null);
        assertNull(this.mapper.getDbNameGuesser(), "no name guesser");
    }

    @Test
    public void testGetSetPrimaryKeyDeterminer() throws Exception{
        assertNotNull(this.mapper.getDbPrimaryKeyDeterminer(), "should have default primary key determiner");

        IDbPrimaryKeyDeterminer dbPkDeterminer = new DbPrimaryKeyDeterminer();
        this.mapper.setDbPrimaryKeyDeterminer(dbPkDeterminer);
        assertSame(dbPkDeterminer, this.mapper.getDbPrimaryKeyDeterminer(), "should be same primary key determiners");

        this.mapper.setDbPrimaryKeyDeterminer(null);
        assertNull(this.mapper.getDbPrimaryKeyDeterminer(), "no primary key determiner");
    }

    @Test
    public void testMapSettersToSelf() throws Exception{
        IObjectMapping mapping = this.mapper.mapSettersToSelf(PersistentObject.class, null);

        assertNull(mapping.getTableName(), "No table name should be used when method to self");
        assertEquals(0, mapping.getGetterMappings().size(), "wrong number of getter mappings");

        IMethodMapping fieldMapping = null;

        Collection fieldMappings = mapping.getSetterMappings();
        fieldMapping = mappingFactory.createSetterMapping(
                PersistentObject.class.getMethod("setId", new Class[] {long.class}), "setId", false);
        assertTrue  (fieldMappings.contains(fieldMapping), "setId not included in method mapping");

        fieldMapping = mappingFactory.createSetterMapping(
                PersistentObject.class.getMethod("setName", new Class[] {String.class}), "setName", false);
        assertTrue  (fieldMappings.contains(fieldMapping), "setName not included in method mapping");
    }

    @Test
    public void testMapSettersToSelf_overloadedSetters() throws Exception {
        IObjectMapping mapping = this.mapper.mapSettersToSelf(OverloadedSetters.class, null);

        assertNull(mapping.getTableName(), "No table name should be used when method to self");
        assertEquals(0, mapping.getGetterMappings().size(), "wrong number of getter mappings");

        IMethodMapping fieldMapping = null;
        IMethodMapping fieldMapping2 = null;
        fieldMapping = mappingFactory.createSetterMapping(
                OverloadedSetters.class.getMethod("setAge", new Class[] {int.class}), "setAge", false);
        fieldMapping2 = mappingFactory.createSetterMapping(
                OverloadedSetters.class.getMethod("setAge", new Class[] {String.class}), "setAge", false);
        assertFalse(fieldMapping.equals(fieldMapping2));
        assertFalse(fieldMapping2.equals(fieldMapping));

        Collection fieldMappings = mapping.getSetterMappings();
        fieldMapping = mappingFactory.createSetterMapping(
                OverloadedSetters.class.getMethod("setAge", new Class[] {int.class}), "setAge", false);
        assertTrue  (fieldMappings.contains(fieldMapping), "setAge not included in method mapping");
        fieldMapping = mappingFactory.createSetterMapping(
                OverloadedSetters.class.getMethod("setAge", new Class[] {String.class}), "setAge", false);
        assertFalse  (fieldMappings.contains(fieldMapping), "setAge included in method mapping");

        fieldMapping = mappingFactory.createSetterMapping(
                OverloadedSetters.class.getMethod("setName", new Class[] {String.class}), "setName", false);
        assertTrue  (fieldMappings.contains(fieldMapping), "setName not included in method mapping");
        fieldMapping = mappingFactory.createSetterMapping(
                OverloadedSetters.class.getMethod("setName", new Class[] {Double.class}), "setName", false);
        assertFalse  (fieldMappings.contains(fieldMapping), "setName not included in method mapping");

        fieldMapping = mappingFactory.createSetterMapping(
                 OverloadedSetters.class.getMethod("setHeight", new Class[] {int.class}), "setHeight", false);
        fieldMapping2 = mappingFactory.createSetterMapping(
                 OverloadedSetters.class.getMethod("setHeight", new Class[] {long.class}), "setHeight", false);
        assertTrue(fieldMappings.contains(fieldMapping)  || fieldMappings.contains(fieldMapping2));
        assertFalse(fieldMappings.contains(fieldMapping) && fieldMappings.contains(fieldMapping2));

        fieldMapping = mappingFactory.createSetterMapping(
                 OverloadedSetters.class.getMethod("setWidth", new Class[] {int.class}), "setWidth", false);
        fieldMapping2 = mappingFactory.createSetterMapping(
                 OverloadedSetters.class.getMethod("setWidth", new Class[] {short.class}), "setWidth", false);
        assertTrue(fieldMappings.contains(fieldMapping)  || fieldMappings.contains(fieldMapping2));
        assertFalse(fieldMappings.contains(fieldMapping) && fieldMappings.contains(fieldMapping2));
    }


    @Test
    public void testMapToTable() throws Exception{
        IObjectMapping mapping  = this.mapper.mapToTable(PersistentObject.class, null, this.connection, null, null);

        assertNotNull(mapping, "Mapping was null");
        assertEquals("persistent_object", mapping.getTableName().toLowerCase(), "Mapping table wrong");
        assertEquals(PersistentObject.class, mapping.getObjectClass(), "Mapping class wrong");

        //test primary key mapping
        subtestPrimaryKeyMappings(mapping);

        //test getters
        subtestGetterMappings(mapping);

        //test setters
        subtestSetterMappings(mapping);
    }

    @Test
    public void testMapClassToNonSimilarTableName() throws Exception {
        IObjectMapping mapping = null;

        if(Environment.isHsqldb() || Environment.isH2() || Environment.isDerby()){
            mapping = this.mapper.mapToTable(
                    PersistentObject.class, null, this.connection, null, "other_persistent_object".toUpperCase());
        } else {
            mapping = this.mapper.mapToTable(
                   PersistentObject.class, null, this.connection, null, "other_persistent_object");
        }

        assertNotNull(mapping, "Mapping was null");
        assertEquals(mapping.getTableName().toLowerCase(), "other_persistent_object", "Mapping table wrong");
        assertEquals(PersistentObject.class, mapping.getObjectClass(), "Mapping class wrong");

        //test primary key mapping
        subtestPrimaryKeyMappings(mapping);

        //test getters
        subtestGetterMappings(mapping);

        //test setters
        subtestSetterMappings(mapping);
    }


    private void subtestPrimaryKeyMappings(IObjectMapping mapping) throws NoSuchMethodException, PersistenceException {
        IMethodMapping fieldMapping;
        IMethodMapping fieldMappingUpperCase;

        String primaryKey = mapping.getPrimaryKey().getColumn();
        assertEquals("id", primaryKey.toLowerCase(), "wrong primary key");

        fieldMapping = mappingFactory.createGetterMapping(PersistentObject.class.getMethod("getId", null), "id", true);
        fieldMappingUpperCase = mappingFactory.createGetterMapping(PersistentObject.class.getMethod("getId", null), "ID", true);
        assertTrue(mapping.getGetterMapping(mapping.getPrimaryKey().getColumn()).equals(fieldMapping) ||
                   mapping.getGetterMapping(mapping.getPrimaryKey().getColumn()).equals(fieldMappingUpperCase),
                   "wrong getter mapping as primary key"
        );

        fieldMapping = mappingFactory.createSetterMapping(PersistentObject.class.getMethod("setId", new Class[]{long.class}), "id", true);
        fieldMappingUpperCase = mappingFactory.createSetterMapping(PersistentObject.class.getMethod("setId", new Class[]{long.class}), "ID", true);
        assertTrue(
                mapping.getSetterMapping(mapping.getPrimaryKey().getColumn()).equals(fieldMapping) ||
                mapping.getSetterMapping(mapping.getPrimaryKey().getColumn()).equals(fieldMappingUpperCase),
                "wrong setter mapping as primary key"
        );
    }

    @Test
    public void testMapGettersToTable() throws Exception{
        IObjectMapping mapping = this.mapper.mapGettersToTable(PersistentObject.class, null, this.connection, null, null);
        assertNotNull(mapping, "no mapping returned");

        //test object mapping instance is reused if provided.
        mapping = new ObjectMapping();
        IObjectMapping mapping2 = this.mapper.mapGettersToTable(PersistentObject.class, mapping, this.connection, null, null);
        assertSame(mapping, mapping2, "should be same object mapping");

        subtestTableNameObjectClassPrimaryKey(mapping);
        subtestObjectClassForMapGettersToTable();
        subtestTableNameForMapGettersToTable();


        //test all getter mappings.
        subtestGetterMappings(mapping);
    }


    private void subtestGetterMappings(IObjectMapping mapping) throws NoSuchMethodException {
//        System.out.println(mapping);
        assertEquals(5, mapping.getGetterMappings().size(), "wrong number of getter method mappings");


        IGetterMapping fieldMappingLowerCase =  mappingFactory.createGetterMapping(
                PersistentObject.class.getMethod("getId", null), "id", true);
        IGetterMapping fieldMappingUpperCase =  mappingFactory.createGetterMapping(
                PersistentObject.class.getMethod("getId", null), "ID", true);
        assertTrue(
                (mapping.getGetterMappings().contains(fieldMappingLowerCase) ||
                mapping.getGetterMappings().contains(fieldMappingUpperCase))
                , "should contain getId method mapping"
        );

        fieldMappingLowerCase = mappingFactory.createGetterMapping(
                PersistentObject.class.getMethod("getName", null), "name", true);
        fieldMappingUpperCase = mappingFactory.createGetterMapping(
                PersistentObject.class.getMethod("getName", null), "NAME", true);
        assertTrue(
                mapping.getGetterMappings().contains(fieldMappingLowerCase) ||
                mapping.getGetterMappings().contains(fieldMappingUpperCase),
                "should contain getName method mapping"
                );

        IGetterMapping fieldMapping = mappingFactory.createGetterMapping(
                PersistentObject.class.getMethod("getObjectValue", null), "objectValue", true);
        fieldMappingUpperCase = mappingFactory.createGetterMapping(
                PersistentObject.class.getMethod("getObjectValue", null), "OBJECTVALUE", true);
        fieldMappingLowerCase = mappingFactory.createGetterMapping(
                PersistentObject.class.getMethod("getObjectValue", null), "objectvalue", true);
        assertTrue(
                mapping.getGetterMappings().contains(fieldMapping) ||
                mapping.getGetterMappings().contains(fieldMappingLowerCase) ||
                mapping.getGetterMappings().contains(fieldMappingUpperCase),
                "should contain getObjectValue method mapping"
                );
    }



    protected void subtestTableNameObjectClassPrimaryKey(IObjectMapping mapping) throws Exception{
        assertEquals("persistent_object"   , mapping.getTableName().toLowerCase(), "wrong table name");
        assertEquals(PersistentObject.class, mapping.getObjectClass(), "wrong object class");
        assertEquals("id"                  , mapping.getPrimaryKey().getColumn().toLowerCase(), "wrong primary key");
    }


    private void subtestObjectClassForMapGettersToTable() throws Exception{
        //test object class is used if one is provided, and there is none in the object mapping
        IObjectMapping mapping = this.mapper.mapGettersToTable(PersistentObject.class, null, this.connection, null, null);
        assertEquals(PersistentObject.class, mapping.getObjectClass(), "should have PersistentObject.class");


        //test object class conflict (different names in parameter and object mapping)
        try {
            mapping = this.mapper.mapGettersToTable(MethodMapping.class, mapping, this.connection, null, null);
            fail("should have conflict on object classes");
        } catch (PersistenceException e) {
            //ignore, an exception is supposed to be thrown when object mapping's object class is not
            // the same as parameter
        }


        //test object class equality (same name in parameter and object mapping)
        mapping = this.mapper.mapGettersToTable(PersistentObject.class, mapping, this.connection, null, null);
        assertEquals(PersistentObject.class, mapping.getObjectClass(), "should be equal classes");

        //test failure on no class provided in either object mapping or parameter.
        mapping.setObjectClass(null);
        try {
            mapping = this.mapper.mapGettersToTable(null, mapping, this.connection, null, null);
            fail("should throw exception on no class provided");
        } catch (PersistenceException e) {
            //ignore, an exception is supposed to be thrown when object mapping's object class is not
            // the same as parameter
        }

    }

    protected void subtestTableNameForMapGettersToTable() throws Exception {

        //test table name is used if one is provided, and there is none in the object mapping
        String tableName = new String("persistent_object" + "");
        IObjectMapping mapping = this.mapper.mapGettersToTable(PersistentObject.class, null, this.connection, null, tableName);
        assertSame(tableName, mapping.getTableName(), "provided table name not used");

        //test table name conflict (different names in parameter and object mapping)
        tableName = "PERSISTENT_OBJECT";
        try{
            mapping = this.mapper.mapGettersToTable(PersistentObject.class, mapping, this.connection, null, tableName);
            fail("should give table name conflict");
        } catch(PersistenceException e){
            //ignore, an exception is supposed to be thrown.
        }

        //test table name from object mapping is used if present, if none is provided as parameter.
        mapping = this.mapper.mapGettersToTable(PersistentObject.class, mapping, this.connection, null, null);
        assertEquals("persistent_object", mapping.getTableName(), "should contain table name from object mapping");


        //test table name equality (same name in parameter and object mapping)
        tableName   = new String("" + "persistent_object");
        assertNotSame(mapping.getTableName(), tableName, "should not be same, only equal");
        mapping     = mapping = this.mapper.mapGettersToTable(PersistentObject.class, mapping, this.connection, null, tableName);
    }


    @Test
    public void testMapSettersToTable() throws Exception{
        IObjectMapping mapping = this.mapper.mapSettersToTable(PersistentObject.class, null, this.connection, null, null);
        assertNotNull(mapping, "no mapping returned");

        //test object mapping instance is reused if provided.
        mapping = new ObjectMapping();
        IObjectMapping mapping2 = this.mapper.mapSettersToTable(PersistentObject.class, mapping, this.connection, null, null);
        assertSame(mapping, mapping2, "should be same object mapping");

        subtestTableNameObjectClassPrimaryKey(mapping);

        subtestObjectClassForMapSettersToTable();
        subtestTableNameForMapSettersToTable();

        //Test all setter method mappings.
        subtestSetterMappings(mapping);
    }


    private void subtestSetterMappings(IObjectMapping mapping) throws NoSuchMethodException {
        assertEquals(4, mapping.getSetterMappings().size(), "wrong number of setter method mappings");

        ISetterMapping fieldMappingLowerCase =  mappingFactory.createSetterMapping(
                PersistentObject.class.getMethod("setId", new Class[]{long.class}), "id", true);

        ISetterMapping fieldMappingUpperCase =  mappingFactory.createSetterMapping(
                PersistentObject.class.getMethod("setId", new Class[]{long.class}), "ID", true);


        assertTrue(mapping.getSetterMappings().contains(fieldMappingUpperCase) ||
                   mapping.getSetterMappings().contains(fieldMappingLowerCase),
                   "should contain setId method mapping" );

        fieldMappingLowerCase = mappingFactory.createSetterMapping(
                PersistentObject.class.getMethod("setName", new Class[]{String.class}), "name", true);
        fieldMappingUpperCase = mappingFactory.createSetterMapping(
                PersistentObject.class.getMethod("setName", new Class[]{String.class}), "NAME", true);

        assertTrue(mapping.getSetterMappings().contains(fieldMappingUpperCase) ||
                   mapping.getSetterMappings().contains(fieldMappingLowerCase),
                   "should contain setName method mapping"
        );
    }


    private void subtestObjectClassForMapSettersToTable() throws Exception{
        //test object class is used if one is provided, and there is none in the object mapping
        IObjectMapping mapping = this.mapper.mapSettersToTable(PersistentObject.class, null, this.connection, null, null);
        assertEquals(PersistentObject.class, mapping.getObjectClass(), "should have PersistentObject.class");


        //test object class conflict (different names in parameter and object mapping)
        try {
            mapping = this.mapper.mapSettersToTable(com.jenkov.db.impl.mapping.method.MethodMapping.class, mapping, this.connection, null, null);
            fail("should have conflict on object classes");
        } catch (PersistenceException e) {
            //ignore, an exception is supposed to be thrown when object mapping's object class is not
            // the same as parameter
        }


        //test object class equality (same name in parameter and object mapping)
        mapping = this.mapper.mapSettersToTable(PersistentObject.class, mapping, this.connection, null, null);
        assertEquals(PersistentObject.class, mapping.getObjectClass(), "should be equal classes");

        //test failure on no class provided in either object mapping or parameter.
        mapping.setObjectClass(null);
        try {
            mapping = this.mapper.mapSettersToTable(null, mapping, this.connection, null, null);
            fail("should throw exception on no class provided");
        } catch (PersistenceException e) {
            //ignore, an exception is supposed to be thrown when object mapping's object class is not
            // the same as parameter
        }

    }


    protected void subtestTableNameForMapSettersToTable() throws Exception {

        //test table name is used if one is provided, and there is none in the object mapping
        String tableName = new String("persistent_object" + "");
        IObjectMapping mapping = this.mapper.mapSettersToTable(PersistentObject.class, null, this.connection, null, tableName);
        assertSame(tableName, mapping.getTableName(), "provided table name not used");

        //test table name conflict (different names in parameter and object mapping)
        tableName = "PERSISTENT_OBJECT";
        try{
            mapping = this.mapper.mapSettersToTable(PersistentObject.class, mapping, this.connection, null, tableName);
            fail("should give table name conflict");
        } catch(PersistenceException e){
            //ignore, an exception is supposed to be thrown.
        }

        //test table name from object mapping is used if present, if none is provided as parameter.
        mapping = this.mapper.mapSettersToTable(PersistentObject.class, mapping, this.connection, null, null);
        assertEquals("persistent_object", mapping.getTableName(), "should contain table name from object mapping");


        //test table name equality (same name in parameter and object mapping)
        tableName   = new String("" + "persistent_object");
        assertNotSame(mapping.getTableName(), tableName, "should not be same, only equal");
        mapping     = mapping = this.mapper.mapSettersToTable(PersistentObject.class, mapping, this.connection, null, tableName);
    }

}
