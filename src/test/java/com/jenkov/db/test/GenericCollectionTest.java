package com.jenkov.db.test;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Map;
import java.util.List;

// This class declaration defines a type variable T
public class GenericCollectionTest<T extends Thread> {

   // This method is used in the reflection example below
   public List<String> aMethod(Class<? extends T> clazz1,
                       Class<T> clazz2, T[] ts) {
       return null;
   }

   // Prints information about a type variable

   private static void print(TypeVariable v) {
      System.out.println("Type variable");
      System.out.println("Name: " + v.getName());
      System.out.println("Declaration: " +
                         v.getGenericDeclaration());
      System.out.println("Bounds:");
      for (Type t : v.getBounds()) {
         print(t);
      }
   }
   // Prints information about a wildcard type
   private static void print(WildcardType wt) {
      System.out.println("Wildcard type");
      System.out.println("Lower bounds:");
      for (Type b : wt.getLowerBounds()) {
         print(b);
      }

      System.out.println("Upper bounds:");
      for (Type b : wt.getUpperBounds()) {
         print(b);
      }
   }

   // Prints information about a parameterized type
   private static void print(ParameterizedType pt) {
      System.out.println("Parameterized type");
      System.out.println("Owner: " + pt.getOwnerType());
      System.out.println("Raw type: " + pt.getRawType());

      for (Type actualType : pt.getActualTypeArguments()) {
         print(actualType);
      }
   }

   // Prints information about a generic array type
   private static void print(GenericArrayType gat) {
      System.out.println("Generic array type");
      System.out.println("Type of array: ");
      print(gat.getGenericComponentType());
   }

   /**
    * Prints information about a type. The nested
    * if/else-if chain calls the
    * appropriate overloaded print method for the
    * type. If t is just a Class,
    * we print it directly.
    */

   private static void print(Type t) {
      if (t instanceof TypeVariable) {
         print((TypeVariable)t);
      } else if (t instanceof WildcardType) {
         print((WildcardType)t);
      } else if (t instanceof ParameterizedType) {
         print((ParameterizedType)t);
      } else if (t instanceof GenericArrayType) {
         print((GenericArrayType)t);
      } else {
         System.out.println(t);
      }
   }

   public static void main(String[] args) throws Exception {
      // Some classes we are going to play with
      Class[] classes = new Class[] {Class.class, Map.class,
                                     GenericCollectionTest.class};

      // Iterate the array for each class instance...
      for (Class clazz : classes) {
         // Prints its name and ...
         System.out.println("Class: " + clazz);

         // Iterate for each type variable defined by this class
         for (TypeVariable v : clazz.getTypeParameters()) {
            print(v);
         }

         System.out.println();
      }
      System.out.println("Reflective information " +
                         "about the parameters of aMethod");
      // Iterate for each method...
      for (Method method : GenericCollectionTest.class.getDeclaredMethods()) {
         // Until we find aMethod
         if (method.getName().equals("aMethod")) {
            // Then, go over all parameters ...
            System.out.println("RETURN TYPE");
            for(Type type: ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()){
                print(type);
            }
             System.out.println("RETURN TYPE END");
             for (Type t : method.getGenericParameterTypes()) {
               System.out.println("Parameter:");
               // And print reflexive information about them
               print(t);
               System.out.println();
            }
            break;
         }
      }
   }
}