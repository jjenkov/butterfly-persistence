package com.jenkov.db;

import com.jenkov.db.impl.PersistenceConfiguration;
import com.jenkov.db.impl.Daos;
import com.jenkov.db.impl.init.DatabaseInitializer;
import com.jenkov.db.itf.*;
import com.jenkov.db.scope.ScopingDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * This class is the starting point of all Butterfly Persistence use. Many things are cached
 * inside the PersistenceManager, so you should reuse the same instance once you have created it. Assign it to a
 * constant, or make it a singleton, like this:
 *
 * <code>
 * public static final PERSISTENCE_MANAGER = new PersistenceManager();
 *
 * IDaos       daos = PERSISTENCE_MANAGER.createDaos(connection);
 * </code>
 *
 *
 * The PersistenceManager instance
 * should be reused throughout the application lifetime. Each application should create it's own PersistenceManager
 * instance.
 *
 * It is safe to share PersistenceManager instances if:
 * 1) The components sharing them are reading the same type of objects from the same database.
 * 2) The components sharing them are reading different types of objects from the same or different databases.
 *
 *
 * @author Jakob Jenkov - Copyright 2005 Jenkov Development
 */
public class PersistenceManager {

    private IPersistenceConfiguration        configuration        = null;


    /**
     * Creates an empty PersistenceManager instance.
     */
    public PersistenceManager(){
        init();
    }

    /**
     * Creates a PersistenceManager instance and stores the given DataSource in the default persistence configuration.
     * @param dataSource The DataSource to use internally by this PersistenceManager
     */
    public PersistenceManager(DataSource dataSource){
        init();
        this.configuration.setDataSource(dataSource);
    }

    /**
     * Creates a PersistenceManager instance and stores the given <code>DatabaseInitiazliser</code>
     * in the default persistence configuration.
     *
     * You don't have to set a <code>DatabaseInitializer</code>
     * as the persistence configuration has a default instance you can access via the
     * <code>getConfiguration().getDatabaseInitializer()</code> method call. But sometimes when configuring
     * the <code>PersistenceManager</code> via dependency injection, it can be handy to be able to configure
     * the <code>DatabaseInitializer</code> separately, and pass it to the <code>PersistenceManager</code>
     * afterwards.
     *
     * @param databaseInitializer The <code>DatabaseInitializer</code> to use in this <code>PersistenceManager</code>.
     */

    public PersistenceManager(DatabaseInitializer databaseInitializer){
        init();
        this.configuration.setDatabaseInitializer(databaseInitializer);
    }

    public PersistenceManager(DataSource dataSource, DatabaseInitializer databaseInitializer){
        init();
        this.configuration.setDataSource(dataSource);
        this.configuration.setDatabaseInitializer(databaseInitializer);
    }

    private void init(){
        configuration               = new PersistenceConfiguration(this);
    }

    /**
     * Returns the persistence configuration of this PersistenceManager.
     * @return default persistence configuration of this PersistenceManager.
     */
    public IPersistenceConfiguration getConfiguration(){
        return this.configuration;
    }

    public synchronized ScopingDataSource getScopingDataSource(){
        if(!(getDataSource() instanceof ScopingDataSource))
            throw new IllegalStateException("The DataSource set on the PersistenceManager is not a ScopingDataSource");

        return (ScopingDataSource) getDataSource();        
    }

    /**
     * Returns the <code>DataSource</code> used by this <code>PersistenceManager</code> .
     * @return the <code>DataSource</code> used by this <code>PersistenceManager</code> .
     */
    public synchronized DataSource getDataSource(){
        return this.configuration.getDataSource();
    }


    /**
     * Sets the default data source used by this PeristenceManager instance, and used by the default
     * IPersistenceConfiguration instance too.
     * @param dataSource The data source to set as default.
     */
    public synchronized void setDataSource(DataSource dataSource){
        this.configuration.setDataSource(dataSource);
    }

    /**
     * Sets the <code>DatabaseIntializer</code> to be used by this PersistenceManager.
     *
     * You don't have to set a <code>DatabaseInitializer</code>
     * as the persistence configuration has a default instance you can access via the
     * <code>getConfiguration().getDatabaseInitializer()</code> method call. But sometimes when configuring
     * the <code>PersistenceManager</code> via dependency injection, it can be handy to be able to configure
     * the <code>DatabaseInitializer</code> separately, and pass it to the <code>PersistenceManager</code>
     * afterwards.
     *
     * @param databaseInitializer The <code>DatabaseIntializer</code> to be used by this PersistenceManager.
     */

    public synchronized void setDatabaseInitializer(DatabaseInitializer databaseInitializer){
        this.configuration.setDatabaseInitializer(databaseInitializer);
    }



    //CONVENIENCE METHODS... shortcuts to doing the most common things done with the factories.

    /**
     * This method will call the <code>DatabaseInitializer.initialize()</code> method, which will
     * initialize the database. A database connection will be obtained from the <code>DataSource</code>
     * set on this <code>PersistenceManager</code> instance. The connection will be closed after
     * the initialization.
     *
     * @throws PersistenceException If anything fails during initialization of the database.
     */
    public void initializeDatabase() throws PersistenceException{
        IDaos daos = null;
        try{
            daos = createDaos();
            this.configuration.getDatabaseInitializer().initialize(daos);
        } finally {
            if(daos != null){
                try {
                    daos.getConnection().close();
                } catch (SQLException e) {
                    throw new PersistenceException("Error closing connection after initialization of database", e);
                }
            }
        }
    }


    /**
     * This method will call the <code>DatabaseInitializer.initialize()</code> method which will initialize
     * the database. The provided database connection will be used during the initialization. The database
     * connection will <b>not</b> be closed afterwards.
     *
     * @param connection The database connection to use during the database initialization. This connection
     *                   will not be closed after the initialization.
     * @throws PersistenceException If anything fails during initialization of the database.
     *
     */
    public void initializeDatabase(Connection connection) throws PersistenceException {
        IDaos daos = null;
        daos = createDaos(connection);
        this.configuration.getDatabaseInitializer().initialize(daos);
    }


    /**
     * This method removes the db_info table which contains the version number for the database. Then it
     * initializes the database. The effect is that the database is re-created from scratch. If you only
     * want to upgrade the database from one version to another (or make sure it is upgraded), call the
     * <code>initializeDatabase()</code> method instead.
     *
     * @throws PersistenceException If anything fails during reset of the database.
     *
     */
    public void resetDatabase() throws PersistenceException{
        IDaos daos = null;

        try{
            daos = createDaos();
            this.configuration.getDatabaseInitializer().reset(daos);
        } finally {
            if(daos != null){
                try {
                    daos.getConnection().close();
                } catch (SQLException e) {
                    throw new PersistenceException("Error closing connection after reset of database", e);
                }
            }
        }
    }


    /**
     * This method removes the db_info table which contains the version number for the database. Then it initializes the database.
     * The effect is that the database is re-created from scratch. If you only
     * want to upgrade the database from one version to another (or make sure it is upgraded), call the
     * <code>initializeDatabase()</code> method instead.
     *
     * @param connection The database connection to use for resetting the database.
     *
     * @throws PersistenceException If anything fails during reset of the database.
     *
     */
    public void resetDatabase(Connection connection) throws PersistenceException {
        IDaos daos = createDaos(connection);
        this.configuration.getDatabaseInitializer().reset(daos);
    }



    /** Creates an IDaos instance containing a connection obtained from the DataSource set on
     *         the configuration of this PersistenceManager.
     * @return An IDaos instance containing a connection obtained from the DataSource set on
     *         the configuration of this PersistenceManager.
     * @throws PersistenceException If opening the connection fails.
     */
    public IDaos createDaos() throws PersistenceException {
        try {
            return createDaos(configuration.getDataSource().getConnection());
        } catch (SQLException e) {
            throw new PersistenceException("Error creating IDaos instance", e);
        }
    }

    /** Creates an IDaos instance containing the connection passed as parameter.
     * @param  connection The Connection to use in the IDaos instance.
     * @return An IDaos instance containing the connection passed as parameter.
     */
    public IDaos createDaos(Connection connection){
        return new Daos(connection, getConfiguration(), this);
    }



}
