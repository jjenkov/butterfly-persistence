package com.jenkov.db.test.util;

import com.jenkov.db.impl.mapping.Key;
import com.jenkov.db.impl.mapping.KeyValue;
import com.jenkov.db.impl.mapping.ObjectMappingFactory;
import com.jenkov.db.itf.mapping.*;
import com.jenkov.db.test.objects.PersistentObject;
import com.jenkov.db.util.MappingUtil;
import com.jenkov.testing.mock.impl.MethodInvocation;
import com.jenkov.testing.mock.impl.MockFactory;
import com.jenkov.testing.mock.itf.IMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.PreparedStatement;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Jakob Jenkov - Copyright 2005 Jenkov Development
 */
public class MappingUtilTest{

    private IObjectMappingFactory mappingFactory = new ObjectMappingFactory();


    private IObjectMapping mapping = null;


    @BeforeEach
    protected void setUp() throws Exception {
        mapping = mappingFactory.createObjectMapping();

        IKey primaryKey = new Key();
        primaryKey.setTable("persistent_object");
        primaryKey.addColumn("id");
        primaryKey.addColumn("name");

        mapping.setPrimaryKey(primaryKey);

        IGetterMapping getterMapping = this.mappingFactory.createGetterMapping(long.class);
        getterMapping.setColumnName("id");
        getterMapping.setObjectMethod(PersistentObject.class.getMethod("getId", null));
        getterMapping.setTableMapped(true);
        mapping.addGetterMapping(getterMapping);

        getterMapping = this.mappingFactory.createGetterMapping(String.class);
        getterMapping.setColumnName("name");
        getterMapping.setObjectMethod(PersistentObject.class.getMethod("getName", null));
        getterMapping.setTableMapped(true);
        mapping.addGetterMapping(getterMapping);

    }


    @Test
    public void testInsertPrimaryKey() throws Exception{

        PreparedStatement statement = (PreparedStatement) MockFactory.createProxy(PreparedStatement.class);
        IMock invocationHandler = MockFactory.getMock(statement);

        IKeyValue keyValue = new KeyValue();
        keyValue.addColumnValue("id"  , new Long(99));
        keyValue.addColumnValue("name", "aName");

        MappingUtil.insertPrimaryKey(this.mapping, keyValue, statement, 1);

        MethodInvocation invocation = new MethodInvocation("setLong",
                new Class[]{int.class, long.class}, new Object[]{new Integer(1), new Long(99)});
        invocationHandler.assertInvoked(invocation);

        invocation = new MethodInvocation("setString",
                new Class[]{int.class, String.class}, new Object[]{new Integer(2), "aName"});
        invocationHandler.assertInvoked(invocation);

        this.mapping.getPrimaryKey().removeColumn("name");
        invocationHandler.clear();
        MappingUtil.insertPrimaryKey(this.mapping, keyValue, statement, 1);
        invocation = new MethodInvocation("setLong",
                new Class[]{int.class, long.class}, new Object[]{new Integer(1), new Long(99)});
        invocationHandler.assertInvoked(invocation);

        this.mapping.getPrimaryKey().removeColumn("id");
        invocationHandler.clear();
        MappingUtil.insertPrimaryKey(this.mapping, keyValue, statement, 1);
        assertEquals(0, invocationHandler.getInvocations().size(), "should be no invcations");

        
    }





}
