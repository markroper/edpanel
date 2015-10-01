package com.scholarscore.models;

import com.scholarscore.models.query.Dimension;
import com.scholarscore.models.query.DimensionField;
import com.scholarscore.models.query.Measure;
import com.scholarscore.models.query.MeasureField;
import com.scholarscore.models.query.expressions.Expression;
import com.scholarscore.models.query.expressions.operands.DimensionOperand;
import com.scholarscore.models.query.expressions.operands.IOperand;
import com.scholarscore.models.query.expressions.operands.OperandType;
import com.scholarscore.models.user.User;

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
public class ModelEqualsAndHashcodeTest {

    private int numberOfFailedDefaultFieldAttempts = 0;
    private Set<String> fieldsThatNeedDefaults = new HashSet<>();

    private final String packageToScan = "com.scholarscore.models";
    private final Set<String> excludedClassNames = new HashSet<String>() {{
        // if you want to exclude a model class from this test, add it here. e.g...
        add(packageToScan + ".ModelEqualsAndHashcodeTest");
    }};
    
    public String getPackageToScan() {
        return packageToScan;
    }
    
    public Set<String> getExcludedClassNames() { 
        return excludedClassNames;
    }    
    
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
        Set<Class<?>> classes = getClassesInPackage(getPackageToScan());

        for (Class clazz : classes) {
            //System.out.println("Now analyzing class " + clazz);
            checkEqualsForClass(clazz);
            //System.out.println("");
        }
        if (numberOfFailedDefaultFieldAttempts <= 0) {
            System.out.println("DONE. No problems.");
        } else {
            System.out.println("DONE. " 
                    + "But encountered "
                    + fieldsThatNeedDefaults.size() + " unique failures " 
                    + "(" + numberOfFailedDefaultFieldAttempts + " total)"
                    + " to construct objects with sensible default values."
                    + "\n(To fix this, please add a line to each method getSensibleValueForType and getAnotherValueForType)"
                    + "\n\n!!!!!!!!!!!!!!!!!!!!!!!!\n" +
                    "The following fields need sensible defaults defined in getValueForType in ModelEqualsAndHashcodeTest class:"
                    + "\n!!!!!!!!!!!!!!!!!!!!!!!!"
            );
            for (String field : fieldsThatNeedDefaults) {
                System.out.println(field);
            }
        }
    }

    private void checkEqualsForClass(Class clazz) {
        if (Modifier.isAbstract(clazz.getModifiers())) {
            System.out.println("Oops! Class " + clazz + " is abstract, not able to test it. skipping...");
            return;
        } else if (getExcludedClassNames().contains(clazz.getName())) {
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
                continue;
            }
            // System.out.println("Checking equals() and hashcode() on " + clazz.getName() + " with field " + field.getName() + " modified...");
            String both = "original: " + unmodifiedInstance + ", tweaked: " + instanceWithTweakedField;
            String objMsg = "For class " + clazz + ", ";
            String equalsMsg = objMsg + "Equals() returned true even though objects have different values for field " + field.getName() + "\n" + both;
            String hashMsg = objMsg + "hashcode() returned identical values even though objects have different values for field " + field.getName() + "\n" + both;
            assertNotEquals(unmodifiedInstance, instanceWithTweakedField, equalsMsg);
            assertNotEquals(unmodifiedInstance.hashCode(), instanceWithTweakedField.hashCode(), hashMsg);
        }
    }
    
    // ad hoc (saves a bunch of lines in a method below)
    private Object buildPopulatedObject(Class clazz, String fieldNameToModify, boolean doModification) {
        if (doModification) {
            return buildPopulatedObject(clazz, fieldNameToModify);
        } else {
            return buildPopulatedObject(clazz);
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
                    // System.out.println("WARNING - default value for field " + field + " appears to be null." 
                    // + " Returning NULL because test case is void.");
                    fieldsThatNeedDefaults.add(field.toString());
                    numberOfFailedDefaultFieldAttempts++;
                    // if can't get default for any fields, assume the test is screwed for this instance
                    return null;
                }
                field.setAccessible(true);
                field.set(instance, value);
            }
            return instance;
        } catch (InstantiationException|IllegalAccessException e) {
//            e.printStackTrace();
        }
        return null;
    }

    private Object getSensibleValueForType(Class<?> type) {
        return getValueForType(type, false);
    }

    private Object getAnotherValueForType(Class<?> type) {
        return getValueForType(type, true);
    }
    
    private Object getValueForType(Class<?> type, boolean alt) {
        if (type.isAssignableFrom(Double.class)) { return alt ? 777D : 76D; }
        if (type.isAssignableFrom(Long.class)) { return alt ? 22L : 2L; }
        if (type.isAssignableFrom(String.class)) { return alt ? "anotherStringValue" : "stringValue"; }
        if (type.isAssignableFrom(Boolean.class)) { return !alt; }
        if (type.isAssignableFrom(Integer.class)) { return alt ? 33: 3; }
        if (type.isAssignableFrom(Date.class)) { return alt ? new Date(1322462400000L) : new Date(1442462400000L); }

        if (type.isAssignableFrom(IOperand.class)) { return alt ? new DimensionOperand() : new Expression(); }

        // in some cases, don't trace through the whole map because it's complex (and possibly circular) and we're lazy
        // but hey, better than nothing.
        if (type.isAssignableFrom(School.class)) { School school = new School();
            if (alt) {
                school.setMainPhone("3217654098");
            } else {
                school.setMainPhone("1234567890");
            }
            return school;
        }

        if (type.isAssignableFrom(Address.class)) { return buildPopulatedObject(Address.class, "postalCode", alt); }
        if (type.isAssignableFrom(MeasureField.class)) { return buildPopulatedObject(MeasureField.class, "field", alt); }
        if (type.isAssignableFrom(User.class)) { return buildPopulatedObject(User.class, "password", alt); }
        if (type.isAssignableFrom(DimensionField.class)) { return buildPopulatedObject(DimensionField.class, "field", alt); }

        if (type.isAssignableFrom(BehaviorCategory.class)) { return alt ? BehaviorCategory.DEMERIT : BehaviorCategory.MERIT; }
        if (type.isAssignableFrom(OperandType.class)) { return alt ? OperandType.DIMENSION : OperandType.EXPRESSION; }
        if (type.isAssignableFrom(Dimension.class)) { return alt ? Dimension.STUDENT : Dimension.TEACHER; }
        if (type.isAssignableFrom(Gender.class)) { return alt ? Gender.FEMALE : Gender.MALE; }
        if (type.isAssignableFrom(Measure.class)) { return alt ? Measure.DEMERIT : Measure.MERIT; }
        else return null;
    }
}