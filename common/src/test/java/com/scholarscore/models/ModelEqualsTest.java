package com.scholarscore.models;

import com.scholarscore.models.query.expressions.Expression;
import com.scholarscore.models.query.expressions.operands.DimensionOperand;
import com.scholarscore.models.query.expressions.operands.IOperand;
import com.scholarscore.models.query.expressions.operands.OperandType;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.testng.annotations.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.testng.Assert.assertNotEquals;

/**
 * User: jordan
 * Date: 9/26/15
 * Time: 5:50 PM
 */
@Test(groups = { "unit" })
public class ModelEqualsTest {

    private int numberOfFailedDefaultFieldAttempts = 0;
    
    private static final String packageToScan = "com.scholarscore.models";
    private static final Set<String> excludedClassNames = new HashSet<String>() {{
        // TODO: examine why these classes aren't working
        add(packageToScan + ".StudentSectionGrade");
        add(packageToScan + ".query.expressions.operators.BinaryOperator");
    }};
    
    private Set<Class<?>> getClassesInPackage(String packageToScan) {
        List<ClassLoader> classLoadersList = new LinkedList<ClassLoader>();
        classLoadersList.add(ClasspathHelper.contextClassLoader());
        classLoadersList.add(ClasspathHelper.staticClassLoader());

        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setScanners(new SubTypesScanner(false /* don't exclude Object.class */), new ResourcesScanner())
                .setUrls(ClasspathHelper.forClassLoader(classLoadersList.toArray(new ClassLoader[0])))
                .filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix(packageToScan))));

        return reflections.getSubTypesOf(Object.class);
    }

    @Test
    public void testModelClassesEqualsBehavior() {
        // TODO Jordan: add reflective package scanning
//        Class[] classes = { Address.class, Assignment.class, EntityId.class, User.class, Administrator.class };

        Set<Class<?>> classes = getClassesInPackage(packageToScan);

        for (Class clazz : classes) {
            System.out.println("Now analyzing class " + clazz);
            checkEqualsForClass(clazz);
            System.out.println("");
        }
        if (numberOfFailedDefaultFieldAttempts <= 0) {
            System.out.println("DONE. No problems.");
        } else {
            System.out.println("DONE. But encountered " + numberOfFailedDefaultFieldAttempts + " inabilities to properly test because lacking sensible default definitions");
        }
    }

    private void checkEqualsForClass(Class clazz) {
        if (Modifier.isAbstract(clazz.getModifiers())) {
            System.out.println("Oops! Class " + clazz + " is abstract, not able to test it. skipping...");
            return;
        } else if (excludedClassNames.contains(clazz.getName())) {
            System.out.println("Oops! Class " + clazz + " is explicitly excluded, skipping...");
            return;
        }

        final Object unmodifiedInstance = buildPopulatedObject(clazz);
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            if (Modifier.isFinal(field.getModifiers()) || Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            Object instanceWithTweakedField = buildPopulatedObject(clazz, field.getName());
            if (instanceWithTweakedField == null) {
                System.out.println("Couldn't build object, skipping...");
                numberOfFailedDefaultFieldAttempts++;
                continue;
            }
            System.out.println("Checking equals() and hashcode() on " + clazz.getName() + " with field " + field.getName() + " modified...");
            String both = "original: " + unmodifiedInstance + ", tweaked: " + instanceWithTweakedField;
            String objMsg = "For class " + clazz + ", ";
            String equalsMsg = objMsg + "Equals() returned true even though objects have different values for field " + field.getName() + "\n" + both;
            String hashMsg = objMsg + "hashcode() returned identical values even though objects have different values for field " + field.getName() + "\n" + both;
            assertNotEquals(unmodifiedInstance, instanceWithTweakedField, equalsMsg);
            assertNotEquals(unmodifiedInstance.hashCode(), instanceWithTweakedField.hashCode(), hashMsg);
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
                // System.out.println("Discovered field [" + field + "] on class " + clazz);

                Object value;
                if (field.getName().equals(fieldNameToModify)) {
                    value = getAnotherValueForType(field.getType());
                } else {
                    value = getSensibleValueForType(field.getType());
                }

              //  System.out.println("About to set field " + field + /*" on " + instance +*/ " to value " + value);
                if (value == null) {
                    System.out.println("WARNING - default value for field " + field + " appears to be null. Returning NULL because test case is void.");
                    return null;
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

        // enums
        if (type.isAssignableFrom(BehaviorCategory.class)) { return BehaviorCategory.MERIT; }
        if (type.isAssignableFrom(OperandType.class)) { return OperandType.EXPRESSION; }
        if  (type.isAssignableFrom(IOperand.class)) { return new Expression(); }
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
        if (type.isAssignableFrom(BehaviorCategory.class)) { return BehaviorCategory.DEMERIT; }
        if (type.isAssignableFrom(OperandType.class)) { return OperandType.DIMENSION; }
        if  (type.isAssignableFrom(IOperand.class)) { return new DimensionOperand(); }
        else return null;
    }
}
