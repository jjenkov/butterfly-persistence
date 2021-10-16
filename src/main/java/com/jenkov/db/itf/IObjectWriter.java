package com.jenkov.db.itf;

import com.jenkov.db.itf.mapping.IObjectMapping;

import java.sql.Connection;
import java.util.Collection;

/**
 * The interface represents all the functions made available by the object writer of Butterfly Persistence.
 * The object writer is responsible for all writing functions in the database (insert / updateBatch / delete).
 * 
 * @author Jakob Jenkov,  Jenkov Development
 */
public interface IObjectWriter {

    /**
     * Sets the database that this ObjectWriter is supposed to read objects from. The Database
     * object contains information about the features the database supports.
     *
     * @param database The database to set on this object reader.
     */
    public void setDatabase(Database database);


    /**
     * Inserts the given object into the table it is mapped to in the given object mapping.
     *
     *
     * The SQL string is used to create a PreparedStatement and must be of the format
     * <code>insert into [table]( [field1], [field2], [field3] (etc.)) values (?, ?, ? etc)</code>.
     * The object writer will insert the values in a PreparedStatement internally. Therefore
     * the sequence of the fields must be the same as the one given by
     * <code>objectMapping.getColumns().iterator()</code>.
     * The SQLGenerator class can generate a suitable SQL string for
     * use with the object writer, so you don't have to do it yourself.
     *
     *
     * Remember to close the <code>Connection</code> yourself when you are done with it. This method
     * doesn't close it.
     * @param mapping  The object mapping to use to insert the object as a record in the database.
     * @param object   The object to insert into the database.
     * @param sql      The SQL string used to create a <code>PreparedStatement</code>. See description above.
     * @param connection The connection to the database to insert the object into.
     * @return The number of records affected, as returned by the <code>PreparedStatement.executeUpdate()</code>
     *         method.
     * @throws PersistenceException If anything goes wrong during the insertion.
     */
    public UpdateResult insert        (IObjectMapping mapping, Object object,
                               String sql, Connection connection) throws PersistenceException;


    /**
     * Inserts the objects in the <code>objects</code> collection into the table
     * they are mapped to, in the given object mapping. This method uses JDBC
     * batch updates to do the job, meaning all insert statements are batched and
     * sent in one go to the database.
     *
     *
     * The SQL string is used to create a PreparedStatement and must be of the format
     * <code>insert into [table]( [field1], [field2], [field3] (etc.)) values (?, ?, ? etc)</code>.
     * The object writer will insert the values in a PreparedStatement internally. Therefore
     * the sequence of the fields must be the same as the one given by
     * <code>objectMapping.getColumns().iterator()</code>.
     * The SQLGenerator class can generate a suitable SQL string for
     * use with the object writer, so you don't have to do it yourself.
     *
     *
     * Remember to close the <code>Connection</code> yourself when you are done with it. This method
     * doesn't close it.
     * @param mapping   The object mapping to use to insert the object as a record in the database.
     * @param objects   The objects to insert into the database.
     * @param sql       The SQL string used to create a <code>PreparedStatement</code>. See description above.
     * @param connection The connection to the database to insert the object into.
     * @return An array containing the number of records affected by each insert in the batch,
     *         as returned by the <code>PreparedStatement.executeBatch()</code>
     *         method.
     * @throws PersistenceException If anything goes wrong during the insertion.
     */
    public UpdateResult insertBatch    (IObjectMapping mapping, Collection objects,
                               String sql, Connection connection) throws PersistenceException;


    /**
     * Updates the record in the database coresponding to the given object, with the values present in
     * the object at the time of calling this method, according to the given object mapping. Do not
     * use this method if the primary key value is also changed during the update, or this method
     * will have no effect, or perhaps unintentional side effects.
     * If you do use it for an update where the primary key has changed,
     * the primary key value in the "where" clause of the SQL will contain the new primary key value.
     * Since no records, or perhaps another existing record, matches the new, changed, primary key value,
     * the update will have no effect. If you need to update a record including it's primary key, use
     * the other update method.
     *
     *
     * The SQL string must be of the format
     * <code>update [table] set [field1]=?, [field2]=?, [field3]=? etc. where [primaryKeyField]=?</code>
     * The object writer will insert the values in a PreparedStatement internally. Therefore
     * the sequence of the fields must be the same as the one given by
     * <code>objectMapping.getColumns().iterator()</code>.
     * The SQLGenerator class can generate a suitable SQL string for
     * use with the object writer, so you don't have to do it yourself.
     *
     *
     * Remember to close the <code>Connection</code> yourself when you are done with it. This method
     * doesn't close it.
     * @param mapping    The object mapping to use to update the objects record in the database.
     * @param object     The object to update the record for in the database, containing the values to be inserted.
     * @param sql        The SQL string to use to create the <code>PreparedStatement</code>.
     * @param connection The connection to the database to update the object in.
     * @return           The number of affected records as returned by the
     *                   <code>PreparedStatement.executeUpdate()</code>
     * @throws PersistenceException If anything goes wrong during the update.
     */
    public UpdateResult update        (IObjectMapping mapping, Object object,
                               String sql, Connection connection) throws PersistenceException;


    /**
     * Updates the record in the database matching the value of the oldPrimaryKeyValue,
     * with the values present in the object at the time of calling this method,
     * according to the given object mapping. Use this method when updating records
     * in which you also change the primary key value. This method inserts the old
     * primary key value into the "where" clause of the PreparedStatement, so the
     * correct record is updated.
     *
     *
     * The SQL string must be of the format
     * <code>update [table] set [field1]=?, [field2]=?, [field3]=? etc. where [primaryKeyField]=?</code>
     * The object writer will insert the values in a PreparedStatement internally. Therefore
     * the sequence of the fields must be the same as the one given by
     * <code>objectMapping.getColumns().iterator()</code>.
     * The SQLGenerator class can generate a suitable SQL string for
     * use with the object writer, so you don't have to do it yourself.
     *
     *
     * Remember to close the <code>Connection</code> yourself when you are done with it. This method
     * doesn't close it.
     * @param mapping    The object mapping to use to update the objects record in the database.
     * @param object     The object to update the record for in the database, containing the values to be inserted.
     * @param oldPrimaryKeyValue The primary key value of the record to update, meaning the value of the
     *                   primary key before it was changed in the object to update.
     * @param sql        The SQL string to use to create the <code>PreparedStatement</code>.
     * @param connection The connection to the database to update the object in.
     * @return           The number of affected records as returned by the
     *                   <code>PreparedStatement.executeUpdate()</code>
     * @throws PersistenceException If anything goes wrong during the update.
     */
    public UpdateResult update        (IObjectMapping mapping, Object object, Object oldPrimaryKeyValue,
                               String sql, Connection connection) throws PersistenceException;


   /*
    * Updates the records in the database coresponding to the objects contained in the collection
    * passed in parameter <code>objects</code>. The values in the objects are written to the
    * coresponding records, according to the object mapping passed as parameter. This method uses
    * JDBC batch updates to do the update, meaning the SQL sentences are sent to the database
    * in larger batches/chunks, instead of sending them individually. This increases test_config
    * of updates radically.
    *
    *
    * Do not
    * use this method if the primary key values of the objects/records are also changed
    * during the update. If you do, this method may have no or a wrong effect.
    * If you do use it for updates where the primary key has changed,
    * the primary key value in the "where" clause of the SQL will contain the new primary key value,
    * and not the old value.
    * Since no records, or perhaps another existing record, matches the new, changed, primary key value,
    * the update will either have no effect, or may cause an update to the wrong record.
    * If you need to update a record including it's primary key, use
    * the other batch update method.
    *
    *
    * The SQL string must be of the format
    * <code>update [table] set [field1]=?, [field2]=?, [field3]=? etc. where [primaryKeyField]=?</code>
    * The object writer will insert the values in a PreparedStatement internally. Therefore
    * the sequence of the fields must be the same as the one given by
    * <code>objectMapping.getColumns().iterator()</code>.
    * The SQLGenerator class can generate a suitable SQL string for
    * use with the object writer, so you don't have to do it yourself.
    *
    *
    * Remember to close the <code>Connection</code> yourself when you are done with it. This method
    * doesn't close it.
    * @param mapping    The object mapping to use to update the objects record in the database.
    * @param objects    The objects to update the records for in the database.
    * @param sql        The SQL string to use to create the <code>PreparedStatement</code>.
    * @param connection The connection to the database to update the object in.
    * @return An array containing the number of records affected by each update in the batch,
    *         as returned by the <code>PreparedStatement.executeBatch()</code>
    *         method.
    * @throws PersistenceException If anything goes wrong during the update.
    */
    public UpdateResult updateBatch      (IObjectMapping mapping, Collection objects,
                               String sql, Connection connection) throws PersistenceException;


   /*
    * Updates the records in the database coresponding to the objects contained in the collection
    * passed in parameter <code>objects</code>. The values in the objects are written to the
    * coresponding records, according to the object mapping passed as parameter. This method uses
    * JDBC batch updates to do the update, meaning the SQL sentences are sent to the database
    * in larger batches/chunks, instead of sending them individually. This increases test_config
    * of updates radically.
    *
    *
    * Use this method if the primary key values of the objects/records are changed
    * during the update. The old primary keys are used to identify the records to
    * update. The values of the primary keys in
    * the objects are the values the the primary keys of the records will have after the update.
    *
    *
    * The SQL string must be of the format
    * <code>update [table] set [field1]=?, [field2]=?, [field3]=? etc. where [primaryKeyField]=?</code>
    * The object writer will insert the values in a PreparedStatement internally. Therefore
    * the sequence of the fields must be the same as the one given by
    * <code>objectMapping.getColumns().iterator()</code>.
    * The SQLGenerator class can generate a suitable SQL string for
    * use with the object writer, so you don't have to do it yourself.
    *
    *
    * Remember to close the <code>Connection</code> yourself when you are done with it. This method
    * doesn't close it.
    * @param mapping    The object mapping to use to update the objects record in the database.
    * @param objects    The objects to update the records for in the database.
    * @param oldPrimaryKeys The old primary key values of the objects to update the records for in the database.
                        The primary key value must be returned by this collection's iterator in the same
                        sequence the objects in the objects collection's iterator. Otherwise the
                        old primary key values will be matched with the wrong objects. By keeping
                        objects and old primary keys in each their java.util.List this works fine.
    * @param sql        The SQL string to use to create the <code>PreparedStatement</code>.
    * @param connection The connection to the database to update the object in.
    * @return An array containing the number of records affected by each update in the batch,
    *         as returned by the <code>PreparedStatement.executeBatch()</code>
    *         method.
    * @throws PersistenceException If anything goes wrong during the update.
    */
    public UpdateResult updateBatch      (IObjectMapping mapping, Collection objects, Collection oldPrimaryKeys,
                               String sql, Connection connection) throws PersistenceException;


    /**
     * Deletes the record in the database coresponding to the given object.
     * The SQL string is used to create a <code>PreparedStatement</code> must be of the format
     * <code>delete from [table] where [primaryKeyField]=?</code>.
     * The SQLGenerator class can generate a suitable SQL string for
     * use with the object writer, so you don't have to do it yourself.
     *
     *
     * Remember to close the <code>Connection</code> yourself when you are done with it. This method
     * doesn't close it.
     * @param mapping    The object mapping to use to delete this object.
     * @param object     The object to delete the record for from the database.
     * @param sql        The SQL string used to create the <code>PreparedStatement</code>.
     * @param connection The connection to the database to delete the objects record from.
     * @return           The number of affected records as returned by
     *                   <code>PreparedStatement.executeUpdate()</code>
     * @throws PersistenceException If anything goes wrong during the deletion.
     */
    public UpdateResult delete        (IObjectMapping mapping, Object object,
                               String sql, Connection connection) throws PersistenceException;



    /**
     * Deletes the records in the database coresponding to the given objects. This method uses JDBC
     * batch updates to do the job, meaning all delete statements are batched and
     * sent in one go to the database.
     *
     * The SQL string is used to create a <code>PreparedStatement</code> must be of the format
     * <code>delete from [table] where [primaryKeyField]=?</code>.
     * The SQLGenerator class can generate a suitable SQL string for
     * use with the object writer, so you don't have to do it yourself.
     *
     *
     * Remember to close the <code>Connection</code> yourself when you are done with it. This method
     * doesn't close it.
     * @param mapping    The object mapping to use to delete this object.
     * @param objects     The object to delete the record for from the database.
     * @param sql        The SQL string used to create the <code>PreparedStatement</code>.
     * @param connection The connection to the database to delete the objects record from.
     * @return           The number of affected records as returned by
     *                   <code>PreparedStatement.executeUpdate()</code>
     * @throws PersistenceException If anything goes wrong during the deletion.
     */
    public UpdateResult deleteBatch   (IObjectMapping mapping, Collection objects,
                               String sql, Connection connection) throws PersistenceException;



    /**
     * Deletes the record matching the given primary key, from the table referenced in the object mapping.
     * The SQL string is used to create a <code>PreparedStatement</code> must be of the format
     * <code>delete from [table] where [primaryKeyField]=?</code>.
     * The SQLGenerator class can generate a suitable SQL string for
     * use with the object writer, so you don't have to do it yourself.
     *
     *
     * Remember to close the <code>Connection</code> yourself when you are done with it. This method
     * doesn't close it.
     * @param mapping    The object mapping to use to delete this object.
     * @param primaryKey The value of the primary key as an object, for instance new Integer(2) or "xyz123" etc.
     * @param sql        The SQL string used to create the <code>PreparedStatement</code>.
     * @param connection The connection to the database to delete the objects record from.
     * @return An array containing the number of records affected by each insert in the batch,
     *         as returned by the <code>PreparedStatement.executeBatch()</code>
     *         method.
     * @throws PersistenceException If anything goes wrong during the deletion.
     */
    public UpdateResult deleteByPrimaryKey(IObjectMapping mapping, Object primaryKey,
                                   String sql, Connection connection) throws PersistenceException;


    /**
     * Deletes the records in the database coresponding to the given primary keys. This method uses JDBC
     * batch updates to do the job, meaning all delete statements are batched and
     * sent in one go to the database.
     *
     * The SQL string is used to create a <code>PreparedStatement</code> must be of the format
     * <code>delete from [table] where [primaryKeyField]=?</code>.
     * The SQLGenerator class can generate a suitable SQL string for
     * use with the object writer, so you don't have to do it yourself.
     *
     *
     * Remember to close the <code>Connection</code> yourself when you are done with it. This method
     * doesn't close it.
     * @param mapping     The object mapping to use to delete this object.
     * @param primaryKeys The object to delete the record for from the database.
     * @param sql         The SQL string used to create the <code>PreparedStatement</code>.
     * @param connection  The connection to the database to delete the objects record from.
     * @return            The number of affected records as returned by
     *                    <code>PreparedStatement.executeUpdate()</code>
     * @throws PersistenceException If anything goes wrong during the deletion.
     */
    public UpdateResult deleteByPrimaryKeysBatch(IObjectMapping mapping, Collection primaryKeys, String sql, Connection connection) throws PersistenceException ;

}
