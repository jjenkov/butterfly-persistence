package com.jenkov.db.test;

import com.jenkov.db.PersistenceManager;
import com.jenkov.db.itf.*;
import com.jenkov.db.jdbc.SimpleDataSource;
import junit.framework.TestCase;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * @author Jakob Jenkov - Copyright 2005 Jenkov Development
 */
public class PersistenceManagerTest extends TestCase {
    PersistenceManager manager = new PersistenceManager();

    protected void setUp() throws Exception {

    }

    public void testGettersSetters(){
        assertNotNull(manager.getConfiguration());
    }

    public void testGetSetDataSource(){
        DataSource dataSource = new SimpleDataSource("org.h2.Driver", "jdbc:hsqldb:hsql://localhost", "sa", "");
        PersistenceManager manager = new PersistenceManager();

        assertNull(manager.getConfiguration().getDataSource());

        manager.setDataSource(dataSource);
        assertSame(dataSource, manager.getConfiguration().getDataSource());
        manager.setDataSource(null);
        assertNull(manager.getConfiguration().getDataSource());
    }


    public void testCreateDaos() throws Exception{
        Connection connection = null;
        try {
            connection = Environment.getConnection();
            IDaos daos = this.manager.createDaos(connection);
            assertSame(connection, daos.getConnection());
        } finally {
            connection.close();
        }
    }
}
