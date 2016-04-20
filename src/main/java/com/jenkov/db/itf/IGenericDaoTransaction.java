package com.jenkov.db.itf;

/**
 *  * NOTE: Experimental!!
 * 
 * <br/><br/>
 * This interface represents a transaction that is executed inside one Mr. Persisters
 * dao instances (fx. by calling <code>IGenericDao.executeTransaction()</code>). A dao command may contain
 * multiple actions, for instance reading and updating various objects in the database.
 *
 * <br/><br/>
 * The advantage of dao commands is typically that some of the standard JDBC work can
 * be taken over by the dao instance. For instance committing or rolling back
 * the transaction and closing the connection after the transaction is executed.
 *  
 * @author Jakob Jenkov - Copyright 2005 Jenkov Development
 */
public interface IGenericDaoTransaction {

    /**
     * Executes this dao transaction.
     * @return  @return The result to be returned, if any, of this transaction.

     */
    public Object execute(IObjectDao dao) throws PersistenceException;
}
