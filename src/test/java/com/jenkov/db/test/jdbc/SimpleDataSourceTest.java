package com.jenkov.db.test.jdbc;

import com.jenkov.db.jdbc.SimpleDataSource;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Jakob Jenkov - Copyright 2005 Jenkov Development
 */
public class SimpleDataSourceTest{


    @Test
    public void testGetConnection() throws Exception {
        SimpleDataSource dataSource =
                new SimpleDataSource("org.h2.Driver", "jdbc:h2:./database/h2/testDataSource", "sa", "");

        Connection connection = dataSource.getConnection();
        assertNotNull(connection, "connection should not be null");
        assertTrue(connection.getAutoCommit(), "should have auto commmit set");
        connection.close();

        connection = dataSource.getConnection("sa", "");
        assertNotNull(connection, "connection should not be null");
        assertTrue(connection.getAutoCommit(), "should have auto commmit set");
        connection.close();
    }
}
