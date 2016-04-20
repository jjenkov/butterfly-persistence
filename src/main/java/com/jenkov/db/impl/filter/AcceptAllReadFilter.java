package com.jenkov.db.impl.filter;

import com.jenkov.db.itf.IReadFilter;
import com.jenkov.db.itf.PersistenceException;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This read filter implementation accepts all records as the object(s) read. This filter
 * is used as default filter in case null is passed as filter to a read method taking a filter.
 * This means null = no filtering = all records accepted.
 */
public class AcceptAllReadFilter implements IReadFilter{

    public static final IReadFilter ACCEPT_ALL_FILTER = new AcceptAllReadFilter();

    public void     init(ResultSet result) throws SQLException, PersistenceException    {/* do nothing */ }
    public boolean  accept(ResultSet result) throws SQLException, PersistenceException  { return true;    }
    public boolean  acceptMore()                                                        { return true;    }

    public void acceptedByAllFilters(boolean wasAcceptedByAllFilters)                   {/* do nothing */ }

    public void     clear()                                                             {/* do nothing */ }

}
