package com.jenkov.db.itf;

import com.jenkov.db.itf.IObjectDao;
import com.jenkov.db.itf.IPersistenceConfiguration;
import com.jenkov.db.itf.IJdbcDao;
import com.jenkov.db.itf.IMapDao;

import java.sql.Connection;
import java.sql.ResultSet;

/**
 * todo JavaDoc this interface.
 * @author Jakob Jenkov - Copyright 2005 Jenkov Development
 */
public interface IDaos {

    /**
     * Returns the connection used by this IDaos instance, and by all the DAO's returned by this IDaos instance.
     * @return The connection used by this IDaos instance, and by all the DAO's returned by this IDaos instance.
     */
    public Connection                getConnection   (                                         );

    /**
     * Returns the persistence configuration used by the PersistenceManager where this IDaos came from.
     * Some of the DAO's returned by this IDaos instance also use this persistence configuration.
     * Most of the time you will not need to access the persistence configuration.
     *
     * @return The persistence configuration used by the PersistenceManager where this IDaos came from.
     */
    public IPersistenceConfiguration getConfiguration(                                         );


    /**
     * Returns an IObjectDao instance. If this method is called more than once, the same IObjectDao
     * instance is returned.
     *
     * @return An IObjectDao instance.
     */
    public IObjectDao                getObjectDao    (                                         );

    /**
     * Returns an IJdbcDao instance. If this method is called more than once, the same IJdbcDao
     * instance is returned.
     *
     * @return An IJdbcDao instance.
     */
    public IJdbcDao                  getJdbcDao      (                                         );

    /**
     * Returns an IMapDao instance. If this method is called more than once, the same IMapDao
     * instance is returned.
     *
     * @return An IMapDao instance.
     */
    public IMapDao                   getMapDao       (                                         );

//    public ResultSetView             getResultSetView(Object objectMappingKey, ResultSet result) throws PersistenceException;

    /**
     * Closes the connection used by this IDaos instance. If closing the connection fails
     * a PersistenceException is thrown (this exception is unchecked).
     */
    public void                      closeConnection() throws PersistenceException;

}
