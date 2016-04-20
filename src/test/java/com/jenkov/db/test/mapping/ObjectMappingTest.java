/**
 * User: Administrator
 */
package com.jenkov.db.test.mapping;

import com.jenkov.db.impl.mapping.Key;
import com.jenkov.db.impl.mapping.ObjectMapping;
import com.jenkov.db.impl.mapping.ObjectMappingFactory;
import com.jenkov.db.impl.mapping.KeyValue;
import com.jenkov.db.impl.mapping.method.GetterMapping;
import com.jenkov.db.impl.mapping.method.MethodMapping;
import com.jenkov.db.impl.mapping.method.SetterMapping;
import com.jenkov.db.itf.mapping.*;
import com.jenkov.db.itf.PersistenceException;
import com.jenkov.db.test.objects.PersistentObject;
import junit.framework.TestCase;

import java.lang.reflect.Method;
import java.sql.Connection;

public class ObjectMappingTest extends TestCase{

    protected Connection         connection = null;
    protected IObjectMapping     mapping    = null;

    public ObjectMappingTest(String test){
        super(test);
    }

    public void setUp() throws Exception {
        //this.connection = Environment.getConnection();
        //this.method = new ObjectMapper().mapToTable(PersistentObject.class, this.connection, (String) null);
        this.mapping = new ObjectMapping();
    }

    public void testGetTableName() throws Exception {
        assertNull("should default to null", this.mapping.getTableName());

        this.mapping.setTableName("test table");
        assertEquals("should be equal", "test table", this.mapping.getTableName());

        this.mapping.setTableName(null);
        assertNull("should be null", this.mapping.getTableName());
    }


    public void testGetObjectClass() throws Exception {
        assertNull("should default to null", this.mapping.getObjectClass());

        this.mapping.setObjectClass(ObjectMapping.class);
        assertEquals("should be equal", ObjectMapping.class, this.mapping.getObjectClass());

        this.mapping.setObjectClass(null);
        assertNull("should be null", this.mapping.getObjectClass());
    }

    public void testGetKeyValueForObject() throws PersistenceException {
        IObjectMappingFactory factory = new ObjectMappingFactory();
        IObjectMapping        mapping = factory.createObjectMapping(PersistentObject.class, "persistent_object");

        IKey key = new Key();
        key.addColumn("id");
        mapping.setPrimaryKey(key);
        mapping.addGetterMapping(factory.createGetterMapping(PersistentObject.class, "getId", "id"));

        PersistentObject object = new PersistentObject();
        object.setId(33);
        object.setName("someName");

        IKeyValue keyValue = mapping.getPrimaryKeyValueForObject(object, new KeyValue());

        assertEquals(new Long(33), keyValue.getColumnValue("id"));

        object.setId(34);
        key.addColumn("name");
        mapping.addGetterMapping(factory.createGetterMapping(PersistentObject.class, "getName", "name"));
        keyValue = mapping.getPrimaryKeyValueForObject(object, keyValue);
        assertEquals(new Long(34), keyValue.getColumnValue("id"));
        assertEquals("someName", keyValue.getColumnValue("name"));
    }


    public void testGetSetPrimaryKeyMapping() throws Exception{
        IKey key = new Key();
        key.setTable("table");

        assertNotSame("should not be same", key, mapping.getPrimaryKey());

        this.mapping.setPrimaryKey(key);
        assertSame("should be same", key, mapping.getPrimaryKey());

        key = new Key();
        key.setTable("table");
        assertNotSame("should not be same", key, mapping.getPrimaryKey());


    }


    public void testAddGetterMapping() throws Exception {
        assertEquals("should default to 0", 0, this.mapping.getGetterMappings().size());

        IGetterMapping fieldMapping1 = createGetterMapping("test1",
                MethodMapping.class.getMethod("getColumnName", null));

        this.mapping.addGetterMapping(fieldMapping1);
        assertEquals("should contain 1 method method", 1, this.mapping.getGetterMappings().size());
        assertTrue("should contain getter method method",
                this.mapping.getGetterMappings().contains(fieldMapping1));


        IGetterMapping fieldMapping2 = createGetterMapping("test2",
                com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("getObjectMethod", null));

        this.mapping.addGetterMapping(fieldMapping2);
        assertEquals("should contain 2 method mappings", 2, this.mapping.getGetterMappings().size());
        assertTrue("should contain getter method method",
                this.mapping.getGetterMappings().contains(fieldMapping2));
    }


    private IGetterMapping createGetterMapping(String dbFieldName, Method method) throws NoSuchMethodException {
        IGetterMapping fieldMapping1 = new GetterMapping();
        fieldMapping1.setColumnName(dbFieldName);
        fieldMapping1.setObjectMethod(method);
        return fieldMapping1;
    }


    public void testAddSetterMapping() throws Exception {
        assertEquals("should default to 0", 0, this.mapping.getSetterMappings().size());

        ISetterMapping fieldMapping1 = createSetterMapping("test1",
                com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("setColumnName", new Class[]{String.class}) );

        this.mapping.addSetterMapping(fieldMapping1);
        assertEquals("should contain 1 method method", 1, this.mapping.getSetterMappings().size());
        assertTrue("should contain setter method method 1",
                this.mapping.getSetterMappings().contains(fieldMapping1));


        ISetterMapping fieldMapping2 = createSetterMapping("test2",
                com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("setObjectMethod", new Class[]{Method.class}) );

        this.mapping.addSetterMapping(fieldMapping2);
        assertEquals("should contain 2 method mappings", 2, this.mapping.getSetterMappings().size());
        assertTrue("should contain setter method method 2",
                this.mapping.getSetterMappings().contains(fieldMapping2));
    }

    private ISetterMapping createSetterMapping(String dbFieldName, Method method) {
        ISetterMapping fieldMapping = new SetterMapping();
        fieldMapping.setColumnName(dbFieldName);
        fieldMapping.setObjectMethod(method);
        return fieldMapping;
    }


    public void testRemoveGetterMappingByColumnName() throws Exception {
        IGetterMapping fieldMapping1 = createGetterMapping("test1",
                com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("getColumnName", null));

        this.mapping.addGetterMapping(fieldMapping1);
        assertEquals("should contain 1 method method", 1, this.mapping.getGetterMappings().size());

        this.mapping.removeGetterMapping("no method method");
        assertEquals("should still contain 1 method method", 1, this.mapping.getGetterMappings().size());

        this.mapping.removeGetterMapping("test1");
        assertEquals("should contain 0 method mappings now", 0, this.mapping.getGetterMappings().size());
    }


    public void testRemoveGetterMappingByMethod() throws Exception {
        IGetterMapping fieldMapping1 = createGetterMapping("test1",
                com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("getColumnName", null));

        this.mapping.addGetterMapping(fieldMapping1);
        assertEquals("should contain 1 method method", 1, this.mapping.getGetterMappings().size());

        this.mapping.removeGetterMapping(com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("getObjectMethod", null));
        assertEquals("should still contain 1 method method", 1, this.mapping.getGetterMappings().size());

        this.mapping.removeGetterMapping(com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("getColumnName", null));
        assertEquals("should contain 0 method mappings now", 0, this.mapping.getGetterMappings().size());
    }


    public void testRemoveSetterMappingByColumnName() throws Exception {
        ISetterMapping fieldMapping1 = createSetterMapping("test1",
                com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("setColumnName", new Class[]{String.class}));

        this.mapping.addSetterMapping(fieldMapping1);
        assertEquals("should contain 1 method method", 1, this.mapping.getSetterMappings().size());

        this.mapping.removeSetterMapping("no method method");
        assertEquals("should still contain 1 method method", 1, this.mapping.getSetterMappings().size());

        this.mapping.removeSetterMapping("test1");
        assertEquals("should contain 0 method mappings now", 0, this.mapping.getSetterMappings().size());
    }

    public void testRemoveSetterMappingByMethod() throws Exception {
        ISetterMapping fieldMapping1 = createSetterMapping("test1",
                com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("setColumnName", new Class[]{String.class}));

        ISetterMapping fieldMapping2 = createSetterMapping("test2",
                com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("setColumnName", new Class[]{String.class}));

        this.mapping.addSetterMapping(fieldMapping1);
        assertEquals("should contain 1 method method", 1, this.mapping.getSetterMappings().size());

        this.mapping.removeSetterMapping(com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("setObjectMethod", new Class[]{Method.class}));
        assertEquals("should still contain 1 method method", 1, this.mapping.getSetterMappings().size());

        this.mapping.removeSetterMapping(com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("setColumnName", new Class[]{String.class}));
        assertEquals("should contain 0 method mappings now", 0, this.mapping.getSetterMappings().size());
    }

    public void testGetGetterMappings() throws Exception {
        IGetterMapping fieldMapping1 = createGetterMapping("test1",
                com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("getColumnName", null));

        IGetterMapping fieldMapping2 = createGetterMapping("test2",
                com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("getObjectMethod", null));

        this.mapping.addGetterMapping(fieldMapping1);
        this.mapping.addGetterMapping(fieldMapping2);
        assertEquals("should contain 2 method mappings", 2, this.mapping.getGetterMappings().size());

        this.mapping.removeGetterMapping(com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("getObjectMethod", null));
        assertEquals("should contain 1 method method", 1, this.mapping.getGetterMappings().size());

        this.mapping.removeGetterMapping(com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("getColumnName", null));
        assertEquals("should contain 0 method mappings now", 0, this.mapping.getGetterMappings().size());
    }

    public void testGetSetterMappings() throws Exception {
        ISetterMapping fieldMapping1 = createSetterMapping("test1",
                com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("setColumnName", new Class[]{String.class}));

        ISetterMapping fieldMapping2 = createSetterMapping("test2",
                com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("setObjectMethod", new Class[]{Method.class}));

        this.mapping.addSetterMapping(fieldMapping1);
        this.mapping.addSetterMapping(fieldMapping2);
        assertEquals("should contain 2 method mappings", 2, this.mapping.getSetterMappings().size());

        this.mapping.removeSetterMapping("test1");
        assertEquals("should contain 1 method method", 1, this.mapping.getSetterMappings().size());

        this.mapping.removeSetterMapping("test2");
        assertEquals("should contain 0 method mappings now", 0, this.mapping.getSetterMappings().size());
    }

    public void testGetGetterMappingByColumnName() throws Exception {
        IGetterMapping fieldMapping1 = createGetterMapping("test1",
                com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("getColumnName", null));

        IGetterMapping fieldMapping2 = createGetterMapping("test2",
                com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("getObjectMethod", null));

        this.mapping.addGetterMapping(fieldMapping1);
        assertEquals("should contain method method 1", fieldMapping1, this.mapping.getGetterMapping("test1"));
        assertNull("should not contain method method 2", this.mapping.getGetterMapping("test2"));

        this.mapping.addGetterMapping(fieldMapping2);
        assertEquals("should contain method method 1", fieldMapping1, this.mapping.getGetterMapping("test1"));
        assertEquals("should contain method method 2", fieldMapping2, this.mapping.getGetterMapping("test2"));

        this.mapping.removeGetterMapping("test1");
        assertEquals("should contain method method 2", fieldMapping2, this.mapping.getGetterMapping("test2"));
        assertNull("should not contain method method 1", this.mapping.getGetterMapping("test1"));
    }

    public void testGetGetterMappingByMethod() throws Exception {
        IGetterMapping fieldMapping1 = createGetterMapping("test1",
                com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("getColumnName", null));

        IGetterMapping fieldMapping2 = createGetterMapping("test2",
                com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("getObjectMethod", null));

        this.mapping.addGetterMapping(fieldMapping1);
        assertEquals("should contain method method 1", fieldMapping1,
                this.mapping.getGetterMapping(com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("getColumnName", null)));
        assertNull("should not contain method method 2",
                this.mapping.getGetterMapping(com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("getObjectMethod", null)));

        this.mapping.addGetterMapping(fieldMapping2);
        assertEquals("should contain method method 1", fieldMapping1,
                this.mapping.getGetterMapping(com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("getColumnName", null)));
        assertEquals("should contain method method 2", fieldMapping2,
                this.mapping.getGetterMapping(com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("getObjectMethod", null)));

        this.mapping.removeGetterMapping("test1");
        assertEquals("should contain method method 2", fieldMapping2,
                this.mapping.getGetterMapping(com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("getObjectMethod", null)));
        assertNull("should not contain method method 1",
                 this.mapping.getGetterMapping(com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("getColumnName", null)));
    }

    public void testGetSetterMappingByColumnName() throws Exception {
        ISetterMapping fieldMapping1 = createSetterMapping("test1",
                com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("setColumnName", new Class[] {String.class}));

        ISetterMapping fieldMapping2 = createSetterMapping("test2",
                com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("setObjectMethod", new Class[]{Method.class}));

        this.mapping.addSetterMapping(fieldMapping1);
        assertEquals("should contain method method 1", fieldMapping1, this.mapping.getSetterMapping("test1"));
        assertNull("should not contain method method 2", this.mapping.getSetterMapping("test2"));

        this.mapping.addSetterMapping(fieldMapping2);
        assertEquals("should contain method method 1", fieldMapping1, this.mapping.getSetterMapping("test1"));
        assertEquals("should contain method method 2", fieldMapping2, this.mapping.getSetterMapping("test2"));

        this.mapping.removeSetterMapping("test1");
        assertEquals("should contain method method 2", fieldMapping2, this.mapping.getSetterMapping("test2"));
        assertNull("should not contain method method 1", this.mapping.getSetterMapping("test1"));
    }

    public void testGetSetterMappingByMethod() throws Exception {
        ISetterMapping fieldMapping1 = createSetterMapping("test1",
                com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("setColumnName", new Class[]{String.class}));

        ISetterMapping fieldMapping2 = createSetterMapping("test2",
                com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("setObjectMethod", new Class[]{Method.class}));

        this.mapping.addSetterMapping(fieldMapping1);
        assertEquals("should contain method method 1", fieldMapping1,
                this.mapping.getSetterMapping(com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("setColumnName", new Class[]{String.class})));
        assertNull("should not contain method method 2",
                this.mapping.getSetterMapping(com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("getObjectMethod", null)));

        this.mapping.addSetterMapping(fieldMapping2);
        assertEquals("should contain method method 1", fieldMapping1,
                this.mapping.getSetterMapping(com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("setColumnName", new Class[]{String.class})));
        assertEquals("should contain method method 2", fieldMapping2,
                this.mapping.getSetterMapping(com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("setObjectMethod", new Class[]{Method.class})));

        this.mapping.removeSetterMapping("test1");
        assertNull("should not contain method method 1",
                 this.mapping.getSetterMapping(com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("setColumnName", new Class[]{String.class})));
        assertEquals("should contain method method 2", fieldMapping2,
                this.mapping.getSetterMapping(com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("setObjectMethod", new Class[]{Method.class})));
    }


    public void testEquals() throws Exception{

        IObjectMapping mapping1 = new ObjectMapping();
        IObjectMapping mapping2 = new ObjectMapping();

        //Testing default
        assertEquals("should default to equal", mapping1, mapping2);

        //Testing table name
        mapping1.setTableName("testTable");
        assertFalse("should not be equal, mapping1 has table name", mapping1.equals(mapping2));

        mapping1.setTableName(null);
        mapping2.setTableName("testTable");
        assertFalse("should not be equal, mapping2 has table name", mapping1.equals(mapping2));

        mapping1.setTableName("testTable");
        assertEquals("should be to equal with table names", mapping1, mapping2);

        //Testing object class
        mapping1.setObjectClass(com.jenkov.db.impl.mapping.method.MethodMapping.class);
        assertFalse("should not be equal, mapping1 has object class", mapping1.equals(mapping2));

        mapping1.setObjectClass(null);
        mapping2.setObjectClass(com.jenkov.db.impl.mapping.method.MethodMapping.class);
        assertFalse("should not be equal, mapping2 has object class", mapping1.equals(mapping2));

        mapping1.setObjectClass(com.jenkov.db.impl.mapping.method.MethodMapping.class);
        assertEquals("should be to equal with object classes", mapping1, mapping2);

        //Testing primary key method mappings
        mapping1.getPrimaryKey().addColumn("test1");
        assertFalse("should not be equal, mapping1 has primary key method name", mapping1.equals(mapping2));

        mapping1.getPrimaryKey().removeColumn("test1");
        mapping2.getPrimaryKey().addColumn("test1");
        assertFalse("should not be equal, mapping2 has primary key method name", mapping1.equals(mapping2));

        mapping1.getPrimaryKey().addColumn("test1");
        assertEquals("should be to equal with primary key method mappings", mapping1, mapping2);

        //test method mappings are equal
        IGetterMapping fieldMapping1get = createGetterMapping("test1",
                com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("getColumnName", null));

        IGetterMapping fieldMapping2get = createGetterMapping("test2",
                com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("getObjectMethod", null));


        ISetterMapping fieldMapping1set = createSetterMapping("test1",
                com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("setColumnName", new Class[]{String.class}));

        ISetterMapping fieldMapping2set = createSetterMapping("test2",
                com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("setObjectMethod", new Class[]{Method.class}));

        //getter method mappings
        mapping1.addGetterMapping(fieldMapping1get);
        mapping1.addGetterMapping(fieldMapping2get);
        assertFalse("should not be equal, mapping1 has getter method mappings", mapping1.equals(mapping2));

        mapping1.removeGetterMapping(fieldMapping1get.getColumnName());
        mapping1.removeGetterMapping(fieldMapping2get.getColumnName());
        mapping2.addGetterMapping(fieldMapping1get);
        mapping2.addGetterMapping(fieldMapping2get);
        assertFalse("should not be equal, mapping2 has getter method mappings", mapping1.equals(mapping2));

        mapping1.addGetterMapping(fieldMapping1get);
        mapping1.addGetterMapping(fieldMapping2get);
        assertEquals("should be to equal with getter method mappings", mapping1, mapping2);

        //setter method mappings
        mapping1.addSetterMapping(fieldMapping1set);
        mapping1.addSetterMapping(fieldMapping2set);
        assertFalse("should not be equal, mapping1 has setter method mappings", mapping1.equals(mapping2));

        mapping1.removeSetterMapping(fieldMapping1get.getColumnName());
        mapping1.removeSetterMapping(fieldMapping2get.getColumnName());
        mapping2.addSetterMapping(fieldMapping1set);
        mapping2.addSetterMapping(fieldMapping2set);
        assertFalse("should not be equal, mapping2 has setter method mappings", mapping1.equals(mapping2));

        mapping1.addSetterMapping(fieldMapping1set);
        mapping1.addSetterMapping(fieldMapping2set);
        assertEquals("should be to equal with setter method mappings", mapping1, mapping2);
    }

    public void testToString() throws Exception{
        ObjectMapping mapping = new ObjectMapping();
        assertEquals("Class: null\nTable: null\n-------------------\n", mapping.toString());

        mapping.setObjectClass(PersistentObject.class);
        assertEquals("Class: com.jenkov.db.test.objects.PersistentObject\n" +
                "Table: null\n-------------------\n", mapping.toString());

        mapping.setTableName("someTable");
        assertEquals("Class: com.jenkov.db.test.objects.PersistentObject\n" +
                "Table: someTable\n-------------------\n", mapping.toString());

        IGetterMapping getterMapping = new GetterMapping();
        getterMapping.setColumnName("id");
        getterMapping.setObjectMethod( getClass().getMethod("toString", null));
        mapping.addGetterMapping(getterMapping);

        assertEquals("Class: com.jenkov.db.test.objects.PersistentObject\n" +
                "Table: someTable\n-------------------\n" +
                "toString --> id\n", mapping.toString());

        ISetterMapping setterMapping = new SetterMapping();
        setterMapping.setColumnName("id");
        setterMapping.setObjectMethod( getClass().getMethod("toString", null));
        mapping.addSetterMapping(setterMapping);

        assertEquals("Class: com.jenkov.db.test.objects.PersistentObject\n" +
                "Table: someTable\n-------------------\n" +
                "toString --> id\n" +
                "toString <-- id\n", mapping.toString());


        //fail("not finished");
    }

    public void testHashCode(){
        ObjectMapping mapping = new ObjectMapping();
        assertTrue(mapping.hashCode() > 0);

        mapping.setObjectClass(PersistentObject.class);
        assertEquals(PersistentObject.class.hashCode(), mapping.hashCode());
    }

}
