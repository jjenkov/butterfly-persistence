/**
 * User: Administrator
 */
package com.jenkov.db.test;

import com.jenkov.db.itf.Database;
import com.jenkov.db.util.JdbcUtil;
import com.jenkov.db.jdbc.SimpleDataSource;
//import com.jenkov.db.jdbc.pool.PoolingDataSource;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class Environment {


    public static final String DELETE_PERSISTENT_OBJECT = "delete from persistent_object";


    public static final String MYSQL               = "database/mysql.properties";
    public static final String FIREBIRD            = "database/firebird.properties";
    public static final String POSTGRESQL          = "database/postgresql.properties";
    public static final String HSQLDB_IN_PROCESS   = "database/hsqldb_in_process.properties";
    public static final String HSQLDB_SERVER       = "database/hsqldb_server.properties";
    public static final String H2_IN_PROCESS       = "database/h2_in_process.properties";
    public static final String H2_SERVER           = "database/h2_server.properties";
    public static final String DERBY_IN_PROCESS    = "database/derby_in_process.properties";
    public static final String DERBY_SERVER        = "database/derby_server.properties"; //todo note - this file does not exist!

//    public static String DATABASE = MYSQL;
//    public static String DATABASE = FIREBIRD;
//    public static String DATABASE = POSTGRESQL;
//    public static String DATABASE = MSSQLSERVER;
//    public static String DATABASE = HSQLDB_IN_PROCESS;
//    public static String DATABASE = HSQLDB_SERVER;
   public static String DATABASE = H2_IN_PROCESS;
//   public static String DATABASE = H2_SERVER;

    
    public static Database database = null;
    private static Properties dbProperties = null;

    private static DataSource datasource        = null;
    private static DataSource poolingDataSource = null;



    public static boolean isDerby(){
        return Environment.DATABASE.equals(DERBY_IN_PROCESS) ||
               Environment.DATABASE.equals(DERBY_SERVER);
    }
    
    public static boolean isHsqldb(){
        return Environment.DATABASE.equals(HSQLDB_IN_PROCESS) ||
               Environment.DATABASE.equals(HSQLDB_SERVER);
    }

    public static boolean isH2(){
        return Environment.DATABASE.equals(H2_IN_PROCESS) ||
               Environment.DATABASE.equals(H2_SERVER);
    }

    public static boolean isFirebird(){
        return Environment.DATABASE.equals(FIREBIRD);
    }

    public static boolean isPostgreSQL(){
        return Environment.DATABASE.equals(POSTGRESQL);
    }

    public static synchronized Properties getDbProperties() {
        if(System.getProperty("db.db.propertyfile") != null){
            DATABASE = System.getProperty("db.db.propertyfile");
        }
        if(dbProperties == null){
            dbProperties = new Properties();
            try {
                dbProperties.load(new FileInputStream(new File(DATABASE)));
            } catch (IOException e) {
                throw new IllegalStateException("Error loading db properties", e);
            }
        }
        return dbProperties;
    }

    public synchronized static DataSource getDataSource(){
        if(datasource == null){
            Properties properties = getDbProperties();
            datasource = new SimpleDataSource(
                    properties.getProperty("db.driverClass"),
                    properties.getProperty("db.url"),
                    properties.getProperty("db.user"),
                    properties.getProperty("db.password"));

        }
        return datasource;
    }

    /*
    public synchronized static DataSource getPoolingDataSource(){
        Properties properties = getDbProperties();
        if(poolingDataSource == null){
            try {
                poolingDataSource = new PoolingDataSource(
                        properties.getProperty("db.driverClass"),
                        properties.getProperty("db.url"),
                        properties.getProperty("db.user"),
                        properties.getProperty("db.password"));
            } catch (SQLException e) {
                throw new IllegalStateException("Error instantiating PoolingDataSource", e);
            }
        }
        return poolingDataSource;
    }
    */



    public static Connection getConnection()
            throws IllegalAccessException, SQLException, InstantiationException, ClassNotFoundException, IOException {

        Properties properties = getDbProperties();
        Connection connection = JdbcUtil.getConnection(
                properties.getProperty("db.driverClass"),
                properties.getProperty("db.url"),
                properties.getProperty("db.user"),
                properties.getProperty("db.password"));

        if(database == null){
            database = Database.determineDatabase(connection);
        }

        return connection;
    }

    public static void executeSql(String sql) throws Exception{
        Statement statement = null;
        Connection connection = null;

        try{
            connection = Environment.getConnection();
            statement = connection.createStatement();
            statement.execute(sql);
        } finally {
            JdbcUtil.close(statement);
            JdbcUtil.close(connection);
        }
    }

    public static void executeSql(String sql, Connection connection) throws Exception{
        Statement statement = null;

        try{
            statement = connection.createStatement();
            statement.execute(sql);
        } finally {
            JdbcUtil.close(statement);
        }
    }

}
