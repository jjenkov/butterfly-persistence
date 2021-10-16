/**
 * @author Jakob Jenkov,  Jenkov Development
 */
package com.jenkov.db.util;

import java.lang.reflect.Method;

/**
 * This class contains utility methods for use on classes and methods.
 */
public class ClassUtil {


    public static boolean isSubstitutableFor(Class targetClass, Class superClassOrInterface){

        if(targetClass.equals(superClassOrInterface)) return true;
        if(isSubclassOf(targetClass, superClassOrInterface)) return true;

        if(isInterfaceOrSubInterfaceImplemented(targetClass, superClassOrInterface)) return true;

        Class targetSuperClass = targetClass.getSuperclass();
        while(targetSuperClass != null){
            if(isInterfaceOrSubInterfaceImplemented(targetSuperClass, superClassOrInterface)) return true;
            targetSuperClass = targetSuperClass.getSuperclass();
        }
        return false;
    }

    public static boolean isSubclassOf(Class targetClass, Class superClass){
        Class targetSuperClass = targetClass.getSuperclass();
        while(targetSuperClass != null){
            if(targetSuperClass.equals(superClass)) return true;
            targetSuperClass = targetSuperClass.getSuperclass();
        }
        return false;
    }

    public static boolean isInterfaceOrSubInterfaceImplemented(Class targetClass, Class theInterface){
        Class[] implementedInterfaces = targetClass.getInterfaces();
        for(Class implementedInterface : implementedInterfaces){
            if(implementedInterface.equals(theInterface)) return true;
            Class superInterface = implementedInterface.getSuperclass();
            while(superInterface != null){
                if(superInterface.equals(theInterface)) return true;
                superInterface = superInterface.getSuperclass();
            }
        }
        return false;
    }

    /**
     * Returns true if the <code>Method</code> instance is a getter, meaning
     * if it's name starts with "get" or "is", takes no parameters, and
     * returns a value. False if not.
     * @param member The method to check if it is a getter method.
     * @return True if the <code>Method</code> instance is a getter. False if not.
     */
    public static boolean isGetter(Method member){
        if(member == null){
            throw new NullPointerException("No Method instance provided in call to ClassUtil.isGetter(...)");
        }

        if(member.getParameterTypes().length > 0){
            return false;
        }

        if(member.getReturnType() == void.class || member.getReturnType() == null){
            return false;
        }

         return  member.getName().startsWith("get") ||
                 member.getName().startsWith("is");
    }



    /**
     * Returns true if the <code>Method</code> instance is a setter method,
     * meaning if the method name starts with "set" and it takes exactly
     * one parameter.
     * @param member The <code>Method</code> instance to check if is a setter method.
     * @return True if the <code>Method</code> instance is a setter. False if not.
     */
    public static boolean isSetter(Method member){
        if(member == null){
            throw new NullPointerException("No Method instance provided in call to ClassUtil.isSetter(...)");
        }

        if(!member.getName().startsWith("set"))    return false;
        if(member.getParameterTypes().length != 1) return false;
        return true;
    }


    /**
     * Returns the name of a class with the package name cut off.
     * @param objectClass The class to get the class name without package name for.
     * @return The name of a class with the package name cut off.
     */
    public static String classNameWithoutPackage(Class objectClass){
        return classNameWithoutPackage(objectClass.getName());

    }

    /**
     * Returns the class name without the package name of the supplied class name.
     * @param fullClassName The fully qualified class name to get the class name of.
     * @return The class name without the package name of the supplied class name. 
     */
    public static String classNameWithoutPackage(String fullClassName){
        return fullClassName.substring(fullClassName.lastIndexOf(".")+1, fullClassName.length());
    }


    public static String toString(Object[] array){
        StringBuffer buffer = new StringBuffer(30);

        buffer.append("[");
        for(int i=0; i< array.length; i++){
            buffer.append(array[i].toString());
            if(i < array.length - 1){
                buffer.append(", ");
            }
        }
        buffer.append("]");
        return buffer.toString();
    }


    /**
     * Compares to objects to each other, where one or both of the objects may be null.
     * If both references are null they are considered equal and 0 is returned.
     * If the first reference is null and the second isn't, 1 is returned
     * If the second reference is null and the first isn't, -1 is returned
     * If both references are not nul, and the first reference is an instance of
     * <code>Comparable</code> o1.compareTo(o2) is returned.
     *
     * 
     * In all other cases 0 is returned, meaning the objects are considered equal, since
     * they cannot be compared. This is useful when comparing various different
     * objects, which can also be null.
     *
     * @param o1 The reference to the first object to compare.
     * @param o2 The reference to the second object to compare.
     * @return 0, 1 or -1 depending on which object is largest, or if they are equal.
     */
    public static int compare(Object o1, Object o2){
        if(o1 == null && o2 == null) return 0;
        if(o1 == null && o2 != null) return 1;
        if(o1 != null && o2 == null) return -1;

        if(o1 instanceof Comparable){
            return ((Comparable) o1).compareTo(o2);
        } else

        return 0;
    }

    public static int compare(Method method1, Method method2){
        int comparison = compare((Object) method1, (Object) method2);
        if(comparison != 0) return comparison;
        
        comparison = method1.getName().compareTo(method2.getName());
        if(comparison != 0) return comparison;

        comparison = method1.getReturnType().getName().compareTo(method2.getReturnType().getName());
        if(comparison != 0) return comparison;

        comparison = method1.getReturnType().getName().compareTo(method2.getReturnType().getName());
        if(comparison != 0) return comparison;

        if(method1.getParameterTypes().length < method2.getParameterTypes().length) return -1;
        if(method1.getParameterTypes().length > method2.getParameterTypes().length) return 1;
        if(method1.getParameterTypes().length == 1){
            comparison = method1.getParameterTypes()[0].getName().compareTo(method2.getParameterTypes()[0].getName());
            if(comparison != 0) return comparison;
        }

        return 0;
    }

    /**
     * Returns true if the two objects are either both null, or equal. If the
     * objects are both non-null equality is determined by the call
     * <code>object1.equals(object2)</code>.
     * @param object1 The first object to test for equality with the second object.
     * @param object2 The second object to test for equality with the first object.
     * @return True if the two objects are either both null, or equal.
     */
    public static boolean areEqual(Object object1, Object object2){
        if(     object1 != null && object2 != null) return (object1.equals(object2));
        else if(object1 == null && object2 != null) return false;
        else if(object1 != null && object2 == null) return false;
        return true;
    }

//    public static boolean areEqual(Object object1, Object object2){
//        if(object1 == null && object2 == null) return true;
//        if(object1 != null && object2 == null) return false;
//        if(object1 == null && object2 != null) return false;
//
//        return object1.equals(object2);
//    }

}