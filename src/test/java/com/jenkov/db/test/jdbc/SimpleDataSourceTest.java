package com.jenkov.db.test.jdbc;

import com.jenkov.db.jdbc.SimpleDataSource;
import junit.framework.TestCase;

import java.sql.Connection;

/**
 * @author Jakob Jenkov - Copyright 2005 Jenkov Development
 */
public class SimpleDataSourceTest extends TestCase{


    public void testGetConnection() throws Exception {
        SimpleDataSource dataSource =
                new SimpleDataSource("org.h2.Driver", "jdbc:h2:database/h2/testDataSource", "sa", "");

        Connection connection = dataSource.getConnection();
        assertNotNull("connection should not be null", connection);
        assertTrue("should have auto commmit set", connection.getAutoCommit());
        connection.close();

        connection = dataSource.getConnection("sa", "");
        assertNotNull("connection should not be null", connection);
        assertTrue("should have auto commmit set", connection.getAutoCommit());
        connection.close();
    }
}
