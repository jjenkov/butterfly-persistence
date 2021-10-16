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
 * As of now the database primary key determiner can only determine single column primary keys, and not
 * primary keys consisting of several columns.
 *
 * The database primary key determiner is used internally in the object mapper.
 *
 * @author Jakob Jenkov,  Jenkov Development
 */
public interface IDbPrimaryKeyDeterminer {


    /**
     * Returns a list of the columns that are part of the
     * @param table         The table to determine the primary key for.
     * @param databaseName  The name of the database containing the table
     * @param connection    A JDBC Connection to the database.
     * @return              An IKey instance representing the primary key for the given table.
     * @throws PersistenceException If anything fails during determining the primary key for the table.
     */
    public IKey getPrimaryKeyMapping(String table, String databaseName, Connection connection)
    throws PersistenceException;
}
