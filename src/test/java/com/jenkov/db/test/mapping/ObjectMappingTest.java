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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

public class ObjectMappingTest {

    protected Connection         connection = null;
    protected IObjectMapping     mapping    = null;


    @BeforeEach
    public void setUp() throws Exception {
        //this.connection = Environment.getConnection();
        //this.method = new ObjectMapper().mapToTable(PersistentObject.class, this.connection, (String) null);
        this.mapping = new ObjectMapping();
    }

    @Test
    public void testGetTableName() throws Exception {
        assertNull(this.mapping.getTableName(), "should default to null");

        this.mapping.setTableName("test table");
        assertEquals("test table", this.mapping.getTableName(), "should be equal");

        this.mapping.setTableName(null);
        assertNull(this.mapping.getTableName(), "should be null");
    }

    @Test
    public void testGetObjectClass() throws Exception {
        assertNull(this.mapping.getObjectClass(), "should default to null");

        this.mapping.setObjectClass(ObjectMapping.class);
        assertEquals(ObjectMapping.class, this.mapping.getObjectClass(), "should be equal");

        this.mapping.setObjectClass(null);
        assertNull(this.mapping.getObjectClass(), "should be null");
    }

    @Test
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

    @Test
    public void testGetSetPrimaryKeyMapping() throws Exception{
        IKey key = new Key();
        key.setTable("table");

        assertNotSame(key, mapping.getPrimaryKey(), "should not be same");

        this.mapping.setPrimaryKey(key);
        assertSame(key, mapping.getPrimaryKey(), "should be same");

        key = new Key();
        key.setTable("table");
        assertNotSame(key, mapping.getPrimaryKey(), "should not be same");


    }

    @Test
    public void testAddGetterMapping() throws Exception {
        assertEquals(0, this.mapping.getGetterMappings().size(), "should default to 0");

        IGetterMapping fieldMapping1 = createGetterMapping("test1",
                MethodMapping.class.getMethod("getColumnName", null));

        this.mapping.addGetterMapping(fieldMapping1);
        assertEquals(1, this.mapping.getGetterMappings().size(), "should contain 1 method method");
        assertTrue(this.mapping.getGetterMappings().contains(fieldMapping1),
                "should contain getter method method");


        IGetterMapping fieldMapping2 = createGetterMapping("test2",
                com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("getObjectMethod", null));

        this.mapping.addGetterMapping(fieldMapping2);
        assertEquals(2, this.mapping.getGetterMappings().size(), "should contain 2 method mappings");
        assertTrue( this.mapping.getGetterMappings().contains(fieldMapping2),
                "should contain getter method method"
        );
    }


    private IGetterMapping createGetterMapping(String dbFieldName, Method method) throws NoSuchMethodException {
        IGetterMapping fieldMapping1 = new GetterMapping();
        fieldMapping1.setColumnName(dbFieldName);
        fieldMapping1.setObjectMethod(method);
        return fieldMapping1;
    }


    @Test
    public void testAddSetterMapping() throws Exception {
        assertEquals(0, this.mapping.getSetterMappings().size(), "should default to 0");

        ISetterMapping fieldMapping1 = createSetterMapping("test1",
                com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("setColumnName", new Class[]{String.class}) );

        this.mapping.addSetterMapping(fieldMapping1);
        assertEquals(1, this.mapping.getSetterMappings().size(), "should contain 1 method method");
        assertTrue(this.mapping.getSetterMappings().contains(fieldMapping1),
                "should contain setter method method 1");


        ISetterMapping fieldMapping2 = createSetterMapping("test2",
                com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("setObjectMethod", new Class[]{Method.class}) );

        this.mapping.addSetterMapping(fieldMapping2);
        assertEquals(2, this.mapping.getSetterMappings().size(), "should contain 2 method mappings");
        assertTrue(this.mapping.getSetterMappings().contains(fieldMapping2),
                "should contain setter method method 2");
    }

    private ISetterMapping createSetterMapping(String dbFieldName, Method method) {
        ISetterMapping fieldMapping = new SetterMapping();
        fieldMapping.setColumnName(dbFieldName);
        fieldMapping.setObjectMethod(method);
        return fieldMapping;
    }

    @Test
    public void testRemoveGetterMappingByColumnName() throws Exception {
        IGetterMapping fieldMapping1 = createGetterMapping("test1",
                com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("getColumnName", null));

        this.mapping.addGetterMapping(fieldMapping1);
        assertEquals(1, this.mapping.getGetterMappings().size(), "should contain 1 method method");

        this.mapping.removeGetterMapping("no method method");
        assertEquals(1, this.mapping.getGetterMappings().size(), "should still contain 1 method method");

        this.mapping.removeGetterMapping("test1");
        assertEquals(0, this.mapping.getGetterMappings().size(), "should contain 0 method mappings now");
    }

    @Test
    public void testRemoveGetterMappingByMethod() throws Exception {
        IGetterMapping fieldMapping1 = createGetterMapping("test1",
                com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("getColumnName", null));

        this.mapping.addGetterMapping(fieldMapping1);
        assertEquals(1, this.mapping.getGetterMappings().size(), "should contain 1 method method");

        this.mapping.removeGetterMapping(com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("getObjectMethod", null));
        assertEquals(1, this.mapping.getGetterMappings().size(), "should still contain 1 method method");

        this.mapping.removeGetterMapping(com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("getColumnName", null));
        assertEquals(0, this.mapping.getGetterMappings().size(), "should contain 0 method mappings now");
    }

    @Test
    public void testRemoveSetterMappingByColumnName() throws Exception {
        ISetterMapping fieldMapping1 = createSetterMapping("test1",
                com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("setColumnName", new Class[]{String.class}));

        this.mapping.addSetterMapping(fieldMapping1);
        assertEquals(1, this.mapping.getSetterMappings().size(), "should contain 1 method method");

        this.mapping.removeSetterMapping("no method method");
        assertEquals(1, this.mapping.getSetterMappings().size(), "should still contain 1 method method");

        this.mapping.removeSetterMapping("test1");
        assertEquals(0, this.mapping.getSetterMappings().size(), "should contain 0 method mappings now");
    }

    @Test
    public void testRemoveSetterMappingByMethod() throws Exception {
        ISetterMapping fieldMapping1 = createSetterMapping("test1",
                com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("setColumnName", new Class[]{String.class}));

        ISetterMapping fieldMapping2 = createSetterMapping("test2",
                com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("setColumnName", new Class[]{String.class}));

        this.mapping.addSetterMapping(fieldMapping1);
        assertEquals(1, this.mapping.getSetterMappings().size(), "should contain 1 method method");

        this.mapping.removeSetterMapping(com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("setObjectMethod", new Class[]{Method.class}));
        assertEquals(1, this.mapping.getSetterMappings().size(), "should still contain 1 method method");

        this.mapping.removeSetterMapping(com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("setColumnName", new Class[]{String.class}));
        assertEquals(0, this.mapping.getSetterMappings().size(), "should contain 0 method mappings now");
    }

    @Test
    public void testGetGetterMappings() throws Exception {
        IGetterMapping fieldMapping1 = createGetterMapping("test1",
                com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("getColumnName", null));

        IGetterMapping fieldMapping2 = createGetterMapping("test2",
                com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("getObjectMethod", null));

        this.mapping.addGetterMapping(fieldMapping1);
        this.mapping.addGetterMapping(fieldMapping2);
        assertEquals(2, this.mapping.getGetterMappings().size(), "should contain 2 method mappings");

        this.mapping.removeGetterMapping(com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("getObjectMethod", null));
        assertEquals(1, this.mapping.getGetterMappings().size(), "should contain 1 method method");

        this.mapping.removeGetterMapping(com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("getColumnName", null));
        assertEquals(0, this.mapping.getGetterMappings().size(), "should contain 0 method mappings now");
    }

    @Test
    public void testGetSetterMappings() throws Exception {
        ISetterMapping fieldMapping1 = createSetterMapping("test1",
                com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("setColumnName", new Class[]{String.class}));

        ISetterMapping fieldMapping2 = createSetterMapping("test2",
                com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("setObjectMethod", new Class[]{Method.class}));

        this.mapping.addSetterMapping(fieldMapping1);
        this.mapping.addSetterMapping(fieldMapping2);
        assertEquals(2, this.mapping.getSetterMappings().size(), "should contain 2 method mappings");

        this.mapping.removeSetterMapping("test1");
        assertEquals(1, this.mapping.getSetterMappings().size(), "should contain 1 method method");

        this.mapping.removeSetterMapping("test2");
        assertEquals(0, this.mapping.getSetterMappings().size(), "should contain 0 method mappings now");
    }

    @Test
    public void testGetGetterMappingByColumnName() throws Exception {
        IGetterMapping fieldMapping1 = createGetterMapping("test1",
                com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("getColumnName", null));

        IGetterMapping fieldMapping2 = createGetterMapping("test2",
                com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("getObjectMethod", null));

        this.mapping.addGetterMapping(fieldMapping1);
        assertEquals(fieldMapping1, this.mapping.getGetterMapping("test1"), "should contain method method 1");
        assertNull(this.mapping.getGetterMapping("test2"), "should not contain method method 2");

        this.mapping.addGetterMapping(fieldMapping2);
        assertEquals(fieldMapping1, this.mapping.getGetterMapping("test1"), "should contain method method 1");
        assertEquals(fieldMapping2, this.mapping.getGetterMapping("test2"), "should contain method method 2");

        this.mapping.removeGetterMapping("test1");
        assertEquals(fieldMapping2, this.mapping.getGetterMapping("test2"), "should contain method method 2");
        assertNull(this.mapping.getGetterMapping("test1"), "should not contain method method 1");
    }

    @Test
    public void testGetGetterMappingByMethod() throws Exception {
        IGetterMapping fieldMapping1 = createGetterMapping("test1",
                com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("getColumnName", null));

        IGetterMapping fieldMapping2 = createGetterMapping("test2",
                com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("getObjectMethod", null));

        this.mapping.addGetterMapping(fieldMapping1);
        assertEquals(fieldMapping1,
                this.mapping.getGetterMapping(com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("getColumnName", null)),
                "should contain method method 1"
        );
        assertNull(
                this.mapping.getGetterMapping(com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("getObjectMethod", null)),
                "should not contain method method 2");

        this.mapping.addGetterMapping(fieldMapping2);
        assertEquals(fieldMapping1,
                this.mapping.getGetterMapping(com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("getColumnName", null)),
                "should contain method method 1"
        );
        assertEquals(fieldMapping2,
                this.mapping.getGetterMapping(com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("getObjectMethod", null)),
                "should contain method method 2");

        this.mapping.removeGetterMapping("test1");
        assertEquals(fieldMapping2,
                this.mapping.getGetterMapping(com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("getObjectMethod", null)),
                "should contain method method 2");
        assertNull(
                this.mapping.getGetterMapping(com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("getColumnName", null)),
                "should not contain method method 1");
    }

    @Test
    public void testGetSetterMappingByColumnName() throws Exception {
        ISetterMapping fieldMapping1 = createSetterMapping("test1",
                com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("setColumnName", new Class[] {String.class}));

        ISetterMapping fieldMapping2 = createSetterMapping("test2",
                com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("setObjectMethod", new Class[]{Method.class}));

        this.mapping.addSetterMapping(fieldMapping1);
        assertEquals(fieldMapping1, this.mapping.getSetterMapping("test1"), "should contain method method 1");
        assertNull(this.mapping.getSetterMapping("test2"), "should not contain method method 2");

        this.mapping.addSetterMapping(fieldMapping2);
        assertEquals(fieldMapping1, this.mapping.getSetterMapping("test1"), "should contain method method 1");
        assertEquals(fieldMapping2, this.mapping.getSetterMapping("test2"), "should contain method method 2");

        this.mapping.removeSetterMapping("test1");
        assertEquals(fieldMapping2, this.mapping.getSetterMapping("test2"), "should contain method method 2");
        assertNull(this.mapping.getSetterMapping("test1"), "should not contain method method 1");
    }

    @Test
    public void testGetSetterMappingByMethod() throws Exception {
        ISetterMapping fieldMapping1 = createSetterMapping("test1",
                com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("setColumnName", new Class[]{String.class}));

        ISetterMapping fieldMapping2 = createSetterMapping("test2",
                com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("setObjectMethod", new Class[]{Method.class}));

        this.mapping.addSetterMapping(fieldMapping1);
        assertEquals(fieldMapping1,
                this.mapping.getSetterMapping(com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("setColumnName", new Class[]{String.class})),
                "should contain method method 1");
        assertNull(
                this.mapping.getSetterMapping(com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("getObjectMethod", null)),
                "should not contain method method 2");

        this.mapping.addSetterMapping(fieldMapping2);
        assertEquals(fieldMapping1,
                this.mapping.getSetterMapping(com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("setColumnName", new Class[]{String.class})),
                "should contain method method 1");
        assertEquals(fieldMapping2,
                this.mapping.getSetterMapping(com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("setObjectMethod", new Class[]{Method.class})),
                "should contain method method 2");

        this.mapping.removeSetterMapping("test1");
        assertNull(
                 this.mapping.getSetterMapping(com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("setColumnName", new Class[]{String.class})),
                "should not contain method method 1");
        assertEquals(fieldMapping2,
                this.mapping.getSetterMapping(com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("setObjectMethod", new Class[]{Method.class})),
                "should contain method method 2");
    }

    @Test
    public void testEquals() throws Exception{

        IObjectMapping mapping1 = new ObjectMapping();
        IObjectMapping mapping2 = new ObjectMapping();

        //Testing default
        assertEquals(mapping1, mapping2, "should default to equal");

        //Testing table name
        mapping1.setTableName("testTable");
        assertFalse(mapping1.equals(mapping2), "should not be equal, mapping1 has table name");

        mapping1.setTableName(null);
        mapping2.setTableName("testTable");
        assertFalse(mapping1.equals(mapping2), "should not be equal, mapping2 has table name");

        mapping1.setTableName("testTable");
        assertEquals(mapping1, mapping2, "should be to equal with table names");

        //Testing object class
        mapping1.setObjectClass(com.jenkov.db.impl.mapping.method.MethodMapping.class);
        assertFalse(mapping1.equals(mapping2), "should not be equal, mapping1 has object class");

        mapping1.setObjectClass(null);
        mapping2.setObjectClass(com.jenkov.db.impl.mapping.method.MethodMapping.class);
        assertFalse(mapping1.equals(mapping2), "should not be equal, mapping2 has object class");

        mapping1.setObjectClass(com.jenkov.db.impl.mapping.method.MethodMapping.class);
        assertEquals(mapping1, mapping2, "should be to equal with object classes");

        //Testing primary key method mappings
        mapping1.getPrimaryKey().addColumn("test1");
        assertFalse(mapping1.equals(mapping2), "should not be equal, mapping1 has primary key method name");

        mapping1.getPrimaryKey().removeColumn("test1");
        mapping2.getPrimaryKey().addColumn("test1");
        assertFalse(mapping1.equals(mapping2), "should not be equal, mapping2 has primary key method name");

        mapping1.getPrimaryKey().addColumn("test1");
        assertEquals(mapping1, mapping2, "should be to equal with primary key method mappings");

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
        assertFalse(mapping1.equals(mapping2), "should not be equal, mapping1 has getter method mappings");

        mapping1.removeGetterMapping(fieldMapping1get.getColumnName());
        mapping1.removeGetterMapping(fieldMapping2get.getColumnName());
        mapping2.addGetterMapping(fieldMapping1get);
        mapping2.addGetterMapping(fieldMapping2get);
        assertFalse(mapping1.equals(mapping2), "should not be equal, mapping2 has getter method mappings");

        mapping1.addGetterMapping(fieldMapping1get);
        mapping1.addGetterMapping(fieldMapping2get);
        assertEquals(mapping1, mapping2, "should be to equal with getter method mappings");

        //setter method mappings
        mapping1.addSetterMapping(fieldMapping1set);
        mapping1.addSetterMapping(fieldMapping2set);
        assertFalse(mapping1.equals(mapping2), "should not be equal, mapping1 has setter method mappings");

        mapping1.removeSetterMapping(fieldMapping1get.getColumnName());
        mapping1.removeSetterMapping(fieldMapping2get.getColumnName());
        mapping2.addSetterMapping(fieldMapping1set);
        mapping2.addSetterMapping(fieldMapping2set);
        assertFalse(mapping1.equals(mapping2), "should not be equal, mapping2 has setter method mappings");

        mapping1.addSetterMapping(fieldMapping1set);
        mapping1.addSetterMapping(fieldMapping2set);
        assertEquals(mapping1, mapping2, "should be to equal with setter method mappings");
    }

    @Test
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

    @Test
    public void testHashCode(){
        ObjectMapping mapping = new ObjectMapping();
        assertTrue(mapping.hashCode() > 0);

        mapping.setObjectClass(PersistentObject.class);
        assertEquals(PersistentObject.class.hashCode(), mapping.hashCode());
    }

}
