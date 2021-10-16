package com.jenkov.db.test.init;

import com.jenkov.db.jdbc.SimpleDataSource;
import com.jenkov.db.PersistenceManager;
import com.jenkov.db.impl.init.DatabaseInitializer;
import com.jenkov.db.impl.init.SqlInitialization;
import com.jenkov.db.itf.IDaos;
import com.jenkov.db.itf.PersistenceException;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**

 */
public class DatabaseInitializerTest {

    @Test
    public void testInitialize() throws PersistenceException {
        SimpleDataSource    dataSource          = new SimpleDataSource("org.h2.Driver", "jdbc:h2:./database/h2/init-test", "sa", "");
        PersistenceManager  persistenceManager  = new PersistenceManager(dataSource);
        DatabaseInitializer databaseInitializer = new DatabaseInitializer();

        IDaos daos = null;
        try{
            daos = persistenceManager.createDaos();

            // first delete the existing db_info table...
            daos.getJdbcDao().update("drop table if exists db_info");
            daos.getJdbcDao().update("drop table if exists one");
            daos.getJdbcDao().update("drop table if exists two");

            databaseInitializer.initialize(daos);

            /* this initialization should get carried out*/
            databaseInitializer.add(1, "create table one(id identity, value varchar(255))");
            databaseInitializer.add(1, "insert into one(value) values('one')");
            databaseInitializer.initialize(daos);

            Long oneId =  daos.getJdbcDao().readLong("select id from one where value ='one'");
            assertEquals(1, oneId.longValue());


            /* this initialization should not get carried out. database version should be 1 after previous initialization */
            /* if this initialization IS carried out, the test will fail, as the database will refuse re-creating an existing table */
            databaseInitializer.add(1, "create table one(id identity, value varchar(255))");
            databaseInitializer.initialize(daos);

            databaseInitializer.add(2, "create table two(id identity, value varchar(255))");
            databaseInitializer.add(2, "insert into two(value) values('two')");
            databaseInitializer.initialize(daos);

            Long twoId =  daos.getJdbcDao().readLong("select id from two where value ='two'");
            assertEquals(1, twoId.longValue());


        } finally {
            if(daos != null){
                try {
                    daos.getConnection().close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    fail("Error closing connection");
                }
            }
        }

    }
}
