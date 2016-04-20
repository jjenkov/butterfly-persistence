package com.jenkov.db.test.mapping;

import junit.framework.TestCase;
import com.jenkov.db.PersistenceManager;
import com.jenkov.db.itf.IDaos;
import com.jenkov.db.test.Environment;

/**
 * @author Jakob Jenkov - Copyright 2004-2006 Jenkov Development
 */
public class AnnotationBasedMappingTest extends TestCase {

    PersistenceManager persistenceManager = new PersistenceManager(Environment.getDataSource());

    public void testAnnotations() throws Exception{

        try {
            Environment.executeSql("delete from persistent_object");

            IDaos daos = persistenceManager.createDaos();

            AnnotatedClass object = new AnnotatedClass();
            object.setId(1);
            object.setName("the name");
            object.setSomeValue(2);
            object.setSomeName("some name");
            daos.getObjectDao().insert(object);

            AnnotatedClass object2 = (AnnotatedClass) daos.getObjectDao().readByPrimaryKey(AnnotatedClass.class, new Long(1));
            assertNotNull(object2);
            assertEquals("id"  , 1, object.getId());
            assertEquals("name", "the name", object.getName());

            assertEquals("some value"  , 2, object.getSomeValue());
            assertEquals("name", "some name", object.getSomeName());
        } finally {
            Environment.executeSql("delete from persistent_object");
        }
    }

    public static void main(String[] args) {
        AnnotationBasedMappingTest test = new AnnotationBasedMappingTest();
        try {
            test.testAnnotations();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
