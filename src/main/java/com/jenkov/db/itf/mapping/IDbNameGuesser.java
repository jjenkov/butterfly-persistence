/**
 * User: Administrator
 */
package com.jenkov.db.itf.mapping;

import java.util.Collection;
import java.lang.reflect.Method;


/**
 * This interface represents the functions made available by the database name guessers used in
 * Butterfly Persistence. The responsibility of the database name guesser is to "guess" (generate)
 * possible either table names from class names, or to "guess" possible column names from
 * class method names (getters and setters).
 *
 * <br/><br/>
 * The database name guesser is used internally in the object mapper.
 * The names generated by the database name guesser are used to feed the database name determiner
 * that determines which of the name guesses (generated possible names) is the correct one.
 *  
 * @author Jakob Jenkov, Jenkov Development.
 */
public interface IDbNameGuesser {

    /**
     * Returns a <code>List</code> containing possible database column names for the given class
     * objectMethod name.
     * @param member The objectMethod to guess column names for.
     * @return A <code>List</code> of <code>String</code> instances representing the guesses (possible names).
     */
    public Collection getPossibleColumnNames(Method member);

    /**
     * Returns a <code>List</code> containing possible table names for the given class name.
     * @param object The object to guess table names for.
     * @return The <code>List</code> of <code>String</code> instances representing the guesses (possible names).
     */
    public Collection getPossibleTableNames(Class object);

}
