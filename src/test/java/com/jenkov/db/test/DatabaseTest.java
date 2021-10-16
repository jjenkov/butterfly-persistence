package com.jenkov.db.test;

import com.jenkov.db.itf.Database;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Jakob Jenkov - Copyright 2005 Jenkov Development
 */
public class DatabaseTest {

    @Test
    public void testGetName(){

        assertEquals("Default - JDBC Compliant Database", Database.DEFAULT.getName());
        assertEquals("HSQL Database Engine", Database.HSQLDB.getName());
    }
}
