package com.jenkov.db.impl.mapping;

import com.jenkov.db.itf.PersistenceException;
import com.jenkov.db.itf.mapping.IDbPrimaryKeyDeterminer;
import com.jenkov.db.itf.mapping.IKey;
import com.jenkov.db.util.JdbcUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Jakob Jenkov,  Jenkov Development
 */
public class DbPrimaryKeyDeterminer implements IDbPrimaryKeyDeterminer{

    ObjectMappingFactory factory = new ObjectMappingFactory();


    public IKey getPrimaryKeyMapping(String table, String databaseName, Connection connection)
    throws PersistenceException{
        ResultSet result = null;
        IKey mapping = this.factory.createKey();
        mapping.setTable(table);


        try {
            result = connection.getMetaData().getPrimaryKeys(null, databaseName, table);

            while(result.next()){
                mapping.addColumn(result.getString(4));
            }
        } catch (SQLException e) {
            throw new PersistenceException("Error determining primary key for table " + table, e);
        } finally {
            JdbcUtil.close(result);
        }

        return mapping;
    }

}
