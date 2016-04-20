package com.jenkov.db.test.filter;

import com.jenkov.db.impl.filter.AcceptAllReadFilter;
import junit.framework.TestCase;

/**
 * @author Jakob Jenkov - Copyright 2005 Jenkov Development
 */
public class AcceptAllReadFilterTest extends TestCase {

    public void testAll(){
        AcceptAllReadFilter filter = new AcceptAllReadFilter();
        filter.acceptedByAllFilters(true);
        filter.clear();
    }
}
