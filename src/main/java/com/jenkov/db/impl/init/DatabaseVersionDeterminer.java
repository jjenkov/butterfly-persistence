package com.jenkov.db.impl.init;

import com.jenkov.db.itf.IDaos;
import com.jenkov.db.itf.PersistenceException;

import java.sql.ResultSet;
import java.sql.SQLException;

/**

 */
public class DatabaseVersionDeterminer {

    public int determineDatabaseVersion(IDaos daos) throws PersistenceException {

        boolean dbInfoTableExists = false;
        dbInfoTableExists = tableExists(daos, "db_info");

        if(!dbInfoTableExists) return -1;

        DbInfo versionInfo = (DbInfo) daos.getObjectDao().read(DbInfo.class, "select * from db_info where name='version' ");
        if(versionInfo == null){
            throw new PersistenceException("Database not property initialized: db_info table is missing version record!");
        }

        return versionInfo.getValueAsInt();
    }

    public boolean tableExists(IDaos daos, String tableName) throws PersistenceException {
        boolean tableExists = false;
        ResultSet result = null;
        try {
            result = daos.getConnection().getMetaData().getTables(null, null, null, null );
            while(result.next()){
                String resultSetTableName = result.getString(3);
                if(tableName.equalsIgnoreCase(resultSetTableName)) {
                    tableExists = true;
                }
            }
        } catch (SQLException e) {
            throw new PersistenceException("Error obtaining list of table names from database meta data", e);
        } finally {
            try {
                result.close();
            } catch (SQLException e) {
                throw new PersistenceException("Error closing ResultSet obtained from database meta data", e);
            }
        }

        return tableExists;
    }


}
