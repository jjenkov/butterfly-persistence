/**
 * @author Jakob Jenkov,  Jenkov Development
 */
package com.jenkov.db.impl.mapping.method;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * A subclass of <code>GetterMapping</code> capable of inserting a character stream
 * (from a Butterfly Persistence CharacterStream instance) into a <code>PreparedStatement</code>.
 *
 * @author Jakob Jenkov, Jenkov Development
 */
public class CharacterStreamGetterMapping extends GetterMapping{

 
    protected void insertObjectDo(Object value, PreparedStatement statement, int index) throws SQLException {
        statement.setCharacterStream(index, ((CharacterStream) value).getReader(), ((CharacterStream) value).getLength());
    }


}
