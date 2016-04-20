/**
 * @author Jakob Jenkov,  Jenkov Development
 */
package com.jenkov.db.impl.mapping.method;

import java.sql.PreparedStatement;
import java.sql.SQLException;
/**
 * A subclass of <code>GetterMapping</code> capable of inserting an ascii stream (InputStream)
 * (from a Butterfly Persistence AsciiStream instance) into a <code>PreparedStatement</code>.
 *
 * @author Jakob Jenkov, Jenkov Development
 */

public class AsciiStreamGetterMapping extends GetterMapping{

    protected void insertObjectDo(Object value, PreparedStatement statement, int index) throws SQLException {
        statement.setAsciiStream(index, ((AsciiStream) value).getInputStream(), ((AsciiStream) value).getLength());
    }

}
