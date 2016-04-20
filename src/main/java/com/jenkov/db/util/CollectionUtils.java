/**
 * User: Administrator
 */
package com.jenkov.db.util;

import java.util.List;
import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;

public class CollectionUtils {


    /**
     * Converts the given collection to a <code>java.util.List</code>.
     * @param collection The collection to convert to a list.
     * @return A <code>java.util.List</code> containing all of the elements from the
     *         supplied collection.
     */
    public static List toList(Collection collection){
        if(collection instanceof List) return (List) collection;

        List list = new ArrayList();
        Iterator iterator = collection.iterator();
        while(iterator.hasNext()){
            list.add(iterator.next());
        }
        return list;
    }

    public static boolean areEqual(Collection collection1, Collection collection2){
        if(collection1.size() != collection2.size()) return false;

        Iterator iterator = collection1.iterator();
        while(iterator.hasNext()){
            if(! collection2.contains(iterator.next())) return false;
        }
        return true;
    }
}
