package com.jenkov.db;

import com.jenkov.db.itf.IPersistenceConfiguration;
import com.jenkov.db.itf.PersistenceException;
import com.jenkov.db.itf.mapping.IObjectMapping;
import com.jenkov.db.util.JdbcUtil;
import com.jenkov.db.impl.PersistenceConfiguration;

import java.util.Properties;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * The MappingTool class is a method utility that can show how the
 * Butterfly Persistence API will map a given class to a database. This
 * can be useful to see if your class will be mapped correctly
 * by the API, or you will have to adjust the mapping manually.
 *
 * <br/><br/>
 * The MappingTool utility can be run from commmand line, or be instantiated
 * and run from within an application.
 *
 * <br/><br/>
 * The method tool needs a database property file in order to connect to the
 * database. There has to be the following 4 properties present in the file:
 * <br/><br/>
 *  <ol> <li><code>db.driver=xxx</code> The name of the JDBC driver class used to connect
 *        to the database</li>
 *       <li><code>db.url</code> The URL to the database server used to connect to the database.</li>
 *       <li><code>db.user</code> The database user name to use to connect to the database.</li>
 *       <li><code>db.password</code> The password of the database user used to connect to the database</li>
 *  </ol>
 *
 * @author Jakob Jenkov, Jenkov Development
 */
public class MappingTool implements Runnable{

    private static final String DB_DRIVER   = "db.driver";
    private static final String DB_URL      = "db.url";
    private static final String DB_USER     = "db.user";
    private static final String DB_PASSWORD = "db.password";



    protected File   databasePropertyFile = null;
    protected Class  classToMap           = null;
    protected String tableName            = null;


    /**
     * Initializes the MappingTool instance with arguments passed from the command line.
     * @param args The arguments as passed to the application from the command line,
     *             in the <code>main(String[] args)</code> method.
     * @throws PersistenceException If the arguments are not valid.
     */
    public MappingTool(String args[]) throws PersistenceException{
        printInfo();
        if(!validateArgs(args)) {
            throw new PersistenceException("Mapping Tool aborted.");
        }

        this.databasePropertyFile = new File(args[0]);
        this.tableName            = args.length > 2 ? args[2] : null;

        try {
            this.classToMap = Class.forName(args[1]);
        } catch (ClassNotFoundException e) {
            System.out.println("Could not find class: " + args[1]);
            System.out.println("Make sure the class is in your classpath\n");
            e.printStackTrace();
            throw new PersistenceException("Mapping Tool aborted.");
        }

    }

    /**
     * Initializes the MappingTool instance with the database property file <code>File</code> instance,
     * and the <code>Class</code> instance of the class to be mapped.
     * @param databasePropertyFile The database property file.
     * @param theClass             The class to be mapped.
     */
    public MappingTool(File databasePropertyFile, Class theClass){
        printInfo();
        this.databasePropertyFile = databasePropertyFile;
        this.classToMap           = theClass;
    }

    /**
     * Initializes the MappingTool instance with the database property file, the class to be mapped,
     * and the name of the table to map the class to.
     * @param databasePropertyFile The database property file
     * @param theClass             The class to be mapped to the database table.
     * @param tableName            The name of the table to map the class to.
     */
    public MappingTool(File databasePropertyFile, Class theClass, String tableName){
        printInfo();
        this.databasePropertyFile = databasePropertyFile;
        this.classToMap           = theClass;
        this.tableName            = tableName;
    }


    /**
     * Validates the properties found in the property file. There has to be the
     * following 4 properties present in the file:
     * <br/><br/>
     *  <ol> <li><code>db.driver=xxx</code> The name of the JDBC driver class used to connect
     *        to the database</li>
     *       <li><code>db.url</code> The URL to the database server used to connect to the database.</li>
     *       <li><code>db.user</code> The database user name to use to connect to the database.</li>
     *       <li><code>db.password</code> The password of the database user used to connect to the database</li>
     *  </ol>
     * @param properties   The <code>Properties</code> loaded from the database property file.
     * @throws PersistenceException If the property file did not contain all of the required keys.
     */
    private void validateProperties(Properties properties) throws PersistenceException{
        if(properties.getProperty(DB_DRIVER) == null){
            throw new PersistenceException("Property file did not contain value for key:   " + DB_DRIVER);
        }
        if(properties.getProperty(DB_URL) == null){
            throw new PersistenceException("Property file did not contain value for key:   " + DB_URL);
        }
        if(properties.getProperty(DB_USER) == null){
            throw new PersistenceException("Property file did not contain value for key:   " + DB_USER);
        }
        if(properties.getProperty(DB_PASSWORD) == null){
            throw new PersistenceException("Property file did not contain value for key:   " + DB_PASSWORD);
        }
    }

    /**
     * Opens a connection to the database to be used with the object mapper.
     * @param properties  The <code>Properties</code> instance containing the database properties
     * @return A connection to the database stated in the database property file.
     * @throws IllegalAccessException If an instance of the database driver class could not be created.
     * @throws SQLException If something goes wrong during the opening of the connection.
     * @throws InstantiationException If an instance of the database driver class could not be created.
     * @throws ClassNotFoundException If the class of the database driver could not be found.
     */
    private Connection getConnection(Properties properties) throws IllegalAccessException, SQLException, InstantiationException, ClassNotFoundException {
          return JdbcUtil.getConnection(
                  properties.getProperty(DB_DRIVER),
                  properties.getProperty(DB_URL),
                  properties.getProperty(DB_USER),
                  properties.getProperty(DB_PASSWORD));

    }


    /**
     * Prints an info header stating what this tool, copyright etc.
     */
    private void printInfo() {
        System.out.println("");
        System.out.println("========================================================");
        System.out.println(" Butterfly Persistence Mapping Tool                             ");
        System.out.println(" Copyright 2004   Jenkov Development                    ");
        System.out.println(" www.jenkov.com / www.jenkov.dk                         ");
        System.out.println("========================================================");
    }

    /**
     * This method does the hard work. Opens a connection to the database and
     * asks the object mapper to map the class to the database, and prints the results.
     */
    public void run() {
        Connection connection   = null;
        Properties properties   = null;

        //loading and verifying database properties
        try {
            properties = new Properties();
            properties.load(new FileInputStream(this.databasePropertyFile));
            validateProperties(properties);
        } catch (IOException e) {
            System.out.println("Error: Could not find or read file " + this.databasePropertyFile.getAbsolutePath());
            e.printStackTrace();
            return;
        } catch (PersistenceException e){
            System.out.println("Error: The database property file did not contain all required keys:");
            e.printStackTrace();
            return;
        }

        //opening connection to database
        try {
            connection = getConnection(properties);
        } catch (Exception e) {
            System.out.println("Error: Could not open connection to database: \n");
            e.printStackTrace();
            return;
        }


        IPersistenceConfiguration configuration = new PersistenceConfiguration(null);
                ;

        try {
            IObjectMapping mapping = configuration.getObjectMapper().mapToTable(
                this.classToMap, null, connection, null, this.tableName);

            System.out.println("");
            System.out.println("Mapping Suggestion:");
            System.out.println("===================");
            System.out.println(mapping.toString());
        } catch (PersistenceException e) {
            System.out.println("Error: Could not map class to database table: ");
            e.printStackTrace();
            return;
        }
    }

    /**
     * Validates the arguments passed to the MappingTool application from the
     * command line.
     * @param args  The arguments passed to the MappingTool application from the
     * command line.
     * @return True if the arguments are ok. False if not
     * (for instance if any required arguments are missing).
     */
    private boolean validateArgs(String[] args) {
        if(args.length < 1){
            System.out.println("Error: No database property file specified (first argument)");
            printUsage();
            return false;
        }

        if(args.length < 2){
            System.out.println("Error: No class name specified (second argument)");
            printUsage();
            return false;
        }
        return true;
    }


    /**
     * Prints how to use the MappingTool class from the command line.
     */
    private void printUsage() {
        System.out.println("\nUsage:\n");
        System.out.println("java   -classpath classpath   com.jenkov.db.MappingTool\n" +
                           "        propertyFile   className   [tableName]");
        System.out.println("");
        System.out.println("   propertyFile - The database property file.");
        System.out.println("   className    - The name of the class to auto map to the database/table.");
        System.out.println("   [tableName]  - Optional, the name of the table to map the class to,\n" +
                           "                  if the table name is not similar to the class name.");
        System.out.println("");
        System.out.println("The database property file should contain the following 4 key/value pairs:\n");
        System.out.println("   " + DB_DRIVER   + "=XXX   (XXX is the class name of the JDBC driver)");
        System.out.println("   " + DB_URL      + "=YYY         (YYY is the URL to the database server to connect to)");
        System.out.println("   " + DB_USER     + "=UUU        (UUU is the user name to use to connect to the database)");
        System.out.println("   " + DB_PASSWORD + "=ZZZ    (ZZZ is the password of the user used to connect to the\n" +
                           "                       database)");
        System.out.println("");
    }


    /**
     * Runs the MappingTool application from the command line.
     * @param args The arguments passed to the MappingTool class from the command line.
     * The MappingTool class requires two arguments and has an additional optional argument:
     *
     * <br/><br/>
     * <ol>
     *   <li>propertyFile - The path to the database property file</li>
     *   <li>className - The fully qualified name of the class to map</li>
     *   <li>tableName - Optional. The table name to map the class to, in case the
     *        table name is not similar to the class name</li>
     * </ol>
     */
    public static void main(String[] args){

        try {
            MappingTool mappingTool  = new MappingTool(args);
            mappingTool.run();
        } catch (PersistenceException e) {
            System.out.println(e.getMessage());
        }
    }




}


