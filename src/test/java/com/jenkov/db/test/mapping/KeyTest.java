package com.jenkov.db.test.mapping;

import com.jenkov.db.impl.mapping.Key;
import com.jenkov.db.impl.mapping.KeyValue;
import com.jenkov.db.itf.mapping.IKey;
import com.jenkov.db.itf.mapping.IKeyValue;
import com.jenkov.db.itf.PersistenceException;
import junit.framework.TestCase;

import java.util.Set;
import java.util.TreeSet;
import java.util.HashSet;

/**
 * @author Jakob Jenkov - Copyright 2005 Jenkov Development
 */
public class KeyTest extends TestCase{


    protected IKey key = null;


    protected void setUp() throws Exception {
        this.key = new Key();
    }

    protected void tearDown() throws Exception {
        this.key = null;
    }

    public void testConstructor(){
        Set columns = new HashSet();
        columns.add("one");
        columns.add("two");
        Key aKey = new Key(columns);

        Set columns2 = (Set) aKey.getColumns();
        assertTrue(columns2.contains("one"));
        assertTrue(columns2.contains("two"));

        aKey = new Key(new String[]{"three", "four"});
        columns2 = (Set) aKey.getColumns();
        assertTrue(columns2.contains("three"));
        assertTrue(columns2.contains("four"));
    }

    public void testGetSetTable() {
        assertNull("table should be null", this.key.getTable());

        this.key.setTable("test");
        assertEquals("table should be test", "test", this.key.getTable());

        this.key.setTable("test2");
        assertEquals("table should be test", "test2", this.key.getTable());

        this.key.setTable(null);
        assertNull("table should be null again", this.key.getTable());
    }

    public void testAddRemoveColumn() throws Exception{
        assertEquals("should be empty", 0, this.key.getColumns().size());

        String column1 = "id";
        String column2 = "fid";

        this.key.addColumn(column1);
        assertEquals("should contain 1 column", 1, this.key.getColumns().size());
        assertTrue("should contain column1", this.key.getColumns().contains(column1));

        this.key.addColumn(column2);
        assertEquals("should contain 2 column", 2, this.key.getColumns().size());
        assertTrue("should contain column2", this.key.getColumns().contains(column2));

        this.key.removeColumn(column1);
        assertEquals("should contain 1 column", 1, this.key.getColumns().size());
        assertTrue("should contain column2", this.key.getColumns().contains(column2));
        assertFalse("should not contain column1", this.key.getColumns().contains(column1));

        this.key.removeColumn(column2);
        assertEquals("should contain 0 columns", 0, this.key.getColumns().size());
        assertFalse("should not contain column2", this.key.getColumns().contains(column2));
        assertFalse("should not contain column1", this.key.getColumns().contains(column1));
    }

    public void testGetSetColumns() throws Exception{
        Set columns = new TreeSet();
        columns.add("test");

        assertNotSame("should not be same", columns, this.key.getColumns());

        this.key.setColumns(columns);
        assertSame("should be same", columns, this.key.getColumns());

        columns = new TreeSet();
        assertNotSame("should not be same", columns, this.key.getColumns());
    }

    public void testIsValid() throws Exception{
        IKeyValue keyValue = new KeyValue();

        assertTrue("should be valid, no values... no key", key.isValid(keyValue));

        keyValue.addColumnValue("test", "test-value");
        assertTrue("when key is empty, all key values are valid", this.key.isValid(keyValue));

        key.addColumn("test");
        assertTrue("should be valid", this.key.isValid(keyValue));

        key.addColumn("id");
        assertFalse("should not be valid", this.key.isValid(keyValue));

        keyValue.addColumnValue("otherColumn", "otherColumn-value");
        assertFalse("should not be valid", this.key.isValid(keyValue));

        keyValue.addColumnValue("id", "id-value");
        assertTrue("should be valid", this.key.isValid(keyValue));

        keyValue.removeColumnValue("otherColumn");
        assertTrue("should be valid", this.key.isValid(keyValue));

        key.removeColumn("test");
        assertTrue("should not be valid", this.key.isValid(keyValue));
    }

    public void testToKeyValue() throws Exception{
        IKeyValue value = null;


        try {
            value = this.key.toKeyValue(null);
            fail("null key value should throw exception");
        } catch (PersistenceException e) {
            //ignore, exception expected.
        }

        try {
            value = this.key.toKeyValue("column");
            fail("empty key should throw exception");
        } catch (PersistenceException e) {
            //ignore, exception expected.
        }

        this.key.addColumn("test");
        value = this.key.toKeyValue("test");
        assertEquals("size should be 1", 1, value.getColumnValues().size());
        assertTrue  ("key value should contain column", value.getColumnValues().containsKey("test"));

        IKeyValue value2 = this.key.toKeyValue(value);
        assertSame("should return value instance", value, value2);

        this.key.addColumn("next");

        try {
            value = this.key.toKeyValue(value);
            fail("should throw exception on compound key");
        } catch (PersistenceException e) {
            //ignore, exception expected
        }

        try {
            value = this.key.toKeyValue(new Long(1));
            fail("should throw exception on compound key");
        } catch (PersistenceException e) {
            //ignore, exception expected
        }

    }

    public void testToString() throws Exception{

        assertEquals("empty key", "null()", this.key.toString());

        this.key.setTable("someTable");
        assertEquals("table set", "someTable()", this.key.toString());

        this.key.addColumn("id");
        assertEquals("1 column", "someTable(id)", this.key.toString());

        this.key.addColumn("test");
        assertEquals("2 columns", "someTable(id, test)", this.key.toString());

        this.key.setTable(null);
        assertEquals("no table, 2 columns", "null(id, test)", this.key.toString());
    }

    public void testEquals() throws Exception{

        IKey key2 = new Key();

        //empty - null
        assertFalse(key.equals(null));

        //empty - non-Key
        assertFalse(key.equals("non-key object"));

        //empty - empty
        assertEquals("empty - empty", key, key2);

        //table - empty
        this.key.setTable("table");
        assertFalse("table - empty", key.equals(key2));
        assertFalse("table - empty", key2.equals(key));

        //table - table !=
        key2.setTable("table2");
        assertFalse("table != table", key.equals(key2));
        assertFalse("table != table", key2.equals(key));

        //table - table ==
        key2.setTable("table");
        assertTrue("table == table", key.equals(key2));
        assertTrue("table == table", key2.equals(key));

        //table - table ==, 1 column - 0 columns
        this.key.addColumn("id");
        assertFalse("table == table, 1 column != 0 columns", key.equals(key2));
        assertFalse("table == table, 1 column != 0 columns", key2.equals(key));

        //table - table ==, 1 column - 1 column, !=
        key2.addColumn("id2");
        assertFalse("table == table, 1 column != 1 column", key.equals(key2));
        assertFalse("table == table, 1 column != 1 column", key2.equals(key));

        //table - table ==, 1 column - 1 column, ==
        key2.removeColumn("id2");
        key2.addColumn("id");
        assertTrue("table == table, 1 column == 1 column", key.equals(key2));
        assertTrue("table == table, 1 column == 1 column", key2.equals(key));

        //table - table ==, 2 columns - 1 column, ==
        this.key.addColumn("name");
        assertFalse("table == table, 2 columns != 1 column", key.equals(key2));
        assertFalse("table == table, 2 columns != 1 column", key2.equals(key));

        //table - table ==, 2 columns - 2 columns ==
        key2.addColumn("name2");
        assertFalse("table == table, 2 column != 2 column", key.equals(key2));
        assertFalse("table == table, 2 column != 2 column", key2.equals(key));

        //table - table ==, 2 columns - 2 columns !=
        key2.removeColumn("name2");
        key2.addColumn("name");
        assertTrue("table == table, 2 column == 2 column", key.equals(key2));
        assertTrue("table == table, 2 column == 2 column", key2.equals(key));

        //table != table, 2 columns - 2 columns ==
        this.key.setTable("other");
        assertFalse("table != table, 2 column == 2 column", key.equals(key2));
        assertFalse("table != table, 2 column == 2 column", key2.equals(key));

        //null != table, 2 columns - 2 columns ==
        this.key.setTable(null);
        assertFalse("null != table, 2 column == 2 column", key.equals(key2));
        assertFalse("null != table, 2 column == 2 column", key2.equals(key));

    }

    public void testSize() {

        assertEquals("size should be 0", 0, this.key.size());

        this.key.addColumn("test");
        assertEquals("size should be 1", 1, this.key.size());

        this.key.addColumn("test2");
        assertEquals("size should be 2", 2, this.key.size());

        this.key.removeColumn("test");
        assertEquals("size should be 1", 1, this.key.size());

        this.key.removeColumn("test2");
        assertEquals("size should be 0", 0, this.key.size());
    }

    public void testGetColumn() throws Exception{

        try {
            this.key.getColumn();
            fail("should throw exception on empty key");
        } catch (PersistenceException e) {
            //ignore, exception expected on empty key
        }

        this.key.addColumn("test");
        assertEquals("test", this.key.getColumn());

        this.key.addColumn("id");
        try {
            this.key.getColumn();
            fail("should throw exception on compound key");
        } catch (PersistenceException e) {
            //ignore, exception expected on empty key
        }
    }

}
