package com.jenkov.db.test.filter;

import junit.framework.TestCase;
import com.jenkov.db.impl.filter.PageReadFilter;
import com.jenkov.db.itf.PersistenceException;
import com.jenkov.testing.mock.impl.MockFactory;
import com.jenkov.testing.mock.impl.MethodInvocation;
import com.jenkov.testing.mock.itf.IMock;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Jakob Jenkov - Copyright 2005 Jenkov Development
 */
public class PagedReadFilterTest extends TestCase {

    public void testInit() throws SQLException, PersistenceException {
        PageReadFilter filter = new PageReadFilter(10, 15);
        ResultSet      result = (ResultSet) MockFactory.createProxy(ResultSet.class);
        IMock          resultMock = MockFactory.getMock(result);

        resultMock.addReturnValue(new Integer(ResultSet.TYPE_FORWARD_ONLY));
        filter.init(result);
        resultMock.assertInvoked(new MethodInvocation("next"));
        resultMock.assertInvoked(new MethodInvocation("getType"));
        assertEquals(10 * 15 + 2, resultMock.getInvocations().size());

        resultMock.clear();
        filter = new PageReadFilter(10,15);
        resultMock.addReturnValue(new Integer(ResultSet.TYPE_SCROLL_INSENSITIVE));
        filter.init(result);
//        System.out.println(resultMock.getInvocations());
        resultMock.assertInvoked(new MethodInvocation("absolute", new Class[]{int.class}, new Object[]{new Integer(151)}));
        resultMock.assertInvoked(new MethodInvocation("getType"));
        assertEquals(2, resultMock.getInvocations().size());
    }

    public void testAccept() throws SQLException, PersistenceException {
        PageReadFilter filter = new PageReadFilter(10, 15);
        ResultSet      result = (ResultSet) MockFactory.createProxy(ResultSet.class);
        IMock          resultMock = MockFactory.getMock(result);

        for(int i=0; i<15; i++){
            assertTrue(filter.acceptMore());
            assertTrue(filter.accept(result));
        }
        assertFalse(filter.accept(result));
        assertFalse(filter.acceptMore());

        filter = new PageReadFilter(10, 15);
        for(int i=0; i<15*2; i++){
            assertTrue(filter.acceptMore());
            assertTrue(filter.accept(result));
            if(i%2 == 0){
                filter.acceptedByAllFilters(false);
            } else {
                filter.acceptedByAllFilters(true);
            }
        }
        assertFalse(filter.accept(result));
        assertFalse(filter.acceptMore());

    }

    /**
     * PageReadFilter.clear() doesn't currently do anything. This test is included for coverage reasons.
     */
    public void testClear(){
        PageReadFilter filter = new PageReadFilter(10, 15);
        filter.clear();
    }
}
