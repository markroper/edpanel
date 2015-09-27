package com.scholarscore.models;

import org.testng.annotations.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Date;

import static org.testng.Assert.assertNotEquals;

/**
 * User: jordan
 * Date: 9/26/15
 * Time: 5:50 PM
 */
@Test(groups = { "unit" })
public class ModelEqualsTest {
    
    @Test
    public void testModelClassesEqualsBehavior() { 

        // TODO Jordan: add reflective package scanning
        Class[] classes = { Address.class, Assignment.class, EntityId.class, User.class, Administrator.class };
        
        for (Class clazz : classes) {
            System.out.println("Now analyzing class " + clazz);
            handleClass(clazz);
            System.out.println("");
        }
    }

    private void handleClass(Class clazz) {
        if (Modifier.isAbstract(clazz.getModifiers())) {
            System.out.println("Oops! Class " + clazz + " is abstract, not able to test it. skipping...");
            return;
        }

        final Object unmodifiedInstance = buildPopulatedObject(clazz);
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            if (Modifier.isFinal(field.getModifiers()) || Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            Object instanceWithTweakedField = buildPopulatedObject(clazz, field.getName());
            System.out.println("---Checking that equals() should be false when using duplicate object with field " + field + " modified");
            System.out.println("---unmodified: " + unmodifiedInstance + ", tweaked: " + instanceWithTweakedField);
            String msg = "Equals() returned true even though objects have different values for field " + field.getName();
            assertNotEquals(unmodifiedInstance, instanceWithTweakedField, msg);
        }
    }
    
    private Object buildPopulatedObject(Class clazz) {
        return buildPopulatedObject(clazz, null);
    }
    
    private Object buildPopulatedObject(Class clazz, String fieldNameToModify) {
        try {
            Object instance = clazz.newInstance();

            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (Modifier.isFinal(field.getModifiers()) || Modifier.isStatic(field.getModifiers())) {
                    // ignore static/final
                    continue;
                }
                System.out.println("Discovered field [" + field + "] on class " + clazz);

                Object value;
                if (field.getName().equals(fieldNameToModify)) {
                    value = getAnotherValueForType(field.getType());
                } else {
                    value = getSensibleValueForType(field.getType());
                }

                System.out.println("About to set field " + field + /*" on " + instance +*/ " to value " + value);
                if (value == null) {
                    System.out.println("WARNING - default value for field " + field + " appears to be null.");
                }
                field.setAccessible(true);
                field.set(instance, value);
            }
            return instance;
        } catch (InstantiationException|IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Object getSensibleValueForType(Class<?> type) {
        if (type.isAssignableFrom(Long.class)) { return 2L; }
        if (type.isAssignableFrom(String.class)) { return "stringValue"; }
        if (type.isAssignableFrom(Boolean.class)) { return true; }
        if (type.isAssignableFrom(Integer.class)) { return 3; }
        if (type.isAssignableFrom(Date.class)) { return new Date(1442462400000L); }
        if (type.isAssignableFrom(Address.class)) { return buildPopulatedObject(Address.class); }
        else return null;
    }

    private Object getAnotherValueForType(Class<?> type) {
        if (type.isAssignableFrom(Long.class)) { return 22L; }
        if (type.isAssignableFrom(String.class)) { return "anotherStringValue"; }
        if (type.isAssignableFrom(Boolean.class)) { return false; }
        if (type.isAssignableFrom(Integer.class)) { return 33; }
        if (type.isAssignableFrom(Date.class)) { return new Date(1322462400000L); }
        if (type.isAssignableFrom(Address.class)) { 
            Address address = (Address)buildPopulatedObject(Address.class);
            address.setPostalCode("23456");
            return address; 
        }
        else return null;
    }
}
