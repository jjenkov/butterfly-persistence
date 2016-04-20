package com.jenkov.db.test.mapping;

import com.jenkov.db.impl.mapping.method.MethodMapping;
import com.jenkov.db.itf.mapping.IMethodMapping;
import junit.framework.TestCase;

import java.lang.reflect.Method;

/**
 * Tests the methods of the default implementation of IMethodMapping, the
 * MethodMapping class.
 */
public class MethodMappingTest extends TestCase{

    protected IMethodMapping fieldMapping = null;

    public MethodMappingTest(String test){
        super(test);
    }

    public void setUp(){
        //IObjectMapping objectMapping = new ObjectMapping();
        this.fieldMapping = new MethodMapping();
    }



    /**
     * (Getter / Setter Test)
     * Simply tests that the isForeignKey defaults to false, and that the setForeignKey(...) updates the
     * isForeignKey() flag correctly.
     * @throws Exception If an exception is thrown during the test.
     */
    /*
    public void testIsForeignKey() throws Exception{
        assertFalse("should default to false", this.fieldMapping.isForeignKey());

        this.fieldMapping.setForeignKey(true);
        assertTrue("should be true", this.fieldMapping.isForeignKey());

        this.fieldMapping.setForeignKey(false);
        assertFalse("should be false", this.fieldMapping.isForeignKey());
    }*/


    /**
     * (Getter / Setter Test)
     * Simply tests that the isTableMapped defaults to false, and that the setTableMapped(...) updates the
     * isTableMapped() flag correctly.
     * @throws Exception If an exception is thrown during the test.
     */
    public void testIsTableMapped() throws Exception{
        assertFalse("should default to false", this.fieldMapping.isTableMapped());

        this.fieldMapping.setTableMapped(true);
        assertTrue("should be true", this.fieldMapping.isTableMapped());

        this.fieldMapping.setTableMapped(false);
        assertFalse("should be false", this.fieldMapping.isTableMapped());
    }


    /**
     * Tests that the isGetter() method correctly identifies the <code>Method</code>
     * instance contained in it as a getter, if the method is a getter, and not as
     * a getter if the <code>Method</code> is not a getter. If no <code>Method</code>
     * instance is contained in the IMethodMapping instance, the isGetter() should throw
     * a NullPointerException.
     * @throws Exception If an exception is thrown during the test.
     */

    /*
    public void testIsGetter() throws Exception{
        assertFalse("should default to false", this.fieldMapping.isGetter());

        this.fieldMapping.setGetter(true);
        assertTrue("should be true", this.fieldMapping.isGetter());

        this.fieldMapping.setGetter(false);
        assertFalse("should be false", this.fieldMapping.isGetter());
    }
    */

    /**
     * Tests that the isSetter() method correctly identifies the <code>Method</code>
     * instance contained in it as a setter, if the method is a setter, and not as
     * a setter if the <code>Method</code> is not a setter. If no <code>Method</code>
     * instance is contained in the IMethodMapping instance, the isSetter() should throw
     * a NullPointerException.
     * @throws Exception If an exception is thrown during the test.
     */

    /*
    public void testIsSetter() throws Exception{
        assertFalse("should default to false", this.fieldMapping.isSetter());

        this.fieldMapping.setSetter(true);
        assertTrue("should be true", this.fieldMapping.isSetter());

        this.fieldMapping.setSetter(false);
        assertFalse("should be false", this.fieldMapping.isSetter());
    }
    */

    /**
     * (Getter / Setter Test) Tests that the getColumnName() / setColumnName() works correctly.
     * @throws Exception If any exceptions are thrown during the test.
     */
    public void testGetDbFieldName() throws Exception{
        assertNull("should default to null", this.fieldMapping.getColumnName());

        this.fieldMapping.setColumnName("test");
        assertEquals("should be test", "test", this.fieldMapping.getColumnName());

        this.fieldMapping.setColumnName(null);
        assertNull("should be null", this.fieldMapping.getColumnName());
    }


    /**
     * (Getter / Setter Test) Tests that the getObjectMethod() / setObjectMethod() works correctly.
     * @throws Exception If any exceptions are thrown during the test.
     */
    public void testGetObjectMember() throws Exception{
        assertNull("should default to null", this.fieldMapping.getObjectMethod());

        Method method = MethodMapping.class.getMethod("getColumnType", null);

        this.fieldMapping.setObjectMethod(MethodMapping.class.getMethod("getColumnType", null));
        assertEquals("should be method", method, this.fieldMapping.getObjectMethod());

        this.fieldMapping.setObjectMethod(null);
        assertNull("should be null", this.fieldMapping.getObjectMethod());
    }

    /**
     * Tests the equals(Object o) method of the MethodMapping implementation
     */
    public void testEquals() throws Exception{
        IMethodMapping field1 = new com.jenkov.db.impl.mapping.method.MethodMapping();
        IMethodMapping field2 = new com.jenkov.db.impl.mapping.method.MethodMapping();

        assertEquals("should default to equal", field1, field2);

        //test setColumnName and equals()
        field1.setColumnName("method");
        assertFalse("db method 1 set", field1.equals(field2));

        field1.setColumnName(null);
        field2.setColumnName("method");
        assertFalse("db method 2 set", field1.equals(field2));

        field1.setColumnName("method");
        assertEquals("should be to equal - db fields equal", field1, field2);

        
        //test setObjectMethod and equals()
        field1.setObjectMethod(com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("getColumnName", null));
        assertFalse("field1 object objectMethod set", field1.equals(field2));

        field1.setObjectMethod(null);
        field2.setObjectMethod(com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("getColumnName", null));
        assertFalse("field2 object objectMethod set", field1.equals(field2));

        field1.setObjectMethod(com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("getColumnName", null));
        assertEquals("should be to equal - same object objectMethod", field1, field2);

        //test setTableMapped and equals()
        field1.setTableMapped(true);
        assertFalse("field1 table mapped true", field1.equals(field2));

        field1.setTableMapped(false);
        field2.setTableMapped(true);
        assertFalse("field2 table mapped true", field1.equals(field2));

        field1.setTableMapped(true);
        assertEquals("should be to equal - table mapped true", field1, field2);
    }



}
