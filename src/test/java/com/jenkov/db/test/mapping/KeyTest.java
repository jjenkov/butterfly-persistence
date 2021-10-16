package com.jenkov.db.test.mapping;

import com.jenkov.db.impl.mapping.Key;
import com.jenkov.db.impl.mapping.KeyValue;
import com.jenkov.db.itf.mapping.IKey;
import com.jenkov.db.itf.mapping.IKeyValue;
import com.jenkov.db.itf.PersistenceException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.TreeSet;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Jakob Jenkov - Copyright 2005 Jenkov Development
 */
public class KeyTest{


    protected IKey key = null;


    @BeforeEach
    protected void setUp() throws Exception {
        this.key = new Key();
    }

    @AfterEach
    protected void tearDown() throws Exception {
        this.key = null;
    }

    @Test
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

    @Test
    public void testGetSetTable() {
        assertNull(this.key.getTable(), "table should be null");

        this.key.setTable("test");
        assertEquals("test", this.key.getTable(), "table should be test");

        this.key.setTable("test2");
        assertEquals("test2", this.key.getTable(), "table should be test");

        this.key.setTable(null);
        assertNull(this.key.getTable(), "table should be null again");
    }

    @Test
    public void testAddRemoveColumn() throws Exception{
        assertEquals(0, this.key.getColumns().size(), "should be empty");

        String column1 = "id";
        String column2 = "fid";

        this.key.addColumn(column1);
        assertEquals(1, this.key.getColumns().size(), "should contain 1 column");
        assertTrue(this.key.getColumns().contains(column1), "should contain column1");

        this.key.addColumn(column2);
        assertEquals(2, this.key.getColumns().size(), "should contain 2 column");
        assertTrue(this.key.getColumns().contains(column2), "should contain column2");

        this.key.removeColumn(column1);
        assertEquals(1, this.key.getColumns().size(), "should contain 1 column");
        assertTrue(this.key.getColumns().contains(column2), "should contain column2");
        assertFalse(this.key.getColumns().contains(column1), "should not contain column1");

        this.key.removeColumn(column2);
        assertEquals(0, this.key.getColumns().size(), "should contain 0 columns");
        assertFalse(this.key.getColumns().contains(column2), "should not contain column2");
        assertFalse(this.key.getColumns().contains(column1), "should not contain column1");
    }

    @Test
    public void testGetSetColumns() throws Exception{
        Set columns = new TreeSet();
        columns.add("test");

        assertNotSame(columns, this.key.getColumns(), "should not be same");

        this.key.setColumns(columns);
        assertSame(columns, this.key.getColumns(), "should be same");

        columns = new TreeSet();
        assertNotSame(columns, this.key.getColumns(), "should not be same");
    }

    @Test
    public void testIsValid() throws Exception{
        IKeyValue keyValue = new KeyValue();

        assertTrue(key.isValid(keyValue), "should be valid, no values... no key");

        keyValue.addColumnValue("test", "test-value");
        assertTrue(this.key.isValid(keyValue), "when key is empty, all key values are valid");

        key.addColumn("test");
        assertTrue(this.key.isValid(keyValue), "should be valid");

        key.addColumn("id");
        assertFalse(this.key.isValid(keyValue), "should not be valid");

        keyValue.addColumnValue("otherColumn", "otherColumn-value");
        assertFalse(this.key.isValid(keyValue), "should not be valid");

        keyValue.addColumnValue("id", "id-value");
        assertTrue(this.key.isValid(keyValue), "should be valid");

        keyValue.removeColumnValue("otherColumn");
        assertTrue(this.key.isValid(keyValue), "should be valid");

        key.removeColumn("test");
        assertTrue(this.key.isValid(keyValue), "should not be valid");
    }

    @Test
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
        assertEquals(1, value.getColumnValues().size(), "size should be 1");
        assertTrue  (value.getColumnValues().containsKey("test"), "key value should contain column");

        IKeyValue value2 = this.key.toKeyValue(value);
        assertSame(value, value2, "should return value instance");

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


    @Test
    public void testToString() throws Exception{

        assertEquals("null()", this.key.toString(), "empty key");

        this.key.setTable("someTable");
        assertEquals("someTable()", this.key.toString(), "table set");

        this.key.addColumn("id");
        assertEquals("someTable(id)", this.key.toString(),"1 column");

        this.key.addColumn("test");
        assertEquals("someTable(id, test)", this.key.toString(), "2 columns");

        this.key.setTable(null);
        assertEquals("null(id, test)", this.key.toString(), "no table, 2 columns");
    }

    @Test
    public void testEquals() throws Exception{

        IKey key2 = new Key();

        //empty - null
        assertFalse(key.equals(null));

        //empty - non-Key
        assertFalse(key.equals("non-key object"));

        //empty - empty
        assertEquals(key, key2, "empty - empty");

        //table - empty
        this.key.setTable("table");
        assertFalse(key.equals(key2), "table - empty");
        assertFalse(key2.equals(key), "table - empty");

        //table - table !=
        key2.setTable("table2");
        assertFalse(key.equals(key2), "table != table");
        assertFalse(key2.equals(key), "table != table");

        //table - table ==
        key2.setTable("table");
        assertTrue(key.equals(key2), "table == table");
        assertTrue(key2.equals(key), "table == table");

        //table - table ==, 1 column - 0 columns
        this.key.addColumn("id");
        assertFalse(key.equals(key2), "table == table, 1 column != 0 columns");
        assertFalse(key2.equals(key), "table == table, 1 column != 0 columns");

        //table - table ==, 1 column - 1 column, !=
        key2.addColumn("id2");
        assertFalse(key.equals(key2), "table == table, 1 column != 1 column");
        assertFalse(key2.equals(key), "table == table, 1 column != 1 column");

        //table - table ==, 1 column - 1 column, ==
        key2.removeColumn("id2");
        key2.addColumn("id");
        assertTrue(key.equals(key2), "table == table, 1 column == 1 column");
        assertTrue(key2.equals(key), "table == table, 1 column == 1 column");

        //table - table ==, 2 columns - 1 column, ==
        this.key.addColumn("name");
        assertFalse(key.equals(key2), "table == table, 2 columns != 1 column");
        assertFalse(key2.equals(key), "table == table, 2 columns != 1 column");

        //table - table ==, 2 columns - 2 columns ==
        key2.addColumn("name2");
        assertFalse(key.equals(key2), "table == table, 2 column != 2 column");
        assertFalse(key2.equals(key), "table == table, 2 column != 2 column");

        //table - table ==, 2 columns - 2 columns !=
        key2.removeColumn("name2");
        key2.addColumn("name");
        assertTrue(key.equals(key2), "table == table, 2 column == 2 column");
        assertTrue(key2.equals(key), "table == table, 2 column == 2 column");

        //table != table, 2 columns - 2 columns ==
        this.key.setTable("other");
        assertFalse(key.equals(key2), "table != table, 2 column == 2 column");
        assertFalse(key2.equals(key), "table != table, 2 column == 2 column");

        //null != table, 2 columns - 2 columns ==
        this.key.setTable(null);
        assertFalse(key.equals(key2), "null != table, 2 column == 2 column");
        assertFalse(key2.equals(key), "null != table, 2 column == 2 column");

    }

    @Test
    public void testSize() {

        assertEquals(0, this.key.size(), "size should be 0");

        this.key.addColumn("test");
        assertEquals(1, this.key.size(), "size should be 1");

        this.key.addColumn("test2");
        assertEquals(2, this.key.size(), "size should be 2");

        this.key.removeColumn("test");
        assertEquals(1, this.key.size(), "size should be 1");

        this.key.removeColumn("test2");
        assertEquals(0, this.key.size(), "size should be 0");
    }

    @Test
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
