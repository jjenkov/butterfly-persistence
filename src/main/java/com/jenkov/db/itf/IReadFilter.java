package com.jenkov.db.itf;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *  This interface represents the read filters that can be used in Butterfly Persistence.
 *  A read filter is used to limit the amount of records read, by having the filter
 *  not accept any records that are not desired to be read. This can be used to
 *  read pages of objects, for instance object 10-20 of a total <code>ResultSet</code>
 *  of 50 records.
 *
 * It can also be used to just filter the <code>ResultSet</code>
 * further than was possible (or hard to do) in the SQL generating the <code>ResultSet</code>
 * in the first place. Also read filters can be combined, making it easier to have
 * filters that can be switched on and off. Easier than having to generate the SQL
 * for those kinds of on/off filterings.
 *
 * Read filters are used when calling the functions of the object reader.
 * @author Jakob Jenkov, Jenkov Development
 */
public interface IReadFilter {

    /**
     * Called by the object reader before reading starts taking place. Can be used to
     * manipulate the result set before the reading takes place, for instance, moving
     * the result set forward to a certain record. This way paged reading can be implemented.
     * @param result The ResultSet to initialize.
     * @throws SQLException If initialization fails
     * @throws PersistenceException If initialization fails.
     */
    public void init(ResultSet result) throws SQLException, PersistenceException;


    /**
     * Returns true if the filter can accept the record at the current position of the result
     * set as part of the objects read. False if the filter doesn't want this record to be
     * part of the list of objects read.
     * @param result The ResultSet instance apply the filter filter to.
     * @return True if the filter accepts the current record. False if not.
     * @throws SQLException If something fails during acceptance/rejectance of the record the ResultSet points to.
     * @throws PersistenceException If something fails during acceptance/rejectance of the reocrd the ResultSet points to.
     */
    public boolean accept(ResultSet result) throws SQLException, PersistenceException;


    /**
     * Returns true if the filter will accept anymore records at all. False if not. This
     * can for instance be used to stop the reading after a certain amount of records has
     * been accepted, for paged reading.
     * @return True if the filter will accept anymore records at all. False if not.
     */
    public boolean acceptMore();

    /**
     * If the filter is used in a combined filter, this filter can be told whether all
     * other filters accepted this record, or not. This may be useful for instance in
     * a paged filter, that wants 10 records total, but only wants to count accepted
     * records if other filters also accepts the records.
     * @param wasAcceptedByAllFilters Will be set to true if all filters in a combined filter
     * accepted the current record. Will be set to false if just one single filter do
     * not accept the current record.
     */
    public void acceptedByAllFilters(boolean wasAcceptedByAllFilters);

    /**
     * This method is called when all reading is done. This gives the filter a chance to
     * clean up any objects instantiated during filtering.
     */
    public void clear();



}
