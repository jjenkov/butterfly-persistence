/**
 * User: Administrator
 */
package com.jenkov.db.test.mapping;

import com.jenkov.db.impl.mapping.DbNameGuesser;
import com.jenkov.db.test.objects.PersistentObject;
import org.junit.jupiter.api.Test;


import java.lang.reflect.Method;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class DbNameGuesserTest{

    protected DbNameGuesser nameGuesser = new DbNameGuesser();

    @Test
    public void testGetPossibleTableNames(){
        Collection names = this.nameGuesser.getPossibleTableNames(PersistentObject.class);
        assertEquals(16, names.size(), "wrong number of names guessed");
        assertTrue(names.contains("PersistentObject"), "1");
        assertTrue(names.contains("persistentObject"), "2");
        assertTrue(names.contains("persistentobject"), "3");
        assertTrue(names.contains("PERSISTENTOBJECT"), "4");
        assertTrue(names.contains("Persistent_Object"), "5");
        assertTrue(names.contains("persistent_object"), "6");
        assertTrue(names.contains("PERSISTENT_OBJECT"), "7");

        assertTrue(names.contains("PersistentObjects"), "8");
        assertTrue(names.contains("persistentObjects"), "9");
        assertTrue(names.contains("persistentobjects"), "10");
        assertTrue(names.contains("PERSISTENTOBJECTS"), "11");
        assertTrue(names.contains("PERSISTENTOBJECTs"), "12");
        assertTrue(names.contains("Persistent_Objects"), "13");
        assertTrue(names.contains("persistent_objects"), "14");
        assertTrue(names.contains("PERSISTENT_OBJECTS"), "15");
        assertTrue(names.contains("PERSISTENT_OBJECTs"), "16");
    }

    @Test
    public void testGetPossibleFieldNames() throws Exception{
        Class objectClass = PersistentObject.class;

        Method method = objectClass.getMethod("getObjectValue", new Class[] {});

        Collection names = this.nameGuesser.getPossibleColumnNames(method);
        assertEquals(7, names.size(), "wrong number of names guessed for getObjectValue()");
        assertTrue(names.contains("ObjectValue"), "1");
        assertTrue(names.contains("objectValue"), "2");
        assertTrue(names.contains("objectvalue"), "3");
        assertTrue(names.contains("OBJECTVALUE"), "4");
        assertTrue(names.contains("Object_Value"), "5");
        assertTrue(names.contains("object_value"), "6");
        assertTrue(names.contains("OBJECT_VALUE"), "7");

        method = objectClass.getMethod("getObject", new Class[] {});
        names = this.nameGuesser.getPossibleColumnNames(method);
        assertEquals(3, names.size(), "wrong number of names guessed for getObject()");
        assertTrue(names.contains("Object"), "1");
        assertTrue(names.contains("object"), "2");
        assertTrue(names.contains("OBJECT"), "3");
    }
}
