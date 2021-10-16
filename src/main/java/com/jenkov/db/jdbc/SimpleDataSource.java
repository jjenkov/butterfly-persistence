package com.jenkov.db.jdbc;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.io.PrintWriter;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 * This class is still experimental. Though Butterfly Persistence will eventually get
 * some connection creation mechanism this may not be it.
 * 
 * @author Jakob Jenkov - Copyright 2005 Jenkov Development
 */
public class SimpleDataSource implements DataSource{

    private String driver   = null;
    private String url      = null;
    private String user     = null;
    private String password = null;


    /**
     * Creates a SimpleDataSource instance.
     *
     * @param driver The JDBC driver (fully qualified class name) to use.
     * @param url The JDBC database URL
     * @param user The database user name
     * @param password The database user password
     * @throws IllegalArgumentException If the driver cannot be instantiated.
     */
    public SimpleDataSource(String driver, String url, String user, String password) {
        validateDriver(driver);

        this.driver   = driver;
        this.url      = url;
        this.user     = user;
        this.password = password;
    }

    private void validateDriver(String driver) {
        try {
            Class.forName(driver).newInstance();
        } catch (InstantiationException e) {
            throw new IllegalArgumentException("Error instantiating JDBC driver: " + e.getMessage());
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Error instantiating JDBC driver: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Error instantiating JDBC driver: " + e.getMessage());
        }
    }




    /**
     * Not supported.
     * @return 0
     * @throws SQLFeatureNotSupportedException Never.
     */
    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException("getParentLogger() is not supported");
    }

    /**
     * Not supported.
     * @return 0
     * @throws SQLException Never.
     */
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    /**
     * Not supported.
     * @throws SQLException Never.
     */
    public void setLoginTimeout(int seconds) throws SQLException {

    }

    /**
     * Not supported.
     * @throws SQLException Never.
     */
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    /**
     * Not supported.
     * @throws SQLException Never.
     */
    public void setLogWriter(PrintWriter out) throws SQLException {

    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(this.url, this.user, this.password);
    }

    public Connection getConnection(String username, String password) throws SQLException {
        return DriverManager.getConnection(this.url, username, password);
    }

    public <T> T unwrap(Class<T> tClass) throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isWrapperFor(Class<?> aClass) throws SQLException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
