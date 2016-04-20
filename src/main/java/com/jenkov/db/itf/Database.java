package com.jenkov.db.itf;

import com.jenkov.db.jdbc.SimpleDataSource;

import javax.sql.DataSource;
import java.sql.*;


/**
 *
 * Signals the type of database a given IPersistenceConfiguration is targeted against.
 * The database type can be used to avoid calling JDBC methods that the given database
 * does not implement, or implements slightly differently than other databases etc.
 *
 * @author Jakob Jenkov - Copyright 2005 Jenkov Development
 */
public class Database {

    public static final Database DEFAULT    = new Database("Default - JDBC Compliant Database", true, true, true);
    public static final Database DAFFODIL   = new Database("DaffodilDB"          , true, true, true);   //true, false, true ??
    public static final Database DERBY      = new Database("Apache Derby"        , true, true, false);
    public static final Database HSQLDB     = new Database("HSQL Database Engine", true, false, true);
    public static final Database H2         = new Database("H2"                  , false, true, true);
    public static final Database MYSQL      = new Database("MySQL"               , true, true, true);
    public static final Database POSTGRESQL = new Database("PostgreSQL"          , true, false, true);
    public static final Database FIREBIRD   = new Database("Firebird"            , true, false, true);

    //todo add static factory method that can determine the type of a database from a connection.
    protected String  name                                                         = null;
    protected boolean isPreparedStatementParameterCountSupported                   = true;
    protected boolean isPrepareStatementStatement_RETURN_GENERATED_KEYS_supported  = true;
    protected boolean isResultSetGetRowSupported                                   = true;

    public Database(String name, boolean preparedStatementParameterCountSupported, boolean prepareStatementStatement_RETURN_GENERATED_KEYS_supported, boolean resultSetGetRowSupported) {
        this.name = name;
        isPreparedStatementParameterCountSupported = preparedStatementParameterCountSupported;
        isPrepareStatementStatement_RETURN_GENERATED_KEYS_supported = prepareStatementStatement_RETURN_GENERATED_KEYS_supported;
        isResultSetGetRowSupported = resultSetGetRowSupported;
    }

    public String getName(){
        return this.name;
    }

    public boolean isPreparedStatementParameterCountSupported() {
        return isPreparedStatementParameterCountSupported;
    }

    public boolean isPrepareStatementStatement_RETURN_GENERATED_KEYS_supported() {
        return isPrepareStatementStatement_RETURN_GENERATED_KEYS_supported;
    }

    public boolean isResultSetGetRowSupported() {
        return isResultSetGetRowSupported;
    }


    public String toString() {
        return this.name;
    }

    public boolean equals(Object o){
        return o == this;
    }


    public static void  setDatabaseOnConfiguration(IPersistenceConfiguration configuration, Connection connection){
        synchronized(configuration){
            if(configuration.getDatabase() == null){
                configuration.setDatabase(determineDatabase(connection));
            }
        }
    }

    public static Database determineDatabase(Connection connection){
        String databaseName = "JDBC-Compliant";
        try {
            databaseName = connection.getMetaData().getDatabaseProductName();
//            System.out.println("databaseName = " + databaseName);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(databaseName == null) return DEFAULT;

        if("Apache Derby"        .equals(databaseName)) return DERBY;
        if("DaffodilDB"          .equals(databaseName)) return DAFFODIL;
        if("HSQL Database Engine".equals(databaseName)) return HSQLDB;
        if("H2"                  .equals(databaseName)) return H2;
        if("MySQL"               .equals(databaseName)) return MYSQL;
        if("PostgreSQL"          .equals(databaseName)) return POSTGRESQL;
        if(databaseName.startsWith("Firebird")) return FIREBIRD;

        return DEFAULT;
    }


    ///CLOVER:OFF
    public static void main(String[] args) throws SQLException {
        if("--".equals(args[2])){
            args[2] = "";
        }
        if("--".equals(args[3])){
            args[3] = "";
        }

        DataSource dataSource = new SimpleDataSource(args[0], args[1], args[2], args[3]);
        Connection connection = dataSource.getConnection();
        Database database = Database.determineDatabase(connection);
        System.out.println("Database name = '" + database + "'");

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement("select * from persistent_object where id=? and name=?");
            System.out.println("Parameter Count = " + statement.getParameterMetaData().getParameterCount());
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            statement = connection.prepareStatement("select * from persistent_object where id=? and name=?", Statement.RETURN_GENERATED_KEYS);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
//            statement = connection.prepareStatement("insert into persistent_object (id, name, objectValue) values (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
//            statement.setLong  (1, 1);
//            statement.setString(2, "lalala-name");
//            statement.setString(3, "lalala-object-value");
            statement = connection.prepareStatement("insert into persistent_object (name, objectValue) values (?, ?)", Statement.RETURN_GENERATED_KEYS);
//            statement = connection.prepareStatement("insert into persistent_object (name, objectValue) values (?, ?)");
            statement.setString(1, "lalala-name");
            statement.setString(2, "lalala-object-value");
            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            while(generatedKeys.next()){
                System.out.println(generatedKeys.getObject(1));
            }
            generatedKeys.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        connection.close();

    }



}
