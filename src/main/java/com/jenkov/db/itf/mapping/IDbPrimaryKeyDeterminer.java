package com.jenkov.db.itf.mapping;

import com.jenkov.db.itf.PersistenceException;

import java.sql.Connection;

/**
 * This interface represents the functions of the database primary key determiners used in Butterfly Persistence.
 * The responsibility of the database primary key determiner is to determine what column in the table is
 * the primary key. This information is stored in the coresponding object method along with the
 * table name, and is used by both the object reader and object writer for functions like
 * read-by-primary-key, insert, updateBatch, delete and delete-by-primary-key.
 *
 * <br/><br/>
 * As of now the database primary key determiner can only determine single column primary keys, and not
 * primary keys consisting of several columns.
 *
 * <br/><br/>
 * The database primary key determiner is used internally in the object mapper.
 *
 * @author Jakob Jenkov,  Jenkov Development
 */
public interface IDbPrimaryKeyDeterminer {


    /**
     * Returns a list of the columns that are part of the
     * @param table
     * @param databaseName
     * @param connection
     * @return
     * @throws PersistenceException
     */
    public IKey getPrimaryKeyMapping(String table, String databaseName, Connection connection)
    throws PersistenceException;
}
