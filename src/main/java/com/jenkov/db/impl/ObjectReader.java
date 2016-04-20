package com.jenkov.db.impl;

import com.jenkov.db.impl.filter.AcceptAllReadFilter;
import com.jenkov.db.itf.IObjectReader;
import com.jenkov.db.itf.IReadFilter;
import com.jenkov.db.itf.PersistenceException;
import com.jenkov.db.itf.Database;
import com.jenkov.db.itf.mapping.IKeyValue;
import com.jenkov.db.itf.mapping.IObjectMapping;
import com.jenkov.db.itf.mapping.ISetterMapping;
import com.jenkov.db.util.JdbcUtil;
import com.jenkov.db.util.MappingUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.*;

/**
 * The default implementation of the <code>IObjectReader</code> interface.
 * 
 * @author Jakob Jenkov,  Jenkov Development
 */
public class ObjectReader implements IObjectReader {

    protected Database database = null;

    public void setDatabase(Database database) {
        this.database = database;
    }


    public Object readByPrimaryKey(IObjectMapping mapping, Object primaryKey, String sql, Connection connection) throws PersistenceException{
        assertPrimaryKeyHasColumns(mapping);
        if(primaryKey instanceof IKeyValue){
            return readByPrimaryKey(mapping, (IKeyValue) primaryKey, sql, connection);
        }
        //todo read into existing object
        if(mapping.getObjectClass().equals(primaryKey.getClass())){
            IKeyValue keyValue = mapping.getPrimaryKeyValueForObject(primaryKey, null);
            return readByPrimaryKey(mapping, keyValue, primaryKey, sql, connection);
        }

        IKeyValue keyValue = mapping.getPrimaryKey().toKeyValue(primaryKey);
        return readByPrimaryKey(mapping, keyValue, sql, connection);
    }

    private void assertPrimaryKeyHasColumns(IObjectMapping mapping) throws PersistenceException {
        if(mapping.getPrimaryKey().getColumns().size() < 1){
            throw new PersistenceException("The object mapping's primary key contains no column.");
        }
    }

    public Object readByPrimaryKey(IObjectMapping mapping, IKeyValue primaryKey, Object persistentObject, String sql,
                                   Connection connection) throws PersistenceException {
        PreparedStatement statement = null;

        if(primaryKey == null){
            throw new PersistenceException("Primary key was null");
        }

        try{
            statement = connection.prepareStatement(sql);
            MappingUtil.insertPrimaryKey(mapping, primaryKey, statement, 1);
            return read(mapping, statement, persistentObject);
        } catch (SQLException e){
            throw new PersistenceException("Error reading object by primary key (" + primaryKey + ") using SQL(" +
                    sql + ") and object mapping (" + mapping + ")", e);
        }
        finally {
            JdbcUtil.close(statement);
        }
    }

    public Object readByPrimaryKey(IObjectMapping mapping, IKeyValue primaryKey, String sql, Connection connection)
    throws PersistenceException{
        PreparedStatement statement = null;

        if(primaryKey == null){
            throw new PersistenceException("Primary key was null");
        }
                                                                          
        try{
            statement = connection.prepareStatement(sql);
            MappingUtil.insertPrimaryKey(mapping, primaryKey, statement, 1);
            return read(mapping, statement);
        } catch (SQLException e){
            throw new PersistenceException("Error reading object by primary key (" + primaryKey + ") using SQL(" +
                    sql + ") and object mapping (" + mapping + ")", e);
        }
        finally {
            JdbcUtil.close(statement);
        }
    }

    public Object read(IObjectMapping mapping, ResultSet result, Object persistentObject)  throws PersistenceException{
        try{
            for(int i=1, n=result.getMetaData().getColumnCount(); i <= n; i++){
                String columnName = result.getMetaData().getColumnName(i);
                ISetterMapping fieldMapping = mapping.getSetterMapping(columnName);
                if(fieldMapping != null){
                    fieldMapping.insertValueIntoObject(persistentObject, result);
                }
            }
        } catch(SQLException e){
            throw new PersistenceException("Error populating persistent object with values", e);
        }

        return persistentObject;
     }

    public Object read(IObjectMapping mapping, ResultSet result)  throws PersistenceException{
        Object persistentObject = newInstance(mapping.getObjectClass(), result);
        return read(mapping, result, persistentObject);
     }

    //todo read into existing object
    public Object read(IObjectMapping mapping, Statement statement, String sql) throws PersistenceException{
         ResultSet result = null;
         try{
             result = statement.executeQuery(sql);
             if(!result.next()){
                 return null;
             }
             //Object persistentObject = read(mapping, result);
             //return persistentObject;
             return read(mapping, result);
        } catch (SQLException e){
             throw new PersistenceException("Error reading from statement with sql:\n" + sql, e);
         } finally {
             JdbcUtil.close(result);
         }
     }

     public Object read(IObjectMapping mapping, String sql, Connection connection) throws PersistenceException{

         Statement statement = null;
         try {
             statement   = connection.createStatement();
             return      read(mapping, statement, sql);
         } catch (SQLException e) {
             throw new PersistenceException("Error reading object. Sql: " + sql, e);
         } finally {
             JdbcUtil.close(statement);
         }
     }

     public Object read(IObjectMapping mapping, PreparedStatement preparedStatement, Object persistentObject) throws PersistenceException{
         ResultSet result = null;
         try {
             result = preparedStatement.executeQuery();
             if(!result.next()){
                 return null;
             }
             persistentObject = read(mapping, result, persistentObject);
             return persistentObject;
         } catch (SQLException e) {
             throw new PersistenceException("Error reading object from PreparedStatement", e);
         } finally {
             JdbcUtil.close(result);
         }
     }

     public Object read(IObjectMapping mapping, PreparedStatement preparedStatement) throws PersistenceException{
         ResultSet result = null;
         Object persistentObject = null;
         try {
             result = preparedStatement.executeQuery();
             if(!result.next()){
                 return null;
             }
             persistentObject = read(mapping, result);
             return persistentObject;
         } catch (SQLException e) {
             throw new PersistenceException("Error reading object from PreparedStatement", e);
         } finally {
             JdbcUtil.close(result);
         }
     }


    public Object read(IObjectMapping mapping, String sql, Collection parameters, Connection connection) throws PersistenceException {
        PreparedStatement statement = null;

        try {
            statement = JdbcUtil.prepareStatement(connection, sql);

            //todo no need to redetermine the database for each object read.
            if (this.database.isPreparedStatementParameterCountSupported()) {
                int parameterCount = JdbcUtil.parameterCount(statement);
                if(parameters.size() != parameterCount){
                    throw new PersistenceException("Parameter count in prepared statement and " +
                            "parameter collection does not match. Prepared statement has " +
                            parameterCount + " parameters (question marks). " +
                            "Parameter collection has " + parameters.size() + " parameters.");
                }
            }
            JdbcUtil.insertParameters(statement, parameters);
            return read(mapping, statement);
        } finally {
            JdbcUtil.close(statement);
        }
    }



    public Object read(IObjectMapping mapping, String sql, Object[] parameters, Connection connection) throws PersistenceException {
        PreparedStatement statement = null;

        try {
            statement = JdbcUtil.prepareStatement(connection, sql);
            if (this.database.isPreparedStatementParameterCountSupported()){
                int parameterCount = JdbcUtil.parameterCount(statement);
                if(parameters.length != parameterCount){
                    throw new PersistenceException("Parameter count in prepared statement and " +
                            "parameter collection does not match. Prepared statement has " +
                            parameterCount + " parameters (question marks). " +
                            "Parameter collection has " + parameters.length + " parameters.");
                }
            }
            JdbcUtil.insertParameters(statement, parameters);
            return read(mapping, statement);
        } finally {
            JdbcUtil.close(statement);
        }
    }

    public List readListByPrimaryKeys(IObjectMapping mapping, Collection primaryKeys, String sql, Connection connection) throws PersistenceException {
        return readListByPrimaryKeys(mapping, primaryKeys, sql, connection, AcceptAllReadFilter.ACCEPT_ALL_FILTER);
    }

    public List readListByPrimaryKeys(IObjectMapping mapping, Collection primaryKeys, String sql, Connection connection, IReadFilter filter) throws PersistenceException {
        if(primaryKeys.size() == 0){
            return new ArrayList();
        }

        if(filter == null) { filter = AcceptAllReadFilter.ACCEPT_ALL_FILTER; }

        PreparedStatement statement = null;

        try{
            statement = connection.prepareStatement(sql);

            Iterator iterator = primaryKeys.iterator();
            int index = 1;
            while(iterator.hasNext()){
                Object primaryKey = iterator.next();
                IKeyValue keyValue = null;
                if( primaryKey instanceof IKeyValue){
                    keyValue = (IKeyValue) primaryKey;
                } else if(mapping.getObjectClass().equals(primaryKey.getClass())){
                    //todo fix this so a readList method exists that reads into the primary column objects.
                    keyValue = mapping.getPrimaryKeyValueForObject(primaryKey, null);
                } else {
                    keyValue = mapping.getPrimaryKey().toKeyValue(primaryKey);
                }
                index = MappingUtil.insertPrimaryKey(mapping, keyValue, statement, index);
            }
            return readList(mapping, statement, filter);
        } catch (SQLException e){
            throw new PersistenceException("Error reading objects by primary keys (" + primaryKeys + ") using SQL(" +
                    sql + ") and object mapping (" + mapping + ")", e);
        }
        finally {
            JdbcUtil.close(statement);
        }
    }


    /*************************************
     * LIST READ METHODS BELOW
     *************************************/

    public List readList(IObjectMapping mapping, ResultSet result) throws PersistenceException {
        return readList(mapping, result, AcceptAllReadFilter.ACCEPT_ALL_FILTER);
    }


    public List readList(IObjectMapping mapping, Statement statement, String sql) throws PersistenceException {
        return readList(mapping, statement, sql, AcceptAllReadFilter.ACCEPT_ALL_FILTER);
    }


    public List readList(IObjectMapping mapping, String sql, Connection connection) throws PersistenceException {
        return readList(mapping, sql, connection, AcceptAllReadFilter.ACCEPT_ALL_FILTER);
    }


    public List readList(IObjectMapping mapping, PreparedStatement preparedStatement) throws PersistenceException {
        return readList(mapping, preparedStatement, AcceptAllReadFilter.ACCEPT_ALL_FILTER);
    }

    public List readList(IObjectMapping mapping, String sql, Collection parameters, Connection connection) throws PersistenceException {
        return readList(mapping, sql, parameters, connection, AcceptAllReadFilter.ACCEPT_ALL_FILTER);
    }

    public List readList(IObjectMapping mapping, String sql, Object[] parameters, Connection connection) throws PersistenceException {
        return readList(mapping, sql, parameters, connection, AcceptAllReadFilter.ACCEPT_ALL_FILTER);
    }


    /*************************************
     * FILTERED READ LIST METHODS BELOW
     *************************************/
    public List readList(IObjectMapping mapping, ResultSet result, IReadFilter filter) throws PersistenceException {
        List list = new ArrayList();
        if(filter == null) filter = AcceptAllReadFilter.ACCEPT_ALL_FILTER;

        try{
            filter.init(result);

            //change for MS SQL Server Driver compatibility :-(
            if(isPositionedAtRecord(result) && filter.accept(result)){
                list.add(read(mapping, result));
            }
            while(result.next() && filter.acceptMore()){
                if(filter.accept(result)){
                    list.add(read(mapping, result));
                }
            }
            return list;
        } catch (SQLException e){
            throw new PersistenceException("Error reading list of objects from ResultSet", e);
        }
    }

    //todo change to read into existing objects ... call read(..., persistentObject);
    public List readList(IObjectMapping mapping, ResultSet result, IReadFilter filter,
                         Collection persistentObjects) throws PersistenceException {
        Map persistentObjectsMap = toMap(mapping, persistentObjects);
        return readList(mapping, result, filter, persistentObjectsMap);
    }
    //todo change to read into existing objects ... call read(..., persistentObject);
    public List readList(IObjectMapping mapping, ResultSet result, IReadFilter filter, Map persistentObjects)
            throws PersistenceException {
        List list = new ArrayList();
        if(filter == null) filter = AcceptAllReadFilter.ACCEPT_ALL_FILTER;

        try{
            filter.init(result);

            //change for MS SQL Server Driver compatibility :-(
            if(isPositionedAtRecord(result) && filter.accept(result)){
                list.add(read(mapping, result));
            }
            while(result.next() && filter.acceptMore()){
                if(filter.accept(result)){
                    list.add(read(mapping, result));
                }
            }
            return list;
        } catch (SQLException e){
            throw new PersistenceException("Error reading list of objects from ResultSet", e);
        }
    }

    /**
     * This method tries to determine if the result set is positioned at a record or, before first record.
     * Unfortunately the many different implementations of JDBC drivers makes it impossible to use something
     * as simple as result.getRow() > 0 :-(
     * @param result
     * @return
     * @throws SQLException
     */
    private boolean isPositionedAtRecord(ResultSet result) throws SQLException {
        if(database.isResultSetGetRowSupported()){
            return result.getRow() > 0;
        }
        
        try {
            result.getObject(1);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }


    public List readList(IObjectMapping mapping, Statement statement, String sql, IReadFilter filter) throws PersistenceException {
        ResultSet result = null;

        try {
            result = statement.executeQuery(sql);
            return readList(mapping, result, filter);
        } catch (SQLException e) {
            throw new PersistenceException("Error reading objects from statement and sql", e);
        } finally {
            JdbcUtil.close(result);
        }
    }


    public List readList(IObjectMapping mapping, String sql, Connection connection, IReadFilter filter) throws PersistenceException {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            return readList(mapping, statement, sql, filter);
        } catch (SQLException e) {
            throw new PersistenceException("Error creating statement to read list of objects from.", e);
        }finally {
            JdbcUtil.close(statement);
        }
    }

    public List readList(IObjectMapping mapping, PreparedStatement preparedStatement, IReadFilter filter) throws PersistenceException {
        ResultSet result = null;
        try {
            result = preparedStatement.executeQuery();
            return readList(mapping, result, filter);
        } catch (SQLException e) {
           throw new PersistenceException("Error reading list of objects from PreparedStatement", e);
        } finally {
            JdbcUtil.close(result);
        }
    }

    public List readList(IObjectMapping mapping, PreparedStatement preparedStatement, IReadFilter filter,
                         Collection persistentObjects) throws PersistenceException {
        ResultSet result = null;
        try {
            result = preparedStatement.executeQuery();
            return readList(mapping, result, filter); //todo change to include persistent objects.
        } catch (SQLException e) {
           throw new PersistenceException("Error reading list of objects from PreparedStatement", e);
        } finally {
            JdbcUtil.close(result);
        }
    }

    public List readList(IObjectMapping mapping, String sql, Collection parameters, Connection connection, IReadFilter filter) throws PersistenceException {
        PreparedStatement statement = null;
        try {
            statement = JdbcUtil.prepareStatement(connection, sql);
            JdbcUtil.insertParameters(statement, parameters);
            return readList(mapping, statement, filter);
        } finally {
            JdbcUtil.close(statement);
        }
    }

    public List readList(IObjectMapping mapping, String sql, Object[] parameters, Connection connection, IReadFilter filter) throws PersistenceException {
        PreparedStatement statement = null;
        try {
            statement = JdbcUtil.prepareStatement(connection, sql);
            JdbcUtil.insertParameters(statement, parameters);
            return readList(mapping, statement, filter);
        } finally {
            JdbcUtil.close(statement);
        }
    }


   /**************************
    *   UTILTY METHODS BELOW
    **************************/

    protected Object newInstance(Class persistentObjectClass, ResultSet result) throws PersistenceException{
        Constructor constructor = null;

        try{
            constructor = persistentObjectClass.getConstructor(new Class[] {ResultSet.class});
            return createResultSetArgumentInstance(persistentObjectClass, result, constructor);
        } catch(NoSuchMethodException e){
            //checking if class has a constructor taking a ResultSet as argument.
        }

        return createNoArgumentInstance(persistentObjectClass);
    }

    public Object createNoArgumentInstance(Class persistentObjectClass) throws PersistenceException{
        try {
            return persistentObjectClass.newInstance();
        } catch (InstantiationException e) {
            throw new PersistenceException("Error occurred when trying to create " +
                    "instance of class " + persistentObjectClass.getName() + " (using no-argument constructor)", e);
        } catch (IllegalAccessException e) {
            throw new PersistenceException("Error occurred when trying to create " +
                    "instance of class " + persistentObjectClass.getName() + " (using no-argument constructor)", e);
        }
    }

    public Object createResultSetArgumentInstance(Class persistentObjectClass, ResultSet result,
                                                  Constructor constructor)
    throws PersistenceException{
        try{
            return constructor.newInstance(new Object[] {result});
        } catch (InstantiationException e) {
            throw new PersistenceException("An error occurred when invoking constructor(ResultSet) on " +
                    "instance of class: " + persistentObjectClass.getName(), e);
        } catch (IllegalAccessException e) {
            throw new PersistenceException("An error occurred when invoking constructor(ResultSet) on " +
                    "instance of class: " + persistentObjectClass.getName(), e);
        } catch (InvocationTargetException e) {
            throw new PersistenceException("An error occurred when invoking constructor(ResultSet) on " +
                    "instance of class: " + persistentObjectClass.getName(), e);
        }

    }

    private Map toMap(IObjectMapping mapping, Collection persistentObjects) throws PersistenceException {
        Map map = new HashMap();
        Iterator iterator = persistentObjects.iterator();
        while(iterator.hasNext()){
            Object persistentObject = iterator.next();
            if(mapping.getObjectClass().equals(persistentObject.getClass())){
                IKeyValue keyValue = mapping.getPrimaryKeyValueForObject(persistentObject, null);
                map.put(keyValue, persistentObject);
            }
        }

        return map;
    }




}
