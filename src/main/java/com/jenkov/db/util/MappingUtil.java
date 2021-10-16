package com.jenkov.db.util;

import com.jenkov.db.itf.PersistenceException;
import com.jenkov.db.itf.mapping.IKeyValue;
import com.jenkov.db.itf.mapping.IObjectMapping;

import java.sql.PreparedStatement;
import java.util.Iterator;

/**
 * This class contains utility methods shared by the components of Butterfly Persistence.
 * @author Jakob Jenkov - Copyright 2005 Jenkov Development
 */
public class MappingUtil {

    /**
     * This method can insert the given key value object into a prepared statement. This
     * is a utility method used by the object reader and object writer.
     *
     * @param  mapping    The object mapping to use during insertion of key value.
     * @param  keyValue   The key value to insert into the prepared statement.
     * @param  statement  The prepared statement to insert the key value into.
     * @param  index      The index of the prepared statement parameter
     *                    to insert the first value of the key value into.
     * @return            The index of the last inserted value + 1.
     *                    Equal to the next index a prepared statement parameter
     *                    should be inserted into, if there are more parameters in
     *                    the prepared statement.
     * @throws com.jenkov.db.itf.PersistenceException If the given key value does not match the primary key of this
     *                    object mapping, or if the insert fails.
     */
    public static int insertPrimaryKey(IObjectMapping mapping, IKeyValue keyValue, PreparedStatement statement, int index)
    throws PersistenceException {
      if(! mapping.getPrimaryKey().isValid(keyValue)){
          throw new PersistenceException("The key value is not valid. " +
                  "Primary key is: "   + mapping.getPrimaryKey() +
                  ". Key value is: "   + keyValue +
                  ". Index is : "      + index);

      }

      Iterator iterator = mapping.getPrimaryKey().getColumns().iterator();
      while (iterator.hasNext()){
          String column = (String) iterator.next();
          mapping.getGetterMapping(column)
                 .insertObject(keyValue.getColumnValue(column), statement, index++);
      }
      return index;
    }


}
