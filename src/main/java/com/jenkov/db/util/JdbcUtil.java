package com.jenkov.db.util;

import com.jenkov.db.itf.PersistenceException;
import com.jenkov.db.itf.IObjectDao;
import com.jenkov.db.impl.mapping.method.AsciiStream;
import com.jenkov.db.impl.mapping.method.CharacterStream;

import java.sql.*;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;

public class JdbcUtil {

    /**
     * Closes the connection enclosed in the given <code>IGenericDao</code> instance.
     * If the dao parameter is null, or its enclosed connection reference is null,
     * nothing happens.
     * @param dao The dao to close the enclosed connection of.
     * @throws PersistenceException If an error occurs when closing the connection.
     */
    public static void close(IObjectDao dao) throws PersistenceException{
        if(dao != null){
            close(dao.getConnection());
        }
    }

    /**
     * Closes the connection enclosed in the given <code>IGenericDao</code> instance.
     * If the dao parameter is null, or its enclosed connection reference is null,
     * nothing happens. Any exceptions thrown while closing the connection are ignored.
     * @param dao The dao to close the enclosed connection of.
     */
    public static void closeIgnore(IObjectDao dao){
        if(dao != null){
            try {
                close(dao.getConnection());
            } catch (PersistenceException e) {
                //ignore.
            }
        }
    }



    /**
     * Opens a connection to the specified database using the JDBC driver class ,
     * the url, the user name, and the password provided.
     * @param driverClass The name of the JDBC driver class.
     * @param url         The URL to the database server / JDBC connection URL.
     * @param user        The user name to be used to connect to the database.
     * @param password    The password of the user to be used to connect to the database.
     * @return A connection to the database if it could be opened.
     * @throws ClassNotFoundException If the JDBC driver class could not be found (is not on the classpath)
     * @throws SQLException If something goes wrong during the connection, for instance if the
     *                    database/catalogue name in the URL is not recognized by the database server.
     * @throws IllegalAccessException If your application does not have permission to
     *                    instantiate the database driver.
     * @throws InstantiationException If an instance of the JDBC driver class could not be created.
     */
    public static Connection getConnection(String driverClass, String url, String user, String password)
    throws ClassNotFoundException, SQLException, IllegalAccessException, InstantiationException {
        Class.forName(driverClass).newInstance();
        return DriverManager.getConnection(url, user, password);
    }


    /**
     * Closes the provided connection, if it is not already closed. If you pass a null connection, nothing
     * happens.
     * @param connection The connection to close.
     * @throws PersistenceException If an SQLException is thrown when calling
     *        <code>connection.close()</code>
     */
    public static void close(Connection connection) throws PersistenceException{
        try {
            if(connection != null){
                if(!connection.isClosed()){
                    connection.close();
                }
            }
        } catch (SQLException e) {
            throw new PersistenceException("Error closing Connection", e);
        }
    }

    /**
     * Closes the provided connection. If you provide a null connection nothing happens.
     * Any exceptions thrown when closing the connection are ignored.
     * @param connection The connection to close.
     */
    public static void closeIgnore(Connection connection){
        try {
            if(connection != null){
                if(!connection.isClosed()){
                    connection.close();
                }
            }
        } catch (SQLException e) {
        }
    }


    /**
     * Closes the provided <code>Statement</code> or <code>PreparedStatement</code>.
     * If you provide a null statement nothing happens.
     * @param  statement The <code>Statement</code> or <code>PreparedStatement</code>
     *         to close.
     * @throws PersistenceException If an SQLException is thrown when calling
     *        <code>statement.close()</code>
     */
    public static void close(Statement statement) throws PersistenceException{
        try {
            if(statement != null){
                statement.close();
            }
        } catch (SQLException e) {
            throw new PersistenceException("Error closing Statement", e);
        }
    }

    /**
     * Closes the provided <code>Statement</code> or <code>PreparedStatement</code>.
     * If you provide a null statement nothing happens.
     * Any exceptions thrown while closing the <code>Statement</code> or
     * <code>PreparedStatement</code> are ignored.
     * @param  statement The <code>Statement</code> or <code>PreparedStatement</code>
     *         to close.
     */
    public static void closeIgnore(Statement statement){
        try {
            if(statement != null){
                statement.close();
            }
        } catch (SQLException e) {
        }
    }


    /**
     * Closes the provided <code>ResultSet</code>. If you provide a null result set nothing happens.
     * @param result The <code>ResultSet</code> to close.
     * @throws PersistenceException If an SQLException is thrown when calling
     *        <code>result.close()</code>
     */
    public static void close(ResultSet result) throws PersistenceException{
        try {
            if(result != null) result.close();
        } catch (SQLException e) {
            throw new PersistenceException("Error closing ResulSet", e);
        }
    }


    /**
     * Closes the provided <code>ResultSet</code>. If you provide a null result set nothing happens.
     * Any exceptions thrown when closing
     * the <code>ResultSet</code> are ignored.
     * @param result The <code>ResultSet</code> to close.
     */
    public static void closeIgnore(ResultSet result){
        try {
            if(result != null){
                result.close();
            }
        } catch (SQLException e) {
        }
    }
    




    /**
     * Closes the <code>ResultSet</code>, then the <code>Statement</code> or
     * <code>PreparedStatement</code>, and finally the <code>Connection</code>
     * in the same sequence as mentioned here. If any of the parameters are null they
     * will be ignored (not attempted closed).
     *
     * @param connection  The <code>Connection</code> to close.
     * @param statement   The <code>Statement</code> or <code>PreparedStatement</code> to close.
     * @param result      The <code>ResultSet</code> to close.
     * @throws PersistenceException If one or more SQLExceptions are thrown when closing
     *                    the result set, statement or connection. The error messages from
     *                    all thrown exceptions are collected and included in the one
     *                    PersistenceException that is thrown.
     */
    public static void close(Connection connection, Statement statement, ResultSet result)
    throws PersistenceException {
        StringBuffer errorText = new StringBuffer(250);

        try {
            if(result != null){
                result.close();
            }
        } catch (SQLException e) {
            errorText.append("Error: Could not close ResultSet: " + e.toString() + "\n");
        }

        try {
            if(statement != null) {
                statement.close();
            }
        } catch (SQLException e) {
            errorText.append("Error: Could not close Statement: " + e.toString() + "\n");
        }

        try {
            if(connection != null){
                connection.close();
            }
        } catch (SQLException e) {
            errorText.append("Error: Could not close Connection: " + e.toString() + "\n");
        }

        if(errorText.length() > 0){
            throw new PersistenceException(errorText.toString());
        }
    }

    /**
     * Closes the <code>ResultSet</code>, then the <code>Statement</code> or
     * <code>PreparedStatement</code>, and finally the <code>Connection</code>
     * in the same sequence as mentioned here. If any of the parameters are null they
     * will be ignored (not attempted closed). All exceptions thrown are ignored.
     *
     * @param connection  The <code>Connection</code> to close.
     * @param statement   The <code>Statement</code> or <code>PreparedStatement</code> to close.
     * @param result      The <code>ResultSet</code> to close.
     */
    public static void closeIgnore(Connection connection, Statement statement, ResultSet result){
        try {
            close(connection, statement, result);
        } catch (PersistenceException e) {
        }
    }

    /**
     * Inserts a parameter into a <code>PreparedStatement</code> on the given index. This
     * method will try to determine what class the parameter is instance of,
     * and call the coresponding setter method on the <code>PreparedStatement</code>.
     * @param statement      The <code>PreparedStatement</code> to insert the parameter into.
     * @param parameter      The parameter to be inserted.
     * @param index          The index of the parameter in the <code>PreparedStatement</code>.
     * @throws PersistenceException  If anything goes wrong when calling the setter method on
     *                       the <code>PreparedStatement</code> instance.
     * @throws IllegalArgumentException If the parameter class is not supported.
     */
    public static void insertParameter(PreparedStatement statement, int index, Object parameter) throws
            PersistenceException{

        //todo full unit test of this method. Only unit tested through ObjectDao.executeUpdate(PreparedStatement...)

//        if(parameter == null){
//            throw new NullPointerException("parameter with index " + index + " was null.");
//        }

        //most used parameter types in databases
        try {
            if(parameter == null)                        statement.setNull      (index, java.sql.Types.NULL);
            else if(parameter instanceof String  )       statement.setString    (index, (String) parameter);
            else if(parameter instanceof Integer)        statement.setInt       (index, ((Integer)parameter).intValue());
            else if(parameter instanceof Long   )        statement.setLong      (index, ((Long)   parameter).longValue());
            else if(parameter instanceof BigDecimal)     statement.setBigDecimal(index, (BigDecimal) parameter);
            else if(parameter instanceof Date)           statement.setDate      (index, (Date) parameter);
            else if(parameter instanceof Timestamp )     statement.setTimestamp (index, (Timestamp) parameter);
            else if(parameter instanceof Time    )       statement.setTime      (index, (Time) parameter);
            else if(parameter instanceof java.util.Date) statement.setTimestamp (index, new Timestamp(((java.util.Date)parameter).getTime()));

            //less used parameter types
            else if(parameter instanceof Boolean)        statement.setBoolean(index, ((Boolean)parameter).booleanValue());
            else if(parameter instanceof Byte   )        statement.setByte   (index, ((Byte)   parameter).byteValue());
            else if(parameter instanceof byte[] )        statement.setBytes  (index, (byte[])  parameter);
            else if(parameter instanceof Double )        statement.setDouble (index, ((Double) parameter).doubleValue());
            else if(parameter instanceof Float  )        statement.setFloat  (index, ((Float)  parameter).floatValue());
            else if(parameter instanceof Short  )        statement.setShort  (index, ((Short)  parameter).shortValue());
            else if(parameter instanceof URL       )     statement.setURL    (index, (URL) parameter);


            //least used parameter types
            else if(parameter instanceof Blob       )    statement.setBlob   (index, (Blob) parameter);
            else if(parameter instanceof Clob       )    statement.setClob   (index, (Clob) parameter);

            else if(parameter instanceof Array  )        statement.setArray  (index, (Array)   parameter);

            else if(parameter instanceof AsciiStream) {
                statement.setAsciiStream(index, ((AsciiStream) parameter).getInputStream(),
                        ((AsciiStream) parameter).getLength());
            }
            else if(parameter instanceof CharacterStream) {
                statement.setCharacterStream(index, ((CharacterStream) parameter).getReader(),
                        ((CharacterStream) parameter).getLength());
            }
            else if(parameter instanceof Ref     )       statement.setRef    (index, (Ref) parameter);
            else if(parameter instanceof Object  )       statement.setObject (index, parameter);
            else {
                throw new IllegalArgumentException("Member type not supported: " + parameter.getClass().getName());
            }
        } catch (SQLException e) {
            throw new PersistenceException("Error inserting parameter " + index + " (" + parameter + ")"
                    + " into prepared statement " + statement, e);
        }
    }

    /**
     * Inserts all parameters in the collection into the <code>PreparedStatement</code>
     * instance in the sequence their are returned by the collection's iterator.
     * @param statement The <code>PreparedStatement</code> to insert the parameters into.
     * @param parameters The parameters to insert.
     * @throws PersistenceException If anything goes wrong during the insertion of the parameters.
     */
    public static void insertParameters(PreparedStatement statement, Collection parameters) throws PersistenceException {
        Iterator iterator = parameters.iterator();

        for(int i=0; iterator.hasNext(); i++){
            insertParameter(statement, i + 1, iterator.next());
        }
    }

    /**
     * Inserts all parameters in the array into the <code>PreparedStatement</code>
     * instance in the sequence their are located in the array.
     * @param statement The <code>PreparedStatement</code> to insert the parameters into
     * @param parameters The parameters to insert.
     * @throws PersistenceException If anything goes wrong during the insertion of the parameters.
     */
    public static void insertParameters(PreparedStatement statement, Object[] parameters) throws PersistenceException {
        for(int i=0; i < parameters.length; i++){
            insertParameter(statement, i + 1, parameters[i]);
        }
    }

    public static PreparedStatement prepareStatement(Connection connection, String sql) throws PersistenceException{
        try {
            return connection.prepareStatement(sql);
        } catch (SQLException e) {
            throw new PersistenceException("Error preparing statement. Sql: " + sql, e);
        }
    }

    public static int parameterCount(PreparedStatement statement) throws PersistenceException{
        try {
            return statement.getParameterMetaData().getParameterCount();
        } catch (SQLException e) {
            throw new PersistenceException("Error retrieving parameter count for prepared statement: "
                    + statement, e);
        }
    }

}
