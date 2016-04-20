package com.jenkov.db.test;

import junit.framework.TestCase;
import com.jenkov.db.itf.Database;

/**
 * @author Jakob Jenkov - Copyright 2005 Jenkov Development
 */
public class DatabaseTest extends TestCase{

    public void testGetName(){

        assertEquals("Default - JDBC Compliant Database", Database.DEFAULT.getName());
        assertEquals("HSQL Database Engine", Database.HSQLDB.getName());
    }
}
