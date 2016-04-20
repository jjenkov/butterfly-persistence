package com.jenkov.db.impl.init;

import com.jenkov.db.itf.init.IDatabaseInitialization;
import com.jenkov.db.itf.IDaos;
import com.jenkov.db.itf.PersistenceException;

/**

 */
public class SqlInitialization implements IDatabaseInitialization {

    protected int    version = 0;
    protected String sql     = null;

    public SqlInitialization(int version, String sql) {
        this.version = version;
        this.sql = sql;
    }

    public int getVersion() {
        return this.version;
    }

    public void execute(IDaos daos) throws Exception {
        daos.getJdbcDao().update(this.sql);
    }
}
