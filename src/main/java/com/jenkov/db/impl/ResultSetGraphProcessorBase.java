package com.jenkov.db.impl;

import com.jenkov.db.itf.IResultSetProcessor;
import com.jenkov.db.itf.PersistenceException;
import com.jenkov.db.itf.IDaos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

/**

 */
public class ResultSetGraphProcessorBase extends ResultSetProcessorBase implements IResultSetProcessor {

    protected Map<Set, Map> previousColumnSetValues = new HashMap<Set, Map>();
    protected Set<String>[] columnSets              = null;


    public void init(ResultSet result, IDaos daos) throws SQLException, PersistenceException {
        onInit(result);
    }

    protected void onInit(ResultSet result)  throws SQLException, PersistenceException {

    }

    public void process(ResultSet result, IDaos daos) throws SQLException, PersistenceException {
        for(Set<String> columnSet : columnSets){
            if(hasColumnChanged(result, columnSet)){
                onColumnChange(result, daos, columnSet);
            }
        }
        onRecord(result, daos);
    }

    protected Set<String> columnSet(String ... columns){
        Set<String> set = new HashSet<String>();
        for(String column : columns){
            set.add(column);
        }
        return set;
    }

    /**
     * Called for every record in the ResultSet where the values of one of the specified column sets change.
     * Override this method to be notified of changes to the column sets speficied in the init() method.
     * @param result    The ResultSet being iterated.
     * @param daos      The IDaos instance used to obtain the JdbcDao with. Can be used to read objects from
     *                  the current record in the ResultSet etc.
     * @param columnSet The columns making up the set to watch for changes.
     * @throws SQLException If anything fails during processing of this record.
     * @throws PersistenceException If anything fails during processing of this record.
     */
    protected void onColumnChange(ResultSet result, IDaos daos, Set columnSet) throws SQLException, PersistenceException {

    }

    /**
     * Called for every record in the ResultSet
     * @param result The ResultSet iterated.
     * @param daos      The IDaos instance used to obtain the JdbcDao with. Can be used to read objects from
     *                  the current record in the ResultSet etc.
     * @throws SQLException If anything fails during the processing of the record.
     * @throws PersistenceException If anything fails during the processing of the record.
     */
    protected void onRecord(ResultSet result, IDaos daos) throws SQLException, PersistenceException  {

    }

    private boolean hasColumnChanged(ResultSet result, Set<String> columnSet) throws SQLException {
        boolean hasColumnsChanged = false;

        Map previousValues = this.previousColumnSetValues.get(columnSet);

        if(previousValues == null){
            this.previousColumnSetValues.put(columnSet, readColumnValues(result, columnSet));
        }

        Map recordValues = readColumnValues(result, columnSet);
        hasColumnsChanged = !recordValues.equals(previousValues);

        if(hasColumnsChanged){
            this.previousColumnSetValues.put(columnSet, recordValues);
        }

        return hasColumnsChanged;
    }

    private Map readColumnValues(ResultSet result, Set<String> columnSet) throws SQLException {
        Map values = new HashMap();

        for(String column : columnSet){
            values.put(column, result.getObject(column));
        }

        return values;
    }



}
