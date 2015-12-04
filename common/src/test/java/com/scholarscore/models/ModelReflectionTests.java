package com.scholarscore.models;

import com.scholarscore.models.assignment.Assignment;
import com.scholarscore.models.assignment.AssignmentType;
import com.scholarscore.models.assignment.AttendanceAssignment;
import com.scholarscore.models.assignment.GradedAssignment;
import com.scholarscore.models.attendance.AttendanceStatus;
import com.scholarscore.models.attendance.SchoolDay;
import com.scholarscore.models.gradeformula.GradeFormula;
import com.scholarscore.models.query.AggregateFunction;
import com.scholarscore.models.query.Dimension;
import com.scholarscore.models.query.DimensionField;
import com.scholarscore.models.query.Measure;
import com.scholarscore.models.query.MeasureField;
import com.scholarscore.models.query.expressions.Expression;
import com.scholarscore.models.query.expressions.operands.DimensionOperand;
import com.scholarscore.models.query.expressions.operands.IOperand;
import com.scholarscore.models.query.expressions.operands.OperandType;
import com.scholarscore.models.query.expressions.operators.ComparisonOperator;
import com.scholarscore.models.query.expressions.operators.IOperator;
import com.scholarscore.models.user.ContactType;
import com.scholarscore.models.user.User;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.testng.annotations.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;

/**
 * User: jordan
 * Date: 9/26/15
 * Time: 5:50 PM
 */
@Test(groups = { "unit" })
public class ModelReflectionTests {

//    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private int numberOfFailedDefaultFieldAttempts = 0;
    private Set<String> fieldsThatNeedDefaults = new HashSet<>();
    
    private boolean loggingEnabled = false;
    
    private final String packageToScan = this.getClass().getPackage().getName();
    private final String testClassName = this.getClass().getSimpleName();   // class name without packagename
    private final Set<String> excludedClassNames = new HashSet<String>() {{
        // if you want to exclude a model class from this test, add it here (including packageToScan)...
        add(packageToScan + "." + testClassName);
    }};
    
    public String getPackageToScan() {
        return packageToScan;
    }
    
    public Set<String> getExcludedClassNames() { 
        return excludedClassNames;
    }    
    
    private Set<Class<?>> getClassesInPackage(String packageToScan) {
        List<ClassLoader> classLoadersList = new LinkedList<>();
        classLoadersList.add(ClasspathHelper.contextClassLoader());
        classLoadersList.add(ClasspathHelper.staticClassLoader());

        // This isn't generally a good idea, but hey, it's tests
        Reflections.log = null;
        
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setScanners(new SubTypesScanner(false /* don't exclude Object.class */), new ResourcesScanner())
                .setUrls(ClasspathHelper.forClassLoader(classLoadersList.toArray(new ClassLoader[0])))
                .filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix(packageToScan))));

        return reflections.getSubTypesOf(Object.class);
    }

    @Test
    public void testClassesInPackage() {
        Set<Class<?>> classes = getClassesInPackage(getPackageToScan());

        for (Class clazz : classes) {
            if (Modifier.isAbstract(clazz.getModifiers())) {
                logDebug("Skipping Class " + clazz + " as it is abstract...");
                logDebug("");
                continue;
            } else if (getExcludedClassNames().contains(clazz.getName())) {
                logDebug("Skipping Class " + clazz + " as it is explicitly excluded...");
                logDebug("");
                continue;
            } else {
                logDebug("Now analyzing class " + clazz + "...");
            }

            checkEqualsAndHashCodeForClass(clazz);
            checkCopyConstructorForClass(clazz);
            checkMergePropertiesIfNullForApiModel(clazz);
            logDebug("");
        }
        if (numberOfFailedDefaultFieldAttempts <= 0) {
            logDebug("\nDONE. No problems.");
        } else {
            StringBuilder debugStringBuilder = new StringBuilder();
            debugStringBuilder.append("\nDONE. \n"
                    + "One or more test(s) (" + numberOfFailedDefaultFieldAttempts + ", " + fieldsThatNeedDefaults.size() + " unique) were skipped because \n"
                    + "of an inability to construct objects with sensible default values."
                    + "\n(To fix this, please see the " + this.getClass().getSimpleName() + ".getValueForType method "
                    + "and follow the example there.)\n\n"
                    + "The following fields need sensible defaults defined in getValueForType...\n");
            for (String field : fieldsThatNeedDefaults) {
                debugStringBuilder.append("\n" + field);
            }
            // show this final output regardless of logging flags
            System.out.println(debugStringBuilder.toString());
        }
    }

    private void checkMergePropertiesIfNullForApiModel(Class<?> clazz) {
        // this test is only applicable to classes that implement IApiModel
        if (IApiModel.class.isAssignableFrom(clazz)) {
            logDebug("*Class " + clazz + " implements IApiModel, checking mergePropertiesIfNull...");
            try {
                IApiModel populatedObject = (IApiModel)buildPopulatedObject(clazz);
                IApiModel emptyObject = (IApiModel)clazz.newInstance();
  
                if (emptyObject == null || populatedObject == null) {
                    System.out.println("Failed to construct " + clazz.getSimpleName() + ", aborting this test...");
                    return;
                }
//                assertNotNull("empty object is null!", emptyObject);
//                assertNotNull("populated object is null!", populatedObject);
                
                // if any fields on emptyObject actually aren't null, we need to know that 
                // or we'll get a false failure because the objects won't be equal
                Field[] fields = clazz.getDeclaredFields();
                for (Field field : fields) {
                    if (Modifier.isFinal(field.getModifiers()) || Modifier.isStatic(field.getModifiers())) {
                        continue;
                    }
                    field.setAccessible(true);
                    Object initialFieldValue = field.get(emptyObject);

                    // in order for the test to pass, the two objects (populated and empty) need to pass an equality test
                    // after mergePropertiesIfNull is called on the empty object with the populated one passed in.
                    // in cases when the so-called 'empty' object has a default value in a given field, 
                    // mergePropertiesIfNull will not succeed in merging this field to the emptyObject and 
                    // the equality test will fail. To work around this, find any fields that have non-null default
                    // values and set those same values onto the populated object so the equality test will work.
                    if (initialFieldValue != null) {
                        logDebug("Found non-null default value in field " 
                                + field.getName() + " (value: " + initialFieldValue + ")"
                                + " after constructing object " + emptyObject.getClass().getSimpleName());
                        field.set(populatedObject, initialFieldValue);
                    }
                }

                    emptyObject.mergePropertiesIfNull(populatedObject);
                assertEquals(populatedObject, emptyObject, "A new object of class " + clazz 
                        + " had mergePropertiesIfNull invoked with a populated instance of the object.\n" 
                        + "After merging, the new object was not equal to to the populated object.");
                logDebug("*Successfully tested mergePropertiesIfNull(..)");
            } catch (InstantiationException | IllegalAccessException e) {
                logDebug("Error constructing class, skipping...");
                e.printStackTrace();
            } 
        } else {
//            logVerbose("-Class " + clazz + " does not implement IApiModel, skipping mergePropertiesIfNull test.");
        }
    }

    private void checkCopyConstructorForClass(Class<?> clazz) {
        final Object firstInstance = buildPopulatedObject(clazz);
        try {
            Constructor constructor = clazz.getDeclaredConstructor(clazz);
            logDebug("*Found copy constructor for " + clazz.getName() + ", building copy now.");

            try {
                Object secondInstance = constructor.newInstance(firstInstance);
                assertEquals(secondInstance, firstInstance, "New Instance of " + clazz.getName() + " doesn't equal original it was copy constructed from!");
                logDebug("*Successfully copied object and confirmed field values match");
            } catch (InstantiationException|IllegalAccessException|InvocationTargetException e) {
                logDebug("WARN: could not invoke copy constructor for " + clazz + ", moving on.");
            }
        } catch (NoSuchMethodException e) {
            logDebug("-Did not find single-argument copy constructor for class " + clazz + ", skipping copy constructor check.");
        }
    }

    private void checkEqualsAndHashCodeForClass(Class clazz) {
        final Object unmodifiedInstance = buildPopulatedObject(clazz);
        Field[] fields = clazz.getDeclaredFields();
    
        logDebug("*Checking equals and hashcode for " + clazz.getName() + "...");
        
        for (Field field : fields) {
            if (Modifier.isFinal(field.getModifiers()) || Modifier.isStatic(field.getModifiers())) {
//                logVerbose("Skipping field " + field + " because it's static/final.");
                continue;
            }
            Object instanceWithTweakedField = buildPopulatedObject(clazz, field.getName());
            if (instanceWithTweakedField == null) {
                logDebug("Couldn't build object with tweaked field " + field.getName() + ", skipping this check.");
                // TODO: make a note of this?
                continue;
            }
            // logDebug("Checking equals() and hashcode() on " + clazz.getName() + " with field " + field.getName() + " modified...");
            String both = "original: " + unmodifiedInstance + ", tweaked: " + instanceWithTweakedField;
            String objMsg = "For class " + clazz + ", ";
            String equalsMsg = objMsg + "Equals() returned true even though objects have different values for field " + field.getName() + "\n" + both;
            String hashMsg = objMsg + "hashcode() returned identical values even though objects have different values for field " + field.getName() + "\n" + both;
            assertNotEquals(unmodifiedInstance, instanceWithTweakedField, equalsMsg);
            assertNotEquals(unmodifiedInstance.hashCode(), instanceWithTweakedField.hashCode(), hashMsg);
        }
        logDebug("*Equals and HashCode are good (assuming no problems were just displayed)");
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

                Object value;
                if (field.getName().equals(fieldNameToModify)) {
                    value = getAnotherValueForType(field.getType());
                } else {
                    value = getSensibleValueForType(field.getType());
                }

                if (value == null) {
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
//            logDebug("exception trying to build populated object...");
//            throw e;
            return null;
        }
    }

    private Object getSensibleValueForType(Class<?> type) {
        return getValueForType(type, false);
    }

    private Object getAnotherValueForType(Class<?> type) {
        return getValueForType(type, true);
    }
    
    // the reflection tests above will test equality methods of the objects under test -- but this requires being able to get
    // two distinct values for each object type. The below method is responsible for generating two data values
    // (which of the two values is returned  depends on the 'alt' flag) for the type passed in.
    private Object getValueForType(Class<?> type, boolean alt) {
        if (type.isAssignableFrom(Double.class)) { return alt ? 777D : 76D; }
        if (type.isAssignableFrom(Long.class)) { return alt ? 22L : 2L; }
        if (type.isAssignableFrom(String.class)) { return alt ? "anotherStringValue" : "stringValue"; }
        if (type.isAssignableFrom(Boolean.class)) { return !alt; }
        if (type.isAssignableFrom(Integer.class)) { return alt ? 33: 3; }
        
        long epochSecondsFirstDate = 1322462400000L;
        long epochSecondsAltDate = 1442462400000L;
        if (type.isAssignableFrom(Date.class)) { return alt ? new Date(epochSecondsAltDate) : new Date(epochSecondsFirstDate); }
        if (type.isAssignableFrom(LocalDate.class)) { return LocalDateTime.ofEpochSecond((alt ? epochSecondsAltDate : epochSecondsFirstDate), 0, ZoneOffset.UTC).toLocalDate(); }

        if (type.isAssignableFrom(IOperand.class)) { return alt ? new DimensionOperand() : new Expression(); }

        // this needs more work -- how to capture generic type of list, and create dummy of same type?
        if (type.isAssignableFrom(List.class)) {
            List list = new ArrayList<>();
            if (alt) {
                list.add(new Object());
            } else {
                list.add(new Object());
            }
            return list;
        }
        
        // this trick is to simplify definitions of stuff that extends APImodel
        // however it does not apply to abstract classes, as well as classes that don't have empty constructors
        if (ApiModel.class.isAssignableFrom(type) && !Modifier.isAbstract(type.getModifiers())) {
            try {
                ApiModel apiModel = (ApiModel)type.newInstance();
                if (alt) {
                    apiModel.setName("apiModel2");
                } else {
                    apiModel.setName("apiModel1");
                }
                return apiModel;
            } catch (InstantiationException e) {
                logDebug("ERROR Trying to construct new instance of " + type + " to cast to type ApiModel.");
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        if (type.isAssignableFrom(GradeFormula.class)) {
            GradeFormula gradeFormula = new GradeFormula();
            Map<String, Double> map = new HashMap<>();
            if (alt) {
                map.put(AssignmentType.ATTENDANCE.name(), 2D);
            } else {
                map.put(AssignmentType.HOMEWORK.name(), 4D);
            }
            gradeFormula.setAssignmentTypeWeights(map);
            return gradeFormula;
        }

        if (type.isAssignableFrom(Assignment.class)) {
            Assignment assignment = null;
            if (alt) {
                assignment = new AttendanceAssignment();
            } else {
                assignment = new GradedAssignment();
            }
            return assignment;
        }

        if (type.isAssignableFrom(SchoolDay.class)) { return buildPopulatedObject(SchoolDay.class, "date", alt); }
        if (type.isAssignableFrom(Expression.class)) { return buildPopulatedObject(Expression.class, "leftHandSide", alt); }
        if (type.isAssignableFrom(Address.class)) { return buildPopulatedObject(Address.class, "postalCode", alt); }
        if (type.isAssignableFrom(MeasureField.class)) { return buildPopulatedObject(MeasureField.class, "field", alt); }
        if (type.isAssignableFrom(User.class)) { return buildPopulatedObject(User.class, "password", alt); }
        if (type.isAssignableFrom(DimensionField.class)) { return buildPopulatedObject(DimensionField.class, "field", alt); }
        if (type.isAssignableFrom(AggregateFunction.class)) { return alt ? AggregateFunction.AVG : AggregateFunction.COUNT; }
        if (type.isAssignableFrom(IOperator.class)) { return alt ? ComparisonOperator.EQUAL : ComparisonOperator.NOT_EQUAL; }
        if (type.isAssignableFrom(BehaviorCategory.class)) { return alt ? BehaviorCategory.DEMERIT : BehaviorCategory.MERIT; }
        if (type.isAssignableFrom(OperandType.class)) { return alt ? OperandType.DIMENSION : OperandType.EXPRESSION; }
        if (type.isAssignableFrom(Dimension.class)) { return alt ? Dimension.STUDENT : Dimension.TEACHER; }
        if (type.isAssignableFrom(Gender.class)) { return alt ? Gender.FEMALE : Gender.MALE; }
        if (type.isAssignableFrom(Measure.class)) { return alt ? Measure.DEMERIT : Measure.MERIT; }
        if (type.isAssignableFrom(AttendanceStatus.class)) { return alt ? AttendanceStatus.PRESENT : AttendanceStatus.ABSENT; }
        if (type.isAssignableFrom(ContactType.class)) { return alt ? ContactType.EMAIL : ContactType.PHONE; }

        // System.out.println("Could not find sensible value for field type " + type);
        return null;
    }
    
    private void logDebug(String msg) { 
        if (loggingEnabled) {
            // TODO: figure out why logging isn't showing up on common tests, only System.out
            // logger.debug(msg);
            System.out.println(msg);
        }
    }
    
}