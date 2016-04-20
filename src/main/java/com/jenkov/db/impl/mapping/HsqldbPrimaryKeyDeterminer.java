package com.jenkov.db.impl.mapping;

import com.jenkov.db.itf.mapping.IDbPrimaryKeyDeterminer;
import com.jenkov.db.itf.mapping.IKey;
import com.jenkov.db.itf.PersistenceException;
import com.jenkov.db.util.JdbcUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Jakob Jenkov - Copyright 2005 Jenkov Development
 */
public class HsqldbPrimaryKeyDeterminer implements IDbPrimaryKeyDeterminer {

    public String getPrimaryKeyColumnName(String table, String databaseName, Connection connection) throws PersistenceException{

        ResultSet result = null;
        try {
            result = connection.getMetaData().getPrimaryKeys(null, databaseName, table.toUpperCase());

            if(result.next()){
                return result.getString(4);
            }
        } catch (SQLException e) {
            throw new PersistenceException("Error determining primary key for table " + table, e);
        } finally {
            JdbcUtil.close(result);
        }

        return null;
    }

    public IKey getPrimaryKeyMapping(String table, String databaseName, Connection connection) throws PersistenceException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
