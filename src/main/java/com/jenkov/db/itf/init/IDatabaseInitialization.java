package com.jenkov.db.itf.init;

import com.jenkov.db.itf.IDaos;
import com.jenkov.db.itf.PersistenceException;

import java.sql.Connection;

/**

 */
public interface IDatabaseInitialization {

    public int   getVersion();
    public void  execute(IDaos daos) throws Exception;

}
