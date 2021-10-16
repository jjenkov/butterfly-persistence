package com.jenkov.db.test.mapping;

import com.jenkov.db.PersistenceManager;
import com.jenkov.db.itf.IDaos;
import com.jenkov.db.test.Environment;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Jakob Jenkov - Copyright 2004-2006 Jenkov Development
 */
public class AnnotationBasedMappingTest {

    PersistenceManager persistenceManager = new PersistenceManager(Environment.getDataSource());

    @Test
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
            assertEquals(1, object.getId(), "id");
            assertEquals("the name", object.getName(), "name");

            assertEquals(2, object.getSomeValue(), "some value");
            assertEquals("some name", object.getSomeName(), "name");
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
