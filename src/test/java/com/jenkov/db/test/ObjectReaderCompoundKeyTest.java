package com.jenkov.db.test;

import com.jenkov.db.impl.ObjectReader;
import com.jenkov.db.impl.SqlGenerator;
import com.jenkov.db.impl.mapping.KeyValue;
import com.jenkov.db.impl.mapping.ObjectMapper;
import com.jenkov.db.impl.mapping.ObjectMappingFactory;
import com.jenkov.db.itf.IObjectReader;
import com.jenkov.db.itf.ISqlGenerator;
import com.jenkov.db.itf.IReadFilter;
import com.jenkov.db.itf.Database;
import com.jenkov.db.itf.mapping.IKeyValue;
import com.jenkov.db.itf.mapping.IObjectMapping;
import com.jenkov.db.test.objects.CompoundPkObject;
import com.jenkov.db.util.JdbcUtil;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Jakob Jenkov - Copyright 2005 Jenkov Development
 */
public class ObjectReaderCompoundKeyTest {

    String INSERT1 = "insert into compound_pk_objects(id, id2) values(1,1)";
    String INSERT2 = "insert into compound_pk_objects(id, id2) values(1,2)";
    String INSERT3 = "insert into compound_pk_objects(id, id2) values(1,3)";
    String INSERT4 = "insert into compound_pk_objects(id, id2) values(1,4)";
    String INSERT5 = "insert into compound_pk_objects(id, id2) values(1,5)";

    String DELETE  = "delete from compound_pk_objects";

    protected Connection     connection = null;
    protected IObjectReader  reader     = null;
    protected ISqlGenerator  generator  = null;
    protected IObjectMapping mapping    = null;


    @BeforeEach
    protected void setUp() throws Exception {
        this.connection = Environment.getConnection();
        this.reader     = new ObjectReader();
        this.reader.setDatabase(Database.determineDatabase(connection));
        this.generator  = new SqlGenerator();
        this.mapping    = new ObjectMapper(new ObjectMappingFactory()).mapToTable(CompoundPkObject.class, null, this.connection, null, null);
    }

    @AfterEach
    protected void tearDown() throws Exception {
        JdbcUtil.closeIgnore(this.connection);
        this.reader    = null;
        this.generator = null;
    }


    @Test
    public void testReadByPrimaryKey_CompoundKey() throws Exception{

        try {
            Environment.executeSql(DELETE);
            Environment.executeSql(INSERT1);
            Environment.executeSql(INSERT2);
            Environment.executeSql(INSERT3);


            IKeyValue keyValue = createKeyValue(1, 1);

            CompoundPkObject object = (CompoundPkObject)
                    this.reader.readByPrimaryKey( this.mapping, keyValue,
                            this.generator.generateReadByPrimaryKeyStatement(this.mapping), this.connection);
            assertEquals(1, object.getId(), "id  should be 1");
            assertEquals(1, object.getId2(), "id2 should be 1");


            keyValue = createKeyValue(1,3);

            object = (CompoundPkObject)
                    this.reader.readByPrimaryKey( this.mapping, keyValue,
                            this.generator.generateReadByPrimaryKeyStatement(this.mapping), this.connection);
            assertEquals(1, object.getId(), "id  should be 1");
            assertEquals(3, object.getId2(), "id2 should be 3");

            keyValue = createKeyValue(1,4);

            object = (CompoundPkObject)
                    this.reader.readByPrimaryKey( this.mapping, keyValue,
                            this.generator.generateReadByPrimaryKeyStatement(this.mapping), this.connection);
            assertNull(object, "should be null");

        } finally {
            Environment.executeSql(DELETE);
        }
    }

    @Test
    public void testReadListByPrimaryKeys_CompoundKey() throws Exception{
        try{
            Environment.executeSql(INSERT1);
            Environment.executeSql(INSERT2);
            Environment.executeSql(INSERT3);

            IKeyValue keyValue1 =createKeyValue(1, 1);

            IKeyValue keyValue2 = createKeyValue(1, 2);

            IKeyValue keyValue3 = createKeyValue(1, 3);

            List primaryKeys = new ArrayList();
            primaryKeys.add(keyValue1);
            primaryKeys.add(keyValue2);
            primaryKeys.add(keyValue3);

            List objects = this.reader.readListByPrimaryKeys(
                    this.mapping, primaryKeys,
                    this.generator.generateReadListByPrimaryKeysStatement(this.mapping, primaryKeys.size()) + " order by id2",
                    this.connection);

            assertEquals(3, objects.size(), "should contain 3 objects");

            CompoundPkObject object = (CompoundPkObject) objects.get(0);
            assertEquals(1, object.getId(), "id  should be 1");
            assertEquals(1, object.getId2(), "id2 should be 1");

            object = (CompoundPkObject) objects.get(1);
            assertEquals(1, object.getId(), "id  should be 1");
            assertEquals(2, object.getId2(), "id2 should be 2");

            object = (CompoundPkObject) objects.get(2);
            assertEquals(1, object.getId(), "id  should be 1");
            assertEquals(3, object.getId2(), "id2 should be 3");

            primaryKeys.remove(2);
            objects = this.reader.readListByPrimaryKeys(
                    this.mapping, primaryKeys,
                    this.generator.generateReadListByPrimaryKeysStatement(this.mapping, primaryKeys.size()) + " order by id2",
                    this.connection);

            assertEquals(2, objects.size(), "should contain 2 objects");

            object = (CompoundPkObject) objects.get(0);
            assertEquals(1, object.getId(), "id  should be 1");
            assertEquals(1, object.getId2(), "id2 should be 1");

            object = (CompoundPkObject) objects.get(1);
            assertEquals(1, object.getId(), "id  should be 1");
            assertEquals(2, object.getId2(), "id2 should be 2");

            primaryKeys.clear();
            keyValue1 = createKeyValue(1, 4);
            primaryKeys.add(keyValue1);
            objects = this.reader.readListByPrimaryKeys(
                    this.mapping, primaryKeys,
                    this.generator.generateReadListByPrimaryKeysStatement(this.mapping, primaryKeys.size()) + " order by id2",
                    this.connection);
            assertEquals(0, objects.size(), "should be empty");

        } finally {
            Environment.executeSql(DELETE);
        }
    }

    @Test
    public void testReadListByPrimaryKeys_CompoundKey_Filtered() throws Exception{
        try {
            Environment.executeSql(INSERT1);
            Environment.executeSql(INSERT2);
            Environment.executeSql(INSERT3);
            Environment.executeSql(INSERT4);
            Environment.executeSql(INSERT5);

            IReadFilter filter = new AcceptEveryOtherFilter(3);

            IKeyValue keyValue1 = createKeyValue(1, 1);

            IKeyValue keyValue2 = createKeyValue(1, 2);

            IKeyValue keyValue3 = createKeyValue(1, 3);

            IKeyValue keyValue4 = createKeyValue(1, 4);

            IKeyValue keyValue5 = createKeyValue(1, 5);

            List primaryKeys = new ArrayList();
            primaryKeys.add(keyValue1);
            primaryKeys.add(keyValue2);
            primaryKeys.add(keyValue3);
            primaryKeys.add(keyValue4);
            primaryKeys.add(keyValue5);

            List objects = this.reader.readListByPrimaryKeys(
                    this.mapping, primaryKeys,
                    this.generator.generateReadListByPrimaryKeysStatement(this.mapping, primaryKeys.size()) + " order by id2",
                    this.connection,
                    filter);

              assertEquals(3, objects.size(), "should contain 3 objects");

              CompoundPkObject object = (CompoundPkObject) objects.get(0);
              assertEquals(1, object.getId(), "id  should be 1");
              assertEquals(1, object.getId2(), "id2 should be 1");

              object = (CompoundPkObject) objects.get(1);
              assertEquals(1, object.getId(), "id  should be 1");
              assertEquals(3, object.getId2(), "id2 should be 3");

              object = (CompoundPkObject) objects.get(2);
              assertEquals(1, object.getId(), "id  should be 1");
              assertEquals(5, object.getId2(), "id2 should be 5");

            filter = new AcceptEveryOtherFilter(3);
            primaryKeys.remove(4);
            objects = this.reader.readListByPrimaryKeys(
                    this.mapping, primaryKeys,
                    this.generator.generateReadListByPrimaryKeysStatement(this.mapping, primaryKeys.size()) + " order by id2",
                    this.connection,
                    filter);

            assertEquals(2, objects.size(), "should contain 2 objects");

            object = (CompoundPkObject) objects.get(0);
            assertEquals(1, object.getId(), "id  should be 1");
            assertEquals(1, object.getId2(), "id2 should be 1");

            object = (CompoundPkObject) objects.get(1);
            assertEquals(1, object.getId(), "id  should be 1");
            assertEquals(3, object.getId2(), "id2 should be 3");

            filter = new AcceptEveryOtherFilter(3);
            primaryKeys.clear();
            keyValue1 = createKeyValue(1, 6);
            primaryKeys.add(keyValue1);
            objects = this.reader.readListByPrimaryKeys(
                    this.mapping, primaryKeys,
                    this.generator.generateReadListByPrimaryKeysStatement(this.mapping, primaryKeys.size()) + " order by id2",
                    this.connection,
                    filter);
            assertEquals(0, objects.size(), "should be empty");


        } finally {
            Environment.executeSql(DELETE);
        }
    }

    private KeyValue createKeyValue(long id1, long id2) {
        KeyValue keyValue = new KeyValue();
        if(Environment.isHsqldb()){
            keyValue.addColumnValue("ID", new Long(id1));
            keyValue.addColumnValue("ID2", new Long(id2));
        } else {
            keyValue.addColumnValue("id", new Long(id1));
            keyValue.addColumnValue("id2", new Long(id2));
        }
        return keyValue;
    }


}
