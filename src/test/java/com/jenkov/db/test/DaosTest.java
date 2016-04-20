package com.jenkov.db.test;

import com.jenkov.db.PersistenceManager;
import com.jenkov.db.impl.Daos;
import com.jenkov.db.impl.ObjectDao;
import com.jenkov.db.impl.PersistenceConfiguration;
import com.jenkov.db.itf.IObjectDao;
import com.jenkov.db.itf.IPersistenceConfiguration;
import junit.framework.TestCase;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;


/**
 * @author Jakob Jenkov - Copyright 2005 Jenkov Development
 */
public class DaosTest extends TestCase {

    public void testGetters() throws SQLException, IllegalAccessException, IOException, ClassNotFoundException, InstantiationException {
        Connection                connection = Environment.getConnection();
        IPersistenceConfiguration config     = new PersistenceConfiguration(new PersistenceManager());
        IObjectDao objectDao = new ObjectDao(connection, config);
        Daos daos = new Daos(connection, config, new PersistenceManager());

        assertSame(connection, daos.getConnection());
        assertSame(config    , daos.getConfiguration());
        assertSame(daos.getObjectDao(), daos.getObjectDao());
    }
}
