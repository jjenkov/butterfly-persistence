package com.jenkov.db.test.mapping;

import com.jenkov.db.impl.mapping.method.MethodMapping;
import com.jenkov.db.itf.mapping.IMethodMapping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the methods of the default implementation of IMethodMapping, the
 * MethodMapping class.
 */
public class MethodMappingTest {

    protected IMethodMapping fieldMapping = null;

    @BeforeEach
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
    @Test
    public void testIsTableMapped() throws Exception{
        assertFalse(this.fieldMapping.isTableMapped(), "should default to false");

        this.fieldMapping.setTableMapped(true);
        assertTrue(this.fieldMapping.isTableMapped(), "should be true");

        this.fieldMapping.setTableMapped(false);
        assertFalse(this.fieldMapping.isTableMapped(), "should be false");
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

    @Test
    public void testGetDbFieldName() throws Exception{
        assertNull(this.fieldMapping.getColumnName(), "should default to null");

        this.fieldMapping.setColumnName("test");
        assertEquals("test", this.fieldMapping.getColumnName(), "should be test");

        this.fieldMapping.setColumnName(null);
        assertNull(this.fieldMapping.getColumnName(), "should be null");
    }


    /**
     * (Getter / Setter Test) Tests that the getObjectMethod() / setObjectMethod() works correctly.
     * @throws Exception If any exceptions are thrown during the test.
     */
    @Test
    public void testGetObjectMember() throws Exception{
        assertNull(this.fieldMapping.getObjectMethod(), "should default to null");

        Method method = MethodMapping.class.getMethod("getColumnType", null);

        this.fieldMapping.setObjectMethod(MethodMapping.class.getMethod("getColumnType", null));
        assertEquals(method, this.fieldMapping.getObjectMethod(), "should be method");

        this.fieldMapping.setObjectMethod(null);
        assertNull(this.fieldMapping.getObjectMethod(), "should be null");
    }

    /**
     * Tests the equals(Object o) method of the MethodMapping implementation
     */
    @Test
    public void testEquals() throws Exception{
        IMethodMapping field1 = new com.jenkov.db.impl.mapping.method.MethodMapping();
        IMethodMapping field2 = new com.jenkov.db.impl.mapping.method.MethodMapping();

        assertEquals(field1, field2, "should default to equal");

        //test setColumnName and equals()
        field1.setColumnName("method");
        assertFalse(field1.equals(field2), "db method 1 set");

        field1.setColumnName(null);
        field2.setColumnName("method");
        assertFalse(field1.equals(field2), "db method 2 set");

        field1.setColumnName("method");
        assertEquals(field1, field2, "should be to equal - db fields equal");

        
        //test setObjectMethod and equals()
        field1.setObjectMethod(com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("getColumnName", null));
        assertFalse(field1.equals(field2), "field1 object objectMethod set");

        field1.setObjectMethod(null);
        field2.setObjectMethod(com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("getColumnName", null));
        assertFalse(field1.equals(field2), "field2 object objectMethod set");

        field1.setObjectMethod(com.jenkov.db.impl.mapping.method.MethodMapping.class.getMethod("getColumnName", null));
        assertEquals(field1, field2, "should be to equal - same object objectMethod");

        //test setTableMapped and equals()
        field1.setTableMapped(true);
        assertFalse(field1.equals(field2), "field1 table mapped true");

        field1.setTableMapped(false);
        field2.setTableMapped(true);
        assertFalse(field1.equals(field2), "field2 table mapped true");

        field1.setTableMapped(true);
        assertEquals(field1, field2, "should be to equal - table mapped true");
    }



}
