package com.jenkov.db.test.util;

import com.jenkov.db.util.ClassUtil;
import com.jenkov.db.impl.mapping.method.SetterMapping;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Jakob Jenkov - Copyright 2005 Jenkov Development
 */
public class ClassUtilTest{


    public void testIsSubstitutableFor(){
        fail("not implemented");
    }

    public void testIsSubclassOf(){
        fail("not implemented");
    }

    public void testIsInterfaceOrSubInterfaceImplemented(){
        fail("not implemented");
    }

    @Test
    public void testCompare() throws Exception {
        assertEquals( 0, ClassUtil.compare((Object)null, (Object)null));
        assertEquals( 1, ClassUtil.compare(null, "test"));
        assertEquals(-1, ClassUtil.compare("test", null));
        assertEquals(-1, ClassUtil.compare("a", "b"));
        assertEquals( 1, ClassUtil.compare("b", "a"));
        assertEquals( 0, ClassUtil.compare("a", "a"));
    }

    @Test
    public void testAreEqual(){
        assertTrue(ClassUtil.areEqual(null, null));

        assertFalse(ClassUtil.areEqual(null, "test")) ;
        assertFalse(ClassUtil.areEqual("test", null)) ;

        assertTrue(ClassUtil.areEqual("test", "test"));
        assertFalse(ClassUtil.areEqual("test2", "test")) ;
    }

    @Test
    public void testIsGetter() throws Exception {

        try{
            ClassUtil.isGetter(null);
            fail("should throw NullPointerException");
        } catch(NullPointerException e){
            //expected
        }


        assertTrue(ClassUtil.isGetter(SetterMapping.class.getMethod("isTableMapped", null)));
        assertTrue(ClassUtil.isGetter(Object.class.getMethod("getClass", null)));
        assertFalse(ClassUtil.isGetter(Properties.class.getMethod("getProperty", new Class[]{String.class})));
        assertFalse(ClassUtil.isGetter(ClassUtilTest.class.getMethod("testIsGetter", null)));
    }

    @Test
    public void testIsSetter() throws Exception {
        try{
            ClassUtil.isSetter(null);
        } catch (NullPointerException e){
            //expected
        }

        assertTrue(ClassUtil.isSetter(SetterMapping.class.getMethod("setTableMapped", new Class[]{boolean.class} )));
        assertFalse(ClassUtil.isSetter(Properties.class.getMethod("setProperty", new Class[]{String.class, String.class})));
        assertFalse(ClassUtil.isSetter(ClassUtilTest.class.getMethod("testIsSetter", null)));
    }



}
