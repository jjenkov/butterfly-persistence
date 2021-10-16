package com.jenkov.db.itf;

import com.jenkov.db.itf.IDaos;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Represents a processor capable of initializing and processing a
 * ResultSet, and returning a result afterwards.
 *
 * In most cases it is easiest to extend the ResultSetProcessorBase
 * which has empty implementations for all methods. Then you only
 * have to override the methods you need special behaviour in.
 * Typically that will be the process method.
 *
 * @author Jakob Jenkov - Copyright 2005 Jenkov Development
 */
public interface IResultSetProcessor {

    /**
     * Initializes the ResultSet. For instance, scrolls down to
     * the correct first record etc.
     * @param result         The ResultSet to initialize.
     * @param daos           The IDaos instance that this database connection belongs to.
     * @throws SQLException  If the intialization fails.
     * @throws PersistenceException If something else fails during initialization.
     */
    public void   init     (ResultSet result, IDaos daos)    throws SQLException, PersistenceException;

    /**
     * Processes a record in the ResultSet. For instance reads the column values into a
     * List, Map, or some other object. This method is called for each
     * record in the ResultSet.
     *
     * @param result The ResultSet to process the record of.
     * @param daos   The IDaos instance where the IJdbcDao that called this method belongs to.
     *               Use it to, for instance, read an object from the current record using
     *               the IGenericDao.read(objectMappingKey, ResultSet).
     * @throws SQLException If something fails in the driver during the processing of the current record.
     * @throws PersistenceException If something else fails during the processing.
     */
    public void   process  (ResultSet result, IDaos daos) throws SQLException, PersistenceException;

    /**
     * Returns the result of the total processing. For instance, if the process method reads records into
     * object and add them to a List, this method will return the List.
     * @return The result of the total processing of the ResultSet.
     * @throws PersistenceException If for some reason the returning of the result fails.
     *              This could be if the getResult() method builds a complex object from
     *              records read, and that this somehow fails (missing records, or invalid values).
     */
    public Object getResult(                ) throws PersistenceException;


}
