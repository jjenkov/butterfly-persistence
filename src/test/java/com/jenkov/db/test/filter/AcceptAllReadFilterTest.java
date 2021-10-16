package com.jenkov.db.test.filter;

import com.jenkov.db.impl.filter.AcceptAllReadFilter;
import org.junit.jupiter.api.Test;

/**
 * @author Jakob Jenkov - Copyright 2005 Jenkov Development
 */
public class AcceptAllReadFilterTest {

    @Test
    public void testAll(){
        AcceptAllReadFilter filter = new AcceptAllReadFilter();
        filter.acceptedByAllFilters(true);
        filter.clear();
    }
}
