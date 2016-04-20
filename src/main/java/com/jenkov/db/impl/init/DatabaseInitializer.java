package com.jenkov.db.impl.init;

import com.jenkov.db.itf.IDaos;
import com.jenkov.db.itf.PersistenceException;
import com.jenkov.db.itf.init.IDatabaseInitialization;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;

/**

 */
public class DatabaseInitializer {
    protected DatabaseVersionDeterminer databaseVersionDeterminer = new DatabaseVersionDeterminer();

    protected List<IDatabaseInitialization> initializations = new ArrayList<IDatabaseInitialization>();

    public DatabaseInitializer() {
        this.initializations.add(new DbInfoTableInitialization());
    }

    /**
     * Adds a database initialization to the <code>DatabaseInitializer</code>'s list of initializations.
     * @param intialization The database initialization to add.
     */
    public void add(IDatabaseInitialization intialization){
        this.initializations.add(intialization);
    }

    /**
     * Adds an SqlInitialization with the given version number and SQL string
     * @param version The version of the database that this initialization belongs to.
     * @param sql     The SQL to initialize the database with.
     */
    public void add(int version, String sql){
        add(new SqlInitialization(version, sql));
    }


    /**
     * Adds all database initializations in the list given as parameter to the internal list of database initializations.
     * @param initializationList The list of database initializations to add.
     */
    public void addAll(List<IDatabaseInitialization> initializationList){
        this.initializations.addAll(initializationList);
    }

    /**
     * Executes the intializations kept internally in the initialization list. Only those database initializations
     * that have a version number greater than the version number of the database will be executed.
     *
     * @param daos The IDaos instance used to execute the database initializations with.
     */
    public void initialize(IDaos daos) throws PersistenceException{
        int databaseVersion = this.databaseVersionDeterminer.determineDatabaseVersion(daos);
        int finalDatabaseVersion = databaseVersion;

        Collections.sort(this.initializations, new Comparator<IDatabaseInitialization>(){
            public int compare(IDatabaseInitialization o1, IDatabaseInitialization o2) {
                return o1.getVersion() - o2.getVersion();
            }
        });

        for(IDatabaseInitialization initialization : initializations){
            if(databaseVersion < initialization.getVersion()){
                try {
                    initialization.execute(daos);
                } catch(PersistenceException e){
                    throw e;
                }
                catch (Exception e) {
                    throw new PersistenceException("Error executing database initialization", e);
                }
                finalDatabaseVersion = initialization.getVersion();
            }
        }

        daos.getJdbcDao().update("update db_info set value='" + finalDatabaseVersion + "' where name='version'");
    }

    /**
     * This method removes the <code>db_info</code> table which contains the version number for the database. Then it calls
     * <code>initialize()</code>. The effect is that the database is re-created from scratch. If you only
     * want to upgrade the database from one version to another (or make sure it is upgraded), call the
     * <code>initialize()</code> method instead.
     *
     * @param daos The IDaos instance to use for resetting the database. 
     */
    public void reset(IDaos daos) throws PersistenceException{
        if(databaseVersionDeterminer.tableExists(daos, "db_info")){
            daos.getJdbcDao().update("drop table db_info");
        }

        initialize(daos);
    }

}
