package com.jenkov.db.impl.init;

import com.jenkov.db.itf.init.IDatabaseInitialization;
import com.jenkov.db.itf.IDaos;
import com.jenkov.db.itf.PersistenceException;

/**

 */
public class DbInfoTableInitialization implements IDatabaseInitialization {

    public int getVersion() {
        return 0;
    }

    public void execute(IDaos daos) throws PersistenceException {
        String dbInfoSql1 = "create table db_info(name varchar(255), value varchar(255))";
        daos.getJdbcDao().update(dbInfoSql1);

        String versionRecordSql = "insert into db_info(name, value) values('version', '0')";
        daos.getJdbcDao().update(versionRecordSql);
    }
}
