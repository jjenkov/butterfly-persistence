package com.jenkov.db.test;

import com.jenkov.db.impl.ObjectWriter;
import com.jenkov.db.impl.SqlGenerator;
import com.jenkov.db.impl.ObjectReader;
import com.jenkov.db.impl.mapping.ObjectMapper;
import com.jenkov.db.impl.mapping.KeyValue;
import com.jenkov.db.impl.mapping.ObjectMappingFactory;
import com.jenkov.db.itf.*;
import com.jenkov.db.itf.mapping.IObjectMapping;
import com.jenkov.db.itf.mapping.IKeyValue;
import com.jenkov.db.test.objects.CompoundPkObject;
import com.jenkov.db.util.JdbcUtil;

import java.sql.Connection;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Jakob Jenkov - Copyright 2005 Jenkov Development
 */
public class ObjectWriterCompoundKeyTest {

    String INSERT1 = "insert into compound_pk_objects(id, id2, name) values(1,1, 'name1')";
    String INSERT2 = "insert into compound_pk_objects(id, id2, name) values(1,2, 'name2')";
    String INSERT3 = "insert into compound_pk_objects(id, id2, name) values(1,3, 'name3')";
    String INSERT4 = "insert into compound_pk_objects(id, id2, name) values(1,4, 'name4')";
    String INSERT5 = "insert into compound_pk_objects(id, id2, name) values(1,5, 'name5')";

    String DELETE  = "delete from compound_pk_objects";

    protected Connection     connection = null;
    protected IObjectReader  reader     = null;
    protected IObjectWriter  writer     = null;
    protected ISqlGenerator  generator  = null;
    protected IObjectMapping mapping    = null;


    protected void setUp() throws Exception {
        this.connection = Environment.getConnection();
        this.reader     = new ObjectReader();
        this.writer     = new ObjectWriter();
        this.writer.setDatabase(Database.determineDatabase(this.connection));
        this.generator  = new SqlGenerator();
        this.mapping    = new ObjectMapper(new ObjectMappingFactory()).mapToTable(CompoundPkObject.class, null, this.connection, null, null);
        Environment.executeSql(DELETE);
    }

    protected void tearDown() throws Exception {
        JdbcUtil.closeIgnore(this.connection);
        this.reader    = null;
        this.writer    = null;
        this.generator = null;
        this.mapping   = null;

        Environment.executeSql(DELETE);
    }


    public void testUpdate_Compound() throws Exception{
        try {
            Environment.executeSql(INSERT1);
            Environment.executeSql(INSERT2);

            CompoundPkObject object = createCompoundKeyObject(1,1, "updated");

            int updateCount = this.writer.update(this.mapping, object,
                    this.generator.generateUpdateStatement(this.mapping), this.connection).getAffectedRecords()[0];

            //check that the record was indeed updated.
            assertEquals(1 ,updateCount, "update count should be 1");
            CompoundPkObject object2 = readByPrimaryKey(createKeyValue(1, 1));
            assertCorrect(object2, 1, 1, "updated");

            //check that the other record in the table has not been updated too by mistake.
            object2 = readByPrimaryKey(createKeyValue(1, 2));
            assertCorrect(object2, 1, 2, "name2");

            //check that no records are affected when primary key doesn't match
            //any key in the database.
            object = createCompoundKeyObject(1,3, "updated");
            int updateCount2 = this.writer.update(this.mapping, object,
                    this.generator.generateUpdateStatement(this.mapping), this.connection).getAffectedRecords()[0];
            assertEquals(0, updateCount2, "update count should be 0");
            object2 = readByPrimaryKey(createKeyValue(1,3));
            assertNull(object2);
        } finally {
            Environment.executeSql(DELETE);
        }
    }


    public void testUpdate_OldPK_Compound() throws Exception{
        Environment.executeSql(INSERT1);
        Environment.executeSql(INSERT2);

        CompoundPkObject object1 = createCompoundKeyObject(1, 3, "name3");

        int updateCount = this.writer.update(this.mapping, object1, createKeyValue(1,1),
                this.generator.generateUpdateStatement(mapping),  this.connection).getAffectedRecords()[0];
        assertEquals(1, updateCount, "should update 1 record");

        CompoundPkObject objectRead1 = readByPrimaryKey(createKeyValue(1, 3));
        CompoundPkObject objectRead2 = readByPrimaryKey(createKeyValue(1, 2));;

        assertCorrect(objectRead1, 1, 3, "name3");
        assertCorrect(objectRead2, 1, 2, "name2");


        //check that no records are affected when old primary key doesn't match any records
        object1 = createCompoundKeyObject(1, 10, "name10");
        updateCount = this.writer.update(this.mapping, object1, createKeyValue(1, 20),
                this.generator.generateUpdateStatement(mapping),  this.connection).getAffectedRecords()[0];
        assertEquals(0, updateCount, "should update 1 record");

        objectRead1 = readByPrimaryKey(createKeyValue(1, 3));
        objectRead2 = readByPrimaryKey(createKeyValue(1, 2));;

        assertCorrect(objectRead1, 1, 3, "name3");
        assertCorrect(objectRead2, 1, 2, "name2");
    }

    public void testUpdateBatch_Compound() throws Exception{
        Environment.executeSql(INSERT1);
        Environment.executeSql(INSERT2);
        Environment.executeSql(INSERT3);

        CompoundPkObject object1 = createCompoundKeyObject(1, 1, "updated1");
        CompoundPkObject object2 = createCompoundKeyObject(1, 2, "updated2");

        List objects = new ArrayList();
        objects.add(object1);
        objects.add(object2);

        int[] updateCounts = this.writer.updateBatch(this.mapping, objects,
                this.generator.generateUpdateStatement(this.mapping), this.connection).getAffectedRecords();

        //check that the record was indeed updated.
        assertEquals(1 , updateCounts[0], "update count should be 1");
        assertEquals(1 , updateCounts[1], "update count should be 1");
        CompoundPkObject objectRead1 = readByPrimaryKey(createKeyValue(1, 1));
        CompoundPkObject objectRead2 = readByPrimaryKey(createKeyValue(1, 2));

        assertCorrect(objectRead1, 1, 1, "updated1" );
        assertCorrect(objectRead2, 1, 2, "updated2" );


        //check that the other record in the table has not been updated too by mistake.
        CompoundPkObject objectRead3 = readByPrimaryKey(createKeyValue(1, 3));
        assertCorrect(objectRead3, 1, 3, "name3");

        //check that no records are affected when primary key doesn't match
        //any key in the database.
        object1 = createCompoundKeyObject(1, 6, "updated6");
        objects.clear();
        objects.add(object1);
        updateCounts = this.writer.updateBatch(this.mapping, objects,
                this.generator.generateUpdateStatement(this.mapping), this.connection).getAffectedRecords();
        assertEquals(0, updateCounts[0], "update count should be 0");

        objectRead1 = readByPrimaryKey(createKeyValue(1, 1));
        objectRead2 = readByPrimaryKey(createKeyValue(1, 2));
        objectRead3 = readByPrimaryKey(createKeyValue(1, 3));

        assertCorrect(objectRead1, 1, 1, "updated1");
        assertCorrect(objectRead2, 1, 2, "updated2");
        assertCorrect(objectRead3, 1, 3, "name3");
    }

    public void testUpdateBacth_OldPK_Compound() throws Exception{
        Environment.executeSql(INSERT1);
        Environment.executeSql(INSERT2);
        Environment.executeSql(INSERT3);

        CompoundPkObject object1 = createCompoundKeyObject(1, 11, "updated1");
        CompoundPkObject object2 = createCompoundKeyObject(1, 12, "updated2");

        List objects = new ArrayList();
        objects.add(object1);
        objects.add(object2);

        List primaryKeys = new ArrayList();
        primaryKeys.add(createKeyValue(1,1));
        primaryKeys.add(createKeyValue(1,2));

        int[] updateCounts = this.writer.updateBatch(this.mapping, objects, primaryKeys,
                this.generator.generateUpdateStatement(this.mapping), this.connection).getAffectedRecords();

        //check that the record was indeed updated.
        assertEquals(1 , updateCounts[0], "update count should be 1");
        assertEquals(1 , updateCounts[1], "update count should be 1");
        CompoundPkObject objectRead1 = readByPrimaryKey(createKeyValue(1, 11));
        CompoundPkObject objectRead2 = readByPrimaryKey(createKeyValue(1, 12));

        assertCorrect(objectRead1, 1, 11, "updated1" );
        assertCorrect(objectRead2, 1, 12, "updated2" );


        //check that the other record in the table has not been updated too by mistake.
        CompoundPkObject objectRead3 = readByPrimaryKey(createKeyValue(1, 3));
        assertCorrect(objectRead3, 1, 3, "name3");

        //check that no records are affected when primary key doesn't match
        //any key in the database.
        objects.clear();
        objects.add(createCompoundKeyObject(1, 6, "updated6"));
        primaryKeys.clear();
        primaryKeys.add(createKeyValue(1,6));
        updateCounts = this.writer.updateBatch(this.mapping, objects, primaryKeys,
                this.generator.generateUpdateStatement(this.mapping), this.connection).getAffectedRecords();
        assertEquals(0, updateCounts[0], "update count should be 0");

        objectRead1 = readByPrimaryKey(createKeyValue(1, 11));
        objectRead2 = readByPrimaryKey(createKeyValue(1, 12));
        objectRead3 = readByPrimaryKey(createKeyValue(1, 3));

        assertCorrect(objectRead1, 1, 11, "updated1");
        assertCorrect(objectRead2, 1, 12, "updated2");
        assertCorrect(objectRead3, 1, 3, "name3");
    }

    public void testDelete_Compound() throws Exception{
        Environment.executeSql(INSERT1);
        Environment.executeSql(INSERT2);
        Environment.executeSql(INSERT3);

        CompoundPkObject object1 = createCompoundKeyObject(1, 1, "deleted1");
        int updateCount = this.writer.delete(this.mapping, object1,
                this.generator.generateDeleteStatement(mapping), this.connection).getAffectedRecords()[0];
        assertEquals(1, updateCount, "update count should be 1");
        object1 = readByPrimaryKey(createKeyValue(1, 1));
        assertNull(object1, "object should be deleted");

        assertCorrect(readByPrimaryKey(createKeyValue(1,2)), 1, 2, "name2");
        assertCorrect(readByPrimaryKey(createKeyValue(1,3)), 1, 3, "name3");

        object1 = createCompoundKeyObject(1, 2, "deleted2");
        updateCount = this.writer.delete(this.mapping, object1,
                this.generator.generateDeleteStatement(this.mapping), this.connection).getAffectedRecords()[0];
        assertEquals(1, updateCount, "update count should be 1");
        assertNull(readByPrimaryKey(createKeyValue(1,2)), "object should be null");
        assertCorrect(readByPrimaryKey(createKeyValue(1,3)), 1, 3, "name3");

        object1 = createCompoundKeyObject(1, 3, "deleted3");
        updateCount = this.writer.delete(this.mapping, object1,
                this.generator.generateDeleteStatement(this.mapping), this.connection).getAffectedRecords()[0];
        assertEquals(1, updateCount, "update count should be 1");
        assertNull(readByPrimaryKey(createKeyValue(1,3)), "object should be null");
    }

    public void testDeleteByPrimaryKey_Compound() throws Exception{
        Environment.executeSql(INSERT1);
        Environment.executeSql(INSERT2);
        Environment.executeSql(INSERT3);

        int updateCount = this.writer.deleteByPrimaryKey(this.mapping, createKeyValue(1, 1),
                this.generator.generateDeleteStatement(mapping), this.connection).getAffectedRecords()[0];
        assertEquals(1, updateCount, "update count should be 1");
        assertNull(readByPrimaryKey(createKeyValue(1, 1)), "object should be deleted");

        assertCorrect(readByPrimaryKey(createKeyValue(1,2)), 1, 2, "name2");
        assertCorrect(readByPrimaryKey(createKeyValue(1,3)), 1, 3, "name3");

        updateCount = this.writer.deleteByPrimaryKey(this.mapping, createKeyValue(1, 2),
                this.generator.generateDeleteStatement(this.mapping), this.connection).getAffectedRecords()[0];
        assertEquals(1, updateCount, "update count should be 1");
        assertNull(readByPrimaryKey(createKeyValue(1,2)), "object should be null");
        assertCorrect(readByPrimaryKey(createKeyValue(1,3)), 1, 3, "name3");

        updateCount = this.writer.deleteByPrimaryKey(this.mapping, createKeyValue(1, 3),
                this.generator.generateDeleteStatement(this.mapping), this.connection).getAffectedRecords()[0];
        assertEquals(1, updateCount, "update count should be 1");
        assertNull(readByPrimaryKey(createKeyValue(1,3)), "object should be null");
    }

    public void testDeleteBatch_Compound() throws Exception{
        Environment.executeSql(INSERT1);
        Environment.executeSql(INSERT2);
        Environment.executeSql(INSERT3);

        List objects = new ArrayList();
        objects.add(createCompoundKeyObject(1, 1, "deleted1"));
        objects.add(createCompoundKeyObject(1, 2, "deleted2"));

        int[] updateCounts = this.writer.deleteBatch(this.mapping, objects,
                this.generator.generateDeleteStatement(this.mapping), this.connection).getAffectedRecords();

        assertEquals(1, updateCounts[0], "update count should be 1");
        assertEquals(1, updateCounts[1], "update count should be 1");

        assertNull(readByPrimaryKey(createKeyValue(1, 1)), "object 1 should be null");
        assertNull(readByPrimaryKey(createKeyValue(1, 2)), "object 2 should be null");

        assertCorrect(readByPrimaryKey(createKeyValue(1, 3)), 1, 3, "name3");
    }

    public void testDeleteByPrimaryKeysBatch_Compound() throws Exception{
        Environment.executeSql(INSERT1);
        Environment.executeSql(INSERT2);
        Environment.executeSql(INSERT3);

        List primaryKeys = new ArrayList();
        primaryKeys.add(createKeyValue(1, 1));
        primaryKeys.add(createKeyValue(1, 2));

        int[] updateCounts = this.writer.deleteByPrimaryKeysBatch(this.mapping, primaryKeys,
                this.generator.generateDeleteStatement(this.mapping), this.connection).getAffectedRecords();

        assertEquals(1, updateCounts[0], "update count should be 1");
        assertEquals(1, updateCounts[1], "update count should be 1");

        assertNull(readByPrimaryKey(createKeyValue(1, 1)), "object 1 should be null");
        assertNull(readByPrimaryKey(createKeyValue(1, 2)), "object 2 should be null");

        assertCorrect(readByPrimaryKey(createKeyValue(1, 3)), 1, 3, "name3");
    }


    private CompoundPkObject createCompoundKeyObject(long id, long id2, String name) {
        CompoundPkObject object = new CompoundPkObject();
        object.setId(id);
        object.setId2(id2);
        object.setName(name);
        return object;
    }

    private IKeyValue createKeyValue(long id, long id2) {
        IKeyValue keyValue = new KeyValue();
        if(Environment.isHsqldb()){
            keyValue.addColumnValue("ID" , new Long(id));
            keyValue.addColumnValue("ID2", new Long(id2));
        } else {
            keyValue.addColumnValue("id" , new Long(id));
            keyValue.addColumnValue("id2", new Long(id2));
        }
        return keyValue;
    }

    private void assertCorrect(CompoundPkObject object, long id, long id2, String name) {
        assertEquals(id, object.getId(), "should have same id");
        assertEquals(id2, object.getId2(), "should have same id2");
        assertEquals(name, object.getName(), "should have same name");
    }

    private CompoundPkObject readByPrimaryKey(IKeyValue keyValue) throws PersistenceException {
        return (CompoundPkObject) this.reader.readByPrimaryKey(this.mapping, keyValue,
                this.generator.generateReadByPrimaryKeyStatement(this.mapping), this.connection );
    }

}
