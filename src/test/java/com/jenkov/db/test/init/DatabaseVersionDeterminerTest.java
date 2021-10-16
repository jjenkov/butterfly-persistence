package com.jenkov.db.test.init;

import com.jenkov.db.jdbc.SimpleDataSource;
import com.jenkov.db.PersistenceManager;
import com.jenkov.db.itf.IDaos;
import com.jenkov.db.itf.PersistenceException;
import com.jenkov.db.impl.init.DatabaseVersionDeterminer;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**

 */
public class DatabaseVersionDeterminerTest {

    @Test
    public void testDetermineDatabaseVersion() throws PersistenceException {

        //todo should this DataSource, or the database coordinates at least, be obtained from the Environment class?
        SimpleDataSource          dataSource         = new SimpleDataSource("org.h2.Driver", "jdbc:h2:./database/h2/init-test", "sa", "");
        PersistenceManager        persistenceManager = new PersistenceManager(dataSource);
        DatabaseVersionDeterminer versionDeterminer  = new DatabaseVersionDeterminer();

        IDaos daos = null;
        try{
            daos = persistenceManager.createDaos();

            // first delete the existing db_info table...
            daos.getJdbcDao().update("drop table if exists db_info");

            int version = versionDeterminer.determineDatabaseVersion(daos);
            assertEquals(-1, version);

            String dbInfoSql1 = "create table db_info(name varchar(255), value varchar(255))";
            daos.getJdbcDao().update(dbInfoSql1);
            try {
                version = versionDeterminer.determineDatabaseVersion(daos);
                fail("should throw exception when no version record present");
            } catch (Exception e) {
                //ignore, expected.
            }


            String dbInfoSql2 = "insert into db_info(name, value) values('version', '0')";
            daos.getJdbcDao().update(dbInfoSql2);

            version = versionDeterminer.determineDatabaseVersion(daos);
            assertEquals(0, version);



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