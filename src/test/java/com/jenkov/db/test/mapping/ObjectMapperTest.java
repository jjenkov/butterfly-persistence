package com.jenkov.db.test.mapping;

import com.jenkov.db.PersistenceManager;
import com.jenkov.db.impl.mapping.*;
import com.jenkov.db.impl.mapping.method.MethodMapping;
import com.jenkov.db.itf.PersistenceException;
import com.jenkov.db.itf.mapping.*;
import com.jenkov.db.test.Environment;
import com.jenkov.db.test.objects.*;
import com.jenkov.db.util.JdbcUtil;
import junit.framework.TestCase;

import java.sql.Connection;
import java.util.Collection;
import java.lang.reflect.Method;

/**
 * @author Jakob Jenkov,  Jenkov Development
 */
public class ObjectMapperTest extends TestCase{

    protected IObjectMapper     mapper          = null;
    protected PersistentObject  persistentObject= null;
    protected Connection        connection      = null;
    protected PersistenceManager persistenceManager = new PersistenceManager();
    protected IObjectMappingFactory mappingFactory = new ObjectMappingFactory();

    public void setUp() throws Exception{
        this.mapper = new ObjectMapper(new ObjectMappingFactory());
        this.persistentObject = new PersistentObject();
        this.connection = Environment.getConnection();
    }

    public void tearDown() throws Exception{
        this.mapper           = null;
        this.persistentObject = null;
        JdbcUtil.close(this.connection);
        this.connection       = null;
    }



    public void testGetSetDbNameDeterminer() throws Exception{
        assertNotNull("should have default name determiner", this.mapper.getDbNameDeterminer());

        IDbNameDeterminer nameDeterminer = new DbNameDeterminer();
        this.mapper.setDbNameDeterminer(nameDeterminer);
        assertSame("should be same name determiners", nameDeterminer, this.mapper.getDbNameDeterminer());

        this.mapper.setDbNameDeterminer(null);
        assertNull("no name determiner", this.mapper.getDbNameDeterminer());
    }

    public void testGetSetDbNameGuesser() throws Exception{
        assertNotNull("should have default name guesser", this.mapper.getDbNameGuesser());

        IDbNameGuesser nameGuesser = new DbNameGuesser();
        this.mapper.setDbNameGuesser(nameGuesser);
        assertSame("should be same name guessers", nameGuesser, this.mapper.getDbNameGuesser());

        this.mapper.setDbNameGuesser(null);
        assertNull("no name guesser", this.mapper.getDbNameGuesser());
    }

    public void testGetSetPrimaryKeyDeterminer() throws Exception{
        assertNotNull("should have default primary key determiner", this.mapper.getDbPrimaryKeyDeterminer());

        IDbPrimaryKeyDeterminer dbPkDeterminer = new DbPrimaryKeyDeterminer();
        this.mapper.setDbPrimaryKeyDeterminer(dbPkDeterminer);
        assertSame("should be same primary key determiners", dbPkDeterminer, this.mapper.getDbPrimaryKeyDeterminer());

        this.mapper.setDbPrimaryKeyDeterminer(null);
        assertNull("no primary key determiner", this.mapper.getDbPrimaryKeyDeterminer());
    }





    public void testMapSettersToSelf() throws Exception{
        IObjectMapping mapping = this.mapper.mapSettersToSelf(PersistentObject.class, null);

        assertNull("No table name should be used when method to self", mapping.getTableName());
        assertEquals("wrong number of getter mappings", 0, mapping.getGetterMappings().size());

        IMethodMapping fieldMapping = null;

        Collection fieldMappings = mapping.getSetterMappings();
        fieldMapping = mappingFactory.createSetterMapping(
                PersistentObject.class.getMethod("setId", new Class[] {long.class}), "setId", false);
        assertTrue  ("setId not included in method mapping", fieldMappings.contains(fieldMapping));

        fieldMapping = mappingFactory.createSetterMapping(
                PersistentObject.class.getMethod("setName", new Class[] {String.class}), "setName", false);
        assertTrue  ("setName not included in method mapping", fieldMappings.contains(fieldMapping));
    }

    public void testMapSettersToSelf_overloadedSetters() throws Exception {
        IObjectMapping mapping = this.mapper.mapSettersToSelf(OverloadedSetters.class, null);

        assertNull("No table name should be used when method to self", mapping.getTableName());
        assertEquals("wrong number of getter mappings", 0, mapping.getGetterMappings().size());

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
        assertTrue  ("setAge not included in method mapping", fieldMappings.contains(fieldMapping));
        fieldMapping = mappingFactory.createSetterMapping(
                OverloadedSetters.class.getMethod("setAge", new Class[] {String.class}), "setAge", false);
        assertFalse  ("setAge included in method mapping", fieldMappings.contains(fieldMapping));

        fieldMapping = mappingFactory.createSetterMapping(
                OverloadedSetters.class.getMethod("setName", new Class[] {String.class}), "setName", false);
        assertTrue  ("setName not included in method mapping", fieldMappings.contains(fieldMapping));
        fieldMapping = mappingFactory.createSetterMapping(
                OverloadedSetters.class.getMethod("setName", new Class[] {Double.class}), "setName", false);
        assertFalse  ("setName not included in method mapping", fieldMappings.contains(fieldMapping));

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



    public void testMapToTable() throws Exception{
        IObjectMapping mapping  = this.mapper.mapToTable(PersistentObject.class, null, this.connection, null, null);

        assertNotNull("Mapping was null", mapping);
        assertEquals("Mapping table wrong", "persistent_object", mapping.getTableName().toLowerCase());
        assertEquals("Mapping class wrong", PersistentObject.class, mapping.getObjectClass());

        //test primary key mapping
        subtestPrimaryKeyMappings(mapping);

        //test getters
        subtestGetterMappings(mapping);

        //test setters
        subtestSetterMappings(mapping);
    }

    public void testMapClassToNonSimilarTableName() throws Exception {
        IObjectMapping mapping = null;

        if(Environment.isHsqldb() || Environment.isH2() || Environment.isDerby()){
            mapping = this.mapper.mapToTable(
                    PersistentObject.class, null, this.connection, null, "other_persistent_object".toUpperCase());
        } else {
            mapping = this.mapper.mapToTable(
                   PersistentObject.class, null, this.connection, null, "other_persistent_object");
        }

        assertNotNull("Mapping was null", mapping);
        assertEquals("Mapping table wrong", "other_persistent_object", mapping.getTableName().toLowerCase());
        assertEquals("Mapping class wrong", PersistentObject.class, mapping.getObjectClass());

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
        assertEquals("wrong primary key", "id", primaryKey.toLowerCase());

        fieldMapping = mappingFactory.createGetterMapping(PersistentObject.class.getMethod("getId", null), "id", true);
        fieldMappingUpperCase = mappingFactory.createGetterMapping(PersistentObject.class.getMethod("getId", null), "ID", true);
        assertTrue("wrong getter mapping as primary key",
                mapping.getGetterMapping(mapping.getPrimaryKey().getColumn()).equals(fieldMapping) ||
                mapping.getGetterMapping(mapping.getPrimaryKey().getColumn()).equals(fieldMappingUpperCase));

        fieldMapping = mappingFactory.createSetterMapping(PersistentObject.class.getMethod("setId", new Class[]{long.class}), "id", true);
        fieldMappingUpperCase = mappingFactory.createSetterMapping(PersistentObject.class.getMethod("setId", new Class[]{long.class}), "ID", true);
        assertTrue("wrong setter mapping as primary key",
                mapping.getSetterMapping(mapping.getPrimaryKey().getColumn()).equals(fieldMapping) ||
                mapping.getSetterMapping(mapping.getPrimaryKey().getColumn()).equals(fieldMappingUpperCase));
    }


    public void testMapGettersToTable() throws Exception{
        IObjectMapping mapping = this.mapper.mapGettersToTable(PersistentObject.class, null, this.connection, null, null);
        assertNotNull("no mapping returned", mapping);

        //test object mapping instance is reused if provided.
        mapping = new ObjectMapping();
        IObjectMapping mapping2 = this.mapper.mapGettersToTable(PersistentObject.class, mapping, this.connection, null, null);
        assertSame("should be same object mapping", mapping, mapping2);

        subtestTableNameObjectClassPrimaryKey(mapping);
        subtestObjectClassForMapGettersToTable();
        subtestTableNameForMapGettersToTable();


        //test all getter mappings.
        subtestGetterMappings(mapping);
    }

    private void subtestGetterMappings(IObjectMapping mapping) throws NoSuchMethodException {
//        System.out.println(mapping);
        assertEquals("wrong number of getter method mappings", 5, mapping.getGetterMappings().size());


        IGetterMapping fieldMappingLowerCase =  mappingFactory.createGetterMapping(
                PersistentObject.class.getMethod("getId", null), "id", true);
        IGetterMapping fieldMappingUpperCase =  mappingFactory.createGetterMapping(
                PersistentObject.class.getMethod("getId", null), "ID", true);
        assertTrue("should contain getId method mapping"         ,
                mapping.getGetterMappings().contains(fieldMappingLowerCase) ||
                mapping.getGetterMappings().contains(fieldMappingUpperCase) );

        fieldMappingLowerCase = mappingFactory.createGetterMapping(
                PersistentObject.class.getMethod("getName", null), "name", true);
        fieldMappingUpperCase = mappingFactory.createGetterMapping(
                PersistentObject.class.getMethod("getName", null), "NAME", true);
        assertTrue("should contain getName method mapping",
                mapping.getGetterMappings().contains(fieldMappingLowerCase) ||
                mapping.getGetterMappings().contains(fieldMappingUpperCase));

        IGetterMapping fieldMapping = mappingFactory.createGetterMapping(
                PersistentObject.class.getMethod("getObjectValue", null), "objectValue", true);
        fieldMappingUpperCase = mappingFactory.createGetterMapping(
                PersistentObject.class.getMethod("getObjectValue", null), "OBJECTVALUE", true);
        fieldMappingLowerCase = mappingFactory.createGetterMapping(
                PersistentObject.class.getMethod("getObjectValue", null), "objectvalue", true);
        assertTrue("should contain getObjectValue method mapping",
                mapping.getGetterMappings().contains(fieldMapping) ||
                mapping.getGetterMappings().contains(fieldMappingLowerCase) ||
                mapping.getGetterMappings().contains(fieldMappingUpperCase));
    }


    protected void subtestTableNameObjectClassPrimaryKey(IObjectMapping mapping) throws Exception{
        assertEquals("wrong table name"     , "persistent_object"   , mapping.getTableName().toLowerCase());
        assertEquals("wrong object class"   , PersistentObject.class, mapping.getObjectClass());
        assertEquals("wrong primary key"    , "id"                  , mapping.getPrimaryKey().getColumn().toLowerCase());
    }


    private void subtestObjectClassForMapGettersToTable() throws Exception{
        //test object class is used if one is provided, and there is none in the object mapping
        IObjectMapping mapping = this.mapper.mapGettersToTable(PersistentObject.class, null, this.connection, null, null);
        assertEquals("should have PersistentObject.class", PersistentObject.class, mapping.getObjectClass());


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
        assertEquals("should be equal classes", PersistentObject.class, mapping.getObjectClass());

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
        assertSame("provided table name not used", tableName, mapping.getTableName());

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
        assertEquals("should contain table name from object mapping", "persistent_object", mapping.getTableName());


        //test table name equality (same name in parameter and object mapping)
        tableName   = new String("" + "persistent_object");
        assertNotSame("should not be same, only equal", mapping.getTableName(), tableName);
        mapping     = mapping = this.mapper.mapGettersToTable(PersistentObject.class, mapping, this.connection, null, tableName);
    }

    public void testMapSettersToTable() throws Exception{
        IObjectMapping mapping = this.mapper.mapSettersToTable(PersistentObject.class, null, this.connection, null, null);
        assertNotNull("no mapping returned", mapping);

        //test object mapping instance is reused if provided.
        mapping = new ObjectMapping();
        IObjectMapping mapping2 = this.mapper.mapSettersToTable(PersistentObject.class, mapping, this.connection, null, null);
        assertSame("should be same object mapping", mapping, mapping2);

        subtestTableNameObjectClassPrimaryKey(mapping);

        subtestObjectClassForMapSettersToTable();
        subtestTableNameForMapSettersToTable();

        //Test all setter method mappings.
        subtestSetterMappings(mapping);
    }


    private void subtestSetterMappings(IObjectMapping mapping) throws NoSuchMethodException {
        assertEquals("wrong number of setter method mappings", 4, mapping.getSetterMappings().size());

        ISetterMapping fieldMappingLowerCase =  mappingFactory.createSetterMapping(
                PersistentObject.class.getMethod("setId", new Class[]{long.class}), "id", true);

        ISetterMapping fieldMappingUpperCase =  mappingFactory.createSetterMapping(
                PersistentObject.class.getMethod("setId", new Class[]{long.class}), "ID", true);


        assertTrue("should contain setId method mapping"         ,
                mapping.getSetterMappings().contains(fieldMappingUpperCase) ||
                mapping.getSetterMappings().contains(fieldMappingLowerCase));

        fieldMappingLowerCase = mappingFactory.createSetterMapping(
                PersistentObject.class.getMethod("setName", new Class[]{String.class}), "name", true);
        fieldMappingUpperCase = mappingFactory.createSetterMapping(
                PersistentObject.class.getMethod("setName", new Class[]{String.class}), "NAME", true);

        assertTrue("should contain setName method mapping",
                mapping.getSetterMappings().contains(fieldMappingUpperCase) ||
                mapping.getSetterMappings().contains(fieldMappingLowerCase));
    }


    private void subtestObjectClassForMapSettersToTable() throws Exception{
        //test object class is used if one is provided, and there is none in the object mapping
        IObjectMapping mapping = this.mapper.mapSettersToTable(PersistentObject.class, null, this.connection, null, null);
        assertEquals("should have PersistentObject.class", PersistentObject.class, mapping.getObjectClass());


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
        assertEquals("should be equal classes", PersistentObject.class, mapping.getObjectClass());

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
        assertSame("provided table name not used", tableName, mapping.getTableName());

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
        assertEquals("should contain table name from object mapping", "persistent_object", mapping.getTableName());


        //test table name equality (same name in parameter and object mapping)
        tableName   = new String("" + "persistent_object");
        assertNotSame("should not be same, only equal", mapping.getTableName(), tableName);
        mapping     = mapping = this.mapper.mapSettersToTable(PersistentObject.class, mapping, this.connection, null, tableName);
    }





}
