/**
 * User: Administrator
 */
package com.jenkov.db.test.mapping;

import com.jenkov.db.impl.mapping.DbNameGuesser;
import com.jenkov.db.test.objects.PersistentObject;
import junit.framework.TestCase;


import java.lang.reflect.Method;
import java.util.Collection;

public class DbNameGuesserTest extends TestCase{

    protected DbNameGuesser nameGuesser = new DbNameGuesser();

    public void testGetPossibleTableNames(){
        Collection names = this.nameGuesser.getPossibleTableNames(PersistentObject.class);
        assertEquals("wrong number of names guessed", 16, names.size());
        assertTrue("1", names.contains("PersistentObject"));
        assertTrue("2", names.contains("persistentObject"));
        assertTrue("3", names.contains("persistentobject"));
        assertTrue("4", names.contains("PERSISTENTOBJECT"));
        assertTrue("5", names.contains("Persistent_Object"));
        assertTrue("6", names.contains("persistent_object"));
        assertTrue("7", names.contains("PERSISTENT_OBJECT"));

        assertTrue("8", names.contains("PersistentObjects"));
        assertTrue("9", names.contains("persistentObjects"));
        assertTrue("10", names.contains("persistentobjects"));
        assertTrue("11", names.contains("PERSISTENTOBJECTS"));
        assertTrue("12", names.contains("PERSISTENTOBJECTs"));
        assertTrue("13", names.contains("Persistent_Objects"));
        assertTrue("14", names.contains("persistent_objects"));
        assertTrue("15", names.contains("PERSISTENT_OBJECTS"));
        assertTrue("16", names.contains("PERSISTENT_OBJECTs"));
    }

    public void testGetPossibleFieldNames() throws Exception{
        Class objectClass = PersistentObject.class;

        Method method = objectClass.getMethod("getObjectValue", new Class[] {});

        Collection names = this.nameGuesser.getPossibleColumnNames(method);
        assertEquals("wrong number of names guessed for getObjectValue()", 7, names.size());
        assertTrue("1", names.contains("ObjectValue"));
        assertTrue("2", names.contains("objectValue"));
        assertTrue("3", names.contains("objectvalue"));
        assertTrue("4", names.contains("OBJECTVALUE"));
        assertTrue("5", names.contains("Object_Value"));
        assertTrue("6", names.contains("object_value"));
        assertTrue("7", names.contains("OBJECT_VALUE"));

        method = objectClass.getMethod("getObject", new Class[] {});
        names = this.nameGuesser.getPossibleColumnNames(method);
        assertEquals("wrong number of names guessed for getObject()",3, names.size());
        assertTrue("1", names.contains("Object"));
        assertTrue("2", names.contains("object"));
        assertTrue("3", names.contains("OBJECT"));
    }
}
