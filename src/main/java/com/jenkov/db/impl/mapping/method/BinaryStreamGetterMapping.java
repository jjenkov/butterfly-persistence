/**
 * @author Jakob Jenkov,  Jenkov Development
 */
package com.jenkov.db.impl.mapping.method;

import java.sql.PreparedStatement;
import java.sql.SQLException;
/**
 * A subclass of <code>GetterMapping</code> capable of inserting a binary stream (InputStream)
 * (from a Butterfly Persistence BinaryStream instance) into a <code>PreparedStatement</code>.
 *
 * @author Jakob Jenkov, Jenkov Development
 */
public class BinaryStreamGetterMapping extends GetterMapping{

    protected void insertObjectDo(Object value, PreparedStatement statement, int index) throws SQLException {
        statement.setBinaryStream(index, ((BinaryStream) value).getInputStream(), ((BinaryStream) value).getLength());
   }

}
