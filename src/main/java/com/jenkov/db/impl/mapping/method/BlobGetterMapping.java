/**
 * @author Jakob Jenkov,  Jenkov Development
 */
package com.jenkov.db.impl.mapping.method;

import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * A subclass of <code>GetterMapping</code> capable of inserting a <code>BlobMock</code> into
 * a <code>PreparedStatement</code>.
 *
 * @author Jakob Jenkov, Jenkov Development
 */

public class BlobGetterMapping extends GetterMapping{


    protected void insertObjectDo(Object value, PreparedStatement statement, int index) throws SQLException {
        statement.setBlob(index, (Blob) value);
    }

}
