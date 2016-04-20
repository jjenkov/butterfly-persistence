package com.jenkov.db.itf;

/**
 * Represents a DAO capable of simplifying the most ordinary JDBC tasks like reading
 * a long from the database, iterating a ResultSet and executing an update.
 *
 * @author Jakob Jenkov - Copyright 2005 Jenkov Development
 */
public interface IJdbcDao {

    /**
     * Reads a long from the database using the given SQL query. The first column of the first record
     * is returned as a long. This method is handy when you need just the id of some record.
     * For instance:<br/><br/>
     *
     * select id from books where title = 'Summer Night Dream'
     *
     * @param sql The SQL that locates the record and column containing the long to read.
     * @return A long read from the first record and first column in the ResultSet.
     * @throws PersistenceException If something goes wrong during the read.
     */
    public Long readLong(String sql)                              throws PersistenceException;

    /**
     * Reads a long from the database using the given SQL query. The first column of the first record
     * is returned as a long. This method is handy when you need just the id of some record.
     * For instance:<br/><br/>
     *
     * select id from books where title = 'Summer Night Dream'
     *
     * @param sql The SQL that locates the record and column containing the long to read.
     *            Formatted as an SQL query for a PreparedStatement, with ?-marks for parameters to insert.
     * @param parameters The parameters to insert into the PreparedStatement before
     *            executing the SQL. For instance new Object[]{45}
     * @return A long read from the first record and first column in the ResultSet.
     * @throws PersistenceException If something goes wrong during the read.
     */
    public Long readLong(String sql, Object ... parameters)         throws PersistenceException;

    /**
     * Reads all the ids located by the given SQL into an id string that can
     * be used in an SQL select ... where in (id1, id2,...) query.
     * This method generates the "(id1, id2, id3, etc.)" String so it
     * is ready to concatenate with a "select * from ... where in" query String.
     * The first column in the ResultSet is what will be interpreted as
     * the id.
     *
     * @param sql The SQL query that locates the ids to add to the id string.
     *            For example: select book_id from books where author_id = 2
     * @return An id string containing the ids concatenated. For instance
     * "(1, 45, 37, 8, 220)"
     * @throws PersistenceException If anything goes wrong during the read.
     *
     */
    public String readIdString(String sql) throws PersistenceException;

    /**
     * Reads all the ids located by the given SQL into an id string that can
     * be used in an SQL select ... where in (id1, id2,...) query.
     * This method generates the "(id1, id2, id3, etc.)" String so it
     * is ready to concatenate with a "select * from ... where in" query String.
     * The first column in the ResultSet is what will be interpreted as
     * the id.
     *
     * @param sql The SQL query that locates the ids to add to the id string.
     *            For example: select book_id from books where author_id = ?
     * @param parameters The parameters to insert into the PreparedStatement before
     *            executing the SQL. For instance new Object[]{45}
     * @return An id string containing the ids concatenated. For instance
     * "(1, 45, 37, 8, 220)"
     * @throws PersistenceException If anything goes wrong during the read.
     *
     */
    public String readIdString(String sql, Object ... parameters) throws PersistenceException;

    /**
     * Reads all the ids located by the given SQL into an id string that can
     * be used in an SQL select ... where in (id1, id2,...) query.
     * This method generates the "(id1, id2, id3, etc.)" String so it
     * is ready to concatenate with a "select * from ... where in" query String.
     * The first column in the ResultSet is what will be interpreted as
     * the id.
     *
     * @param sql The SQL query that locates the ids to add to the id string.
     *            For example: select book_id from books where author_id = ?
     * @param statementManager An instance capable of preparing, initializing
     *            parameters of, and post-processing the PreparedStatement
     *            being used to execute the SQL. It is easiest to extend
     *            the PreparedStatementManagerBase which has default implementations
     *            for the prepare(...), init(...), execute(...) and postProcess() methods.
     * @return An id string containing the ids concatenated. For instance
     * "(1, 45, 37, 8, 220)"
     * @throws PersistenceException If anything goes wrong during the read.
     *
     */
    public String readIdString(String sql, IPreparedStatementManager statementManager) throws PersistenceException;

    /**
     * Executes the given SQL and calls the IResultSetProcessor's process(...) method
     * for each record in the ResultSet. Before iterating the ResultSet the IResultSetProcessor's
     * init(...) method is called. When the ResultSet is fully iterated the IResultSetProcessor's
     * getResult() method is called. The value returned from getResult() is the value returned
     * from this read(...) method.
     *
     * @param sql         The SQL that locates the records to iterate.
     * @param processor   The IResultSetProcessor implementation that processes the ResultSet.
     *                    It is easiest to extend the ResultSetProcessorBase which has empty
     *                    implementations for init(...), process(...), and getResult(). Then
     *                    you only have to override the methods you need.
     * @return
     * @throws PersistenceException If anything goes wrong during the execution of the SQL and the
     *                    iteration of the ResultSet.
     */
    public Object read(String sql, IResultSetProcessor processor) throws PersistenceException;

    /**
     * Executes the given SQL and calls the IResultSetProcessor's process(...) method
     * for each record in the ResultSet. Before iterating the ResultSet the IResultSetProcessor's
     * init(...) method is called. When the ResultSet is fully iterated the IResultSetProcessor's
     * getResult() method is called. The value returned from getResult() is the value returned
     * from this read(...) method.
     *
     * @param sql         The SQL that locates the records to iterate.
     * @param parameters  The parameters to insert into the PreparedStatement before executing the SQL.
     * @param processor   The IResultSetProcessor implementation that processes the ResultSet.
     *                    It is easiest to extend the ResultSetProcessorBase which has empty
     *                    implementations for init(...), process(...), and getResult(). Then
     *                    you only have to override the methods you need.
     * @return
     * @throws PersistenceException If anything goes wrong during the execution of the SQL and the
     *                    iteration of the ResultSet.
     */
    public Object read(String sql, IResultSetProcessor processor, Object ... parameters) throws PersistenceException;

    /**
     * Executes the given SQL and calls the IResultSetProcessor's process(...) method
     * for each record in the ResultSet. Before iterating the ResultSet the IResultSetProcessor's
     * init(...) method is called. When the ResultSet is fully iterated the IResultSetProcessor's
     * getResult() method is called. The value returned from getResult() is the value returned
     * from this read(...) method.
     *
     * @param sql         The SQL that locates the records to iterate.
     * @param statementManager An instance capable of preparing, initializing
     *            parameters of, and post-processing the PreparedStatement
     *            being used to execute the SQL. It is easiest to extend
     *            the PreparedStatementManagerBase which has default implementations
     *            for the prepare(...), init(...), execute(...) and postProcess() methods.
     * @param processor   The IResultSetProcessor implementation that processes the ResultSet.
     *                    It is easiest to extend the ResultSetProcessorBase which has empty
     *                    implementations for init(...), process(...), and getResult(). Then
     *                    you only have to override the methods you need.
     * @return
     * @throws PersistenceException If anything goes wrong during the execution of the SQL and the
     *                    iteration of the ResultSet.
     */
    public Object read(String sql, IPreparedStatementManager statementManager, IResultSetProcessor processor) throws PersistenceException;

    /**
     * Executes the given SQL as an update (PreparedStatement.executeUpdate()).
     * Useful for insert, update and delete statements.
     *
     * @param sql The SQL containing the update.
     * @return The number of records affected as returned by the PreparedStatement.executeUpdate() method.
     * @throws PersistenceException If anyting goes wrong during the update.
     */
    public int update(String sql) throws PersistenceException;

    /**
     * Executes the given SQL as an update (PreparedStatement.executeUpdate()).
     * Useful for insert, update and delete statements.
     *
     * @param sql The SQL containing the update.
     * @param parameters  The parameters to insert into the PreparedStatement before executing the SQL.
     * @return The number of records affected as returned by the PreparedStatement.executeUpdate() method.
     * @throws PersistenceException If anyting goes wrong during the update.
     */
    public int update(String sql, Object ... parameters) throws PersistenceException;


    /**
     * Executes the given SQL as an update (PreparedStatement.executeUpdate()).
     * Useful for insert, update and delete statements.
     *
     * @param sql The SQL containing the update.
     * @param statementManager An instance capable of preparing, initializing
     *            parameters of, and post-processing the PreparedStatement
     *            being used to execute the SQL. It is easiest to extend
     *            the PreparedStatementManagerBase which has default implementations
     *            for the prepare(...), init(...), execute(...) and postProcess() methods.
     * @return The number of records affected as returned by the PreparedStatement.executeUpdate() method.
     * @throws PersistenceException If anyting goes wrong during the update.
     */
    public int update(String sql, IPreparedStatementManager statementManager) throws PersistenceException;

}
