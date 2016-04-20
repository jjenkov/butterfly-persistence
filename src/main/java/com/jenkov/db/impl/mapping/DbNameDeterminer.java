/**
 * User: Administrator
 */
package com.jenkov.db.impl.mapping;

import com.jenkov.db.itf.mapping.IDbNameDeterminer;
import com.jenkov.db.itf.PersistenceException;
import com.jenkov.db.util.JdbcUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class DbNameDeterminer implements IDbNameDeterminer {

    protected Map tableColumnSets  = new HashMap();
    protected Map tableColumnTypes = new HashMap();

    public void init(){
        this.tableColumnSets.clear();
    }

    public int getColumnType(String columnName, String tableName){
        Map tableColumnTypeMap = (Map) this.tableColumnTypes.get(tableName);
        return ((Integer) tableColumnTypeMap.get(columnName)).intValue();
    }


    public String determineColumnName(Collection names, String tableName, Connection connection)
    throws PersistenceException {

        Set<String> fieldNames  = new HashSet<String>();
        Set<String> columnNames = null;
        Iterator fieldIterator = names.iterator();

        try {
            columnNames = getColumns(tableName, connection);

            while(fieldIterator.hasNext()){
                String fieldName = (String) fieldIterator.next();
                if(columnNames.contains(fieldName)){
                    fieldNames.add(fieldName);
                }
                /*
                for(String columnName : columnNames){
                    if(fieldName.equalsIgnoreCase(columnName)){
                        fieldNames.add(fieldName);
                    }
                }
                */
            }
        } catch (SQLException e) {
            throw new PersistenceException("Could not read database meta data for table " + tableName, e);
        }

        if(fieldNames.size() == 1) return (String) fieldNames.iterator().next();
        if(fieldNames.size() > 1){
            throw new PersistenceException("More than one column in table " + tableName
                +" matched the guessed names: " + names.toString()
                +". \nMatching columns were: " + fieldNames.toString());
        }
        return null;
    }


    public String determineTableName(Collection names, Connection connection)
    throws PersistenceException{
        return determineTableName(names, null, connection);
    }


    public String determineTableName(Collection names, String databaseName, Connection connection)
    throws PersistenceException {
        Set<String> tableNames = new HashSet<String>();
        try {
            Iterator nameIterator = names.iterator();
            while(nameIterator.hasNext()){
                String name = (String) nameIterator.next();
                ResultSet result = connection.getMetaData().getTables(databaseName, null, name, null );
                try{
                    while(result.next()){
                        if(name.equalsIgnoreCase(result.getString(3))) tableNames.add(name);
                    }
                }finally {
                    if(result != null) result.close();
                }
            }
        } catch (SQLException e) {
            throw new PersistenceException("Could not read meta data for database " + databaseName, e);
        }

        if(tableNames.size() == 1) return (String) tableNames.iterator().next();
        if(tableNames.size() > 1){

            Set<String> tableNamesCaseInsensitive = new HashSet<String>();
            for(String tableName : tableNames){
                tableNamesCaseInsensitive.add(tableName.toLowerCase());
            }

            if(tableNamesCaseInsensitive.size() == 1){
                return tableNames.iterator().next();
            }

            throw new PersistenceException("More than one table in database " + databaseName
                +" matched the guessed names: " + names.toString()
                +". \nMatching tables were: " + tableNames.toString());
        }
        throw new PersistenceException("No tables in database " + databaseName
            + " matched the guessed names: " + names.toString());
    }

    private synchronized Set<String> getColumns(String tableName, Connection connection) throws SQLException, PersistenceException {
        Set<String>       columns = (Set<String>) this.tableColumnSets.get(tableName);
        if(columns != null){
            return columns;
        }
        columns = new HashSet<String>();
        Map columnTypes = new HashMap();

        findColumnsForTable(tableName, connection, columns, columnTypes);
        if(columns.size() == 0){
            findColumnsForTable(tableName.toUpperCase(), connection, columns, columnTypes);
            if(columns.size() == 0){
                findColumnsForTable(tableName.toLowerCase(), connection, columns, columnTypes);
            }
        }
        this.tableColumnSets.put(tableName, columns);
        this.tableColumnTypes.put(tableName, columnTypes);
        return columns;
    }

    private void findColumnsForTable(String tableName, Connection connection, Set<String> columns, Map columnTypes) throws SQLException, PersistenceException {
        ResultSet result  = null;
        try {
            result = connection.getMetaData().getColumns(null, null,  tableName, null);
            while(result.next()){
                if(result.getString(4) != null){
                    columns.add(result.getString(4));
                    columnTypes.put(result.getString(4), new Integer(result.getInt(5)));
                }
            }
        } finally {
            JdbcUtil.close(result);
        }
    }

}
