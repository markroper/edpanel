package com.scholarscore.models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.scholarscore.models.assignment.Assignment;
import com.scholarscore.models.assignment.AssignmentType;
import com.scholarscore.models.assignment.AttendanceAssignment;
import com.scholarscore.models.assignment.GradedAssignment;
import com.scholarscore.models.attendance.SchoolDay;
import com.scholarscore.models.goal.BehaviorComponent;
import com.scholarscore.models.goal.GoalComponent;
import com.scholarscore.models.gpa.Gpa;
import com.scholarscore.models.gpa.SimpleGpa;
import com.scholarscore.models.gpa.WeightedGpa;
import com.scholarscore.models.gradeformula.GradeFormula;
import com.scholarscore.models.message.topic.BehaviorTopic;
import com.scholarscore.models.message.topic.GpaTopic;
import com.scholarscore.models.message.topic.MessageTopic;
import com.scholarscore.models.notification.Notification;
import com.scholarscore.models.notification.TriggeredNotification;
import com.scholarscore.models.notification.group.NotificationGroup;
import com.scholarscore.models.notification.group.SingleStudent;
import com.scholarscore.models.notification.group.SingleTeacher;
import com.scholarscore.models.query.AggregateMeasure;
import com.scholarscore.models.query.DimensionField;
import com.scholarscore.models.query.MeasureField;
import com.scholarscore.models.query.Record;
import com.scholarscore.models.query.expressions.Expression;
import com.scholarscore.models.query.expressions.operands.DimensionOperand;
import com.scholarscore.models.query.expressions.operands.IOperand;
import com.scholarscore.models.query.expressions.operands.OperandType;
import com.scholarscore.models.query.expressions.operators.ComparisonOperator;
import com.scholarscore.models.query.expressions.operators.IOperator;
import com.scholarscore.models.survey.SurveyQuestionAggregate;
import com.scholarscore.models.survey.answer.BooleanAnswer;
import com.scholarscore.models.survey.answer.MultipleChoiceAnswer;
import com.scholarscore.models.survey.answer.QuestionAnswer;
import com.scholarscore.models.survey.question.SurveyBooleanQuestion;
import com.scholarscore.models.survey.question.SurveyMultipleChoiceQuestion;
import com.scholarscore.models.survey.question.SurveyQuestion;
import com.scholarscore.models.ui.ScoreAsOfWeek;
import com.scholarscore.models.ui.SectionGradeWithProgression;
import com.scholarscore.models.user.Staff;
import com.scholarscore.models.user.Student;
import com.scholarscore.models.user.User;
import org.apache.commons.lang3.tuple.MutablePair;
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
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
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
            String className = clazz.getName();
            if (className.contains("$")) {
                logDebug("Skipping inner class " + className + "...");
                continue;
            }
            if (Modifier.isAbstract(clazz.getModifiers())) {
                logDebug("Skipping Class " + clazz + " as it is abstract...");
                logDebug("");
                continue;
            } else if (getExcludedClassNames().contains(className)) {
                logDebug("Skipping Class " + clazz + " as it is explicitly excluded...");
                logDebug("");
                continue;
            } else if (className.toLowerCase().endsWith("test")) {
                logDebug("Skipping class " + clazz + " as it contains 'test'...");
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
        StringBuilder debugStringBuilder = new StringBuilder();
        if (numberOfFailedDefaultFieldAttempts <= 0) {
            // just shut up if everything is fine.
            //  debugStringBuilder.append("\nDONE. No problems.");
        } else {
            debugStringBuilder.append("\nDONE. \n"
                    + "One or more test(s) (" + numberOfFailedDefaultFieldAttempts + ", " + fieldsThatNeedDefaults.size() + " unique) were skipped because \n"
                    + "of an inability to construct objects with sensible default values."
                    + "\n(To fix this, please see the " + this.getClass().getSimpleName() + ".getValueForField method "
                    + "and follow the example there.)\n\n"
                    + "The following fields need sensible defaults defined in getValueForField...\n");
            for (String field : fieldsThatNeedDefaults) {
                debugStringBuilder.append("\n" + field);
            }
        }
        // show this final output regardless of logging flags
        System.out.println(debugStringBuilder.toString());
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
                // if any fields on emptyObject actually aren't null, we need to know that 
                // or we'll get a false failure because the objects won't be equal
//                Field[] fields = clazz.getDeclaredFields();

                // return all fields from sourceOfFieldsClass, as well as any and all superclasses within package being tested
                Field[] allFields = getAllFieldNamesWithinEligibleSuperclasses(clazz);

                for (Field field : allFields) {
                    if (Modifier.isFinal(field.getModifiers()) 
                            || Modifier.isStatic(field.getModifiers())
                            || Modifier.isTransient(field.getModifiers())) {
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
                assertEquals(emptyObject, populatedObject, "A new object of class " + clazz 
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
                assertEquals(firstInstance, secondInstance, "New Instance of " + clazz.getName() + " doesn't equal original it was copy constructed from!");
                logDebug("*Successfully copied object and confirmed field values match");
            } catch (InstantiationException|IllegalAccessException|InvocationTargetException e) {
                logDebug("WARN: could not invoke copy constructor for " + clazz + ", moving on.");
            }
        } catch (NoSuchMethodException e) {
            logDebug("-Did not find single-argument copy constructor for class " + clazz + ", skipping copy constructor check.");
        }
    }

    private void checkEqualsAndHashCodeForClass(Class clazz) {
    
        // TODO Jordan: just temporary -- finish refactoring to use concreteClass and sourceOfFieldsClass below 
        Class<?> concreteClass = clazz;
        Class<?> sourceOfFieldsClass = clazz;
        
        boolean sameClass = (concreteClass == sourceOfFieldsClass);
        
        final Object unmodifiedInstance = buildPopulatedObject(clazz);
//        Field[] fields = sourceOfFieldsClass.getDeclaredFields();
        
        String classDescString = sameClass ? "class " + concreteClass.getSimpleName() 
                : "class (impl)" + concreteClass.getSimpleName() + " (fields)" + sourceOfFieldsClass.getSimpleName();
    
        logDebug("*Checking equals and hashcode for " + classDescString + "...");
        
        // return all fields from sourceOfFieldsClass, as well as any and all superclasses within package being tested
        Field[] allFields = getAllFieldNamesWithinEligibleSuperclasses(sourceOfFieldsClass);
        
        // for each field in the object under test, make a copy of the object with just that field tweaked.
        // then check resulting equals/hashcode between them and see what we can discover

        for (Field field : allFields) {
            if (Modifier.isFinal(field.getModifiers()) 
                    || Modifier.isStatic(field.getModifiers())) {
                // skip static and final fields. our concern is equals() and hashcode() which don't consider these
                continue;
            }
//            if (Modifier.isTransient(field.getModifiers())) {
                // TODO Jordan: should test to ensure transients are NOT considered in equals
//                System.out.println("Transient field detected, should test that it doesn't affect equals");
//                continue;
//            }
            
            Object instanceWithTweakedField = buildPopulatedObject(clazz, field.getName());
            if (instanceWithTweakedField == null) {
                logDebug("Couldn't build object with tweaked field " + field.getName() + ", skipping this check.");
                // TODO: make a note of this?
                continue;
            }
            // logDebug("Checking equals() and hashcode() on " + clazz.getName() + " with field " + field.getName() + " modified...");
            String both = "original:\n" + unmodifiedInstance + "\ntweaked:\n" + instanceWithTweakedField;
            String objMsg = "For class " + clazz + ", ";
            String equalsMsg = objMsg + "Equals() returned true even though objects have different values for field " + field.getName() + "\n" + both;
            String hashMsg = objMsg + "hashcode() returned identical values even though objects have different values for field " + field.getName() + "\n" + both;
            try {
                field.setAccessible(true);
                Object unmodifiedValue = field.get(unmodifiedInstance);
                Object modifiedValue = field.get(instanceWithTweakedField);
//                equalsMsg += "\n(Unmodified value: " + unmodifiedValue + ", Modified value: " + modifiedValue + ")";
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            assertNotEquals(unmodifiedInstance, instanceWithTweakedField, equalsMsg);
            assertNotEquals(unmodifiedInstance.hashCode(), instanceWithTweakedField.hashCode(), hashMsg);
        }
        logDebug("*Equals and HashCode are good (assuming no problems were just displayed)");
    }

    private Field[] getAllFieldNamesWithinEligibleSuperclasses(Class<?> clazz) {
        ArrayList<Field> allFields = new ArrayList<>();
        allFields.addAll(Arrays.asList(clazz.getDeclaredFields()));
        if (clazz.getSuperclass().getPackage().toString().toLowerCase().contains(packageToScan.toLowerCase())) {
            // keep going!
            Field[] superclassFields = getAllFieldNamesWithinEligibleSuperclasses(clazz.getSuperclass());
            allFields.addAll(Arrays.asList(superclassFields));
        }
        return allFields.toArray(new Field[allFields.size()]);
    }

    // ad hoc (saves a bunch of lines in a method below)
    private Object buildPopulatedObject(Class clazz, String fieldNameToModify, boolean doModification) {
        return doModification ? 
                buildPopulatedObject(clazz, fieldNameToModify) :
                buildPopulatedObject(clazz);
    }
    
    private <T> Object buildPopulatedObject(Class<T> clazz) {
        return buildPopulatedObject(clazz, clazz, (String)null);
    }
    
    private <S, T extends S> Object buildPopulatedObject(Class<T> clazz, String fieldNameToModify) { 
        return buildPopulatedObject(clazz, clazz, fieldNameToModify);
    }
     
    /* 
     * Create a reasonable test object. 
     * If fieldNameToModify is set, this field will be varied from the normal default value typical to this field type.
     */
    private <S,T extends S> T buildPopulatedObject(Class<T> concreteClass, Class<S> sourceOfFieldsClass, String fieldNameToModify) {
        T instance;
        try {
            instance = concreteClass.newInstance();
        } catch (InstantiationException|IllegalAccessException ie) {
            // this is expected to happen if we can't build the object with a no-arg constructor. just move on and pretend nothing happened.
            logDebug("InstantiationException|IllegalAccessException " + ie);
            return null;
        }             
        return buildPopulatedObject(instance, sourceOfFieldsClass, fieldNameToModify);
    }   

    private <S,T extends S> T buildPopulatedObject(T instance, Class<S> sourceOfFieldsClass, String fieldNameToModify) {
        return buildPopulatedObject(instance, sourceOfFieldsClass, fieldNameToModify, false);
    }

        // using an already-created instance, provide best-guess values for a list of fields pulled from a specified class 
    // (the specified class to pull fields from is either the class of the instance or a superclass)
    private <S,T extends S> T buildPopulatedObject(T instance, Class<S> sourceOfFieldsClass, String fieldNameToModify, boolean fieldAlreadyTweaked) {
        
        Field[] fields = sourceOfFieldsClass.getDeclaredFields();
        // although the instance that is returned in successful cases is the same,
        // NULL is returned when a field can't be tweaked. So refactor this.
        T returnedInstance = null;
        try {
            boolean fieldTweakedInFields = populateObjectFields(instance, fields, fieldNameToModify);
            returnedInstance = instance;
            fieldAlreadyTweaked |= fieldTweakedInFields;
        } catch (NoDefaultValueForTypeException | UnableToSetValueException e) {
            // NoDefaultValueForTypeException - failure to figure out a sensible default for field
            // UnableToSetValueException - sensible default known, but object field doesn't get set even after calling corresponding setter
//            System.out.println("buildPopulatedObject returning NULL for object " + instance.getClass().getName() + " when trying to tweak field " + fieldNameToModify);
            returnedInstance = null;
        }
        // if exception not thrown, we can safely assume defaults have been set for the fields specified
        
        // don't try to ascend the hierarchy unless it has gone well up to this point...
        if (returnedInstance != null) {
            Class<? super S> superclass = sourceOfFieldsClass.getSuperclass();
            if (superclass.getPackage().toString().toLowerCase().contains(packageToScan.toLowerCase())) {
                logDebug("Hit in-package parent class of " + sourceOfFieldsClass + ", superclass " + superclass.getSimpleName());
                returnedInstance = buildPopulatedObject(returnedInstance, superclass, fieldNameToModify, fieldAlreadyTweaked);

                if (returnedInstance == null) {
                    logDebug("ERROR populating object when ascending hierarchy... abandoning attempt to populate "
                            + instance.getClass().getSimpleName() + " fields from superclass " + superclass.getSimpleName() + " and returning instance as-is");
                    return instance;
                }
            } else {
                // okay, we've gotten as high as we're going to get in the hierarchy. confirm
                // that the field that we're supposed to tweak has been tweaked, and noisily complain (throw exception?) if it hasn't been.
//                System.out.println("Parent of " + sourceOfFieldsClass + " (" + superclass.getSimpleName() + ") is not in package, done ascending hierarchy...");
                if (fieldNameToModify != null && !fieldAlreadyTweaked) {
                    System.out.println("WARNING - object " + instance + " being returned without tweaking field " + fieldNameToModify);                    
                }
            }
        }
        return returnedInstance;
    }
    
    private class NoDefaultValueForTypeException extends Exception {
        NoDefaultValueForTypeException() { super(); }
        NoDefaultValueForTypeException(String msg) { super(); }
    }
    
    private class UnableToSetValueException extends Exception {
        UnableToSetValueException() { super(); }
        UnableToSetValueException(String msg) { super(msg); }
    }
    
    // This method is provided an instance, a field that is found on that instance, and the new value that is desired for that field
    
    private void assignValueToField(Object instance, Field field, Object value) throws UnableToSetValueException {
        // actually do the setting. prefer to use a setter method but directly twiddle the field if necessary.
        ArrayList<Method> matchingSetters = new ArrayList<>();
        Class<?> instanceClass = instance.getClass();
        Method[] instanceMethods = instanceClass.getMethods();
        for (Method method : instanceMethods) {
            // we want to set variable named <field.getName()> but would prefer to go through a setter, if existent
            if (method.getName().toLowerCase().equals("set" + field.getName().toLowerCase())) {
                if (method.getParameterCount() == 1) {
                    matchingSetters.add(method);
                    // all done, don't need to continue
                    break;
                }
            }
        }

        Method bestSetterMatch = null;
        if (matchingSetters.size() == 1) {
            // use the matching setter
            bestSetterMatch = matchingSetters.get(0);
        } else if (matchingSetters.size() > 1) {
            // more than one setter... hmmm. probably need to do more to figure out how to pick one here...
            System.out.println("OOPS! I see more than one potential setter for field " + field.getName()
                    + " on class " + instance.getClass().getSimpleName() + ". For now, just taking the first one I see...");
            bestSetterMatch = matchingSetters.get(0);
        }

        try {
            if (bestSetterMatch != null) {
                // can use a setter, the preferred way
                try {
                    bestSetterMatch.invoke(instance, value);
                } catch (InvocationTargetException e) {
                    System.out.println("FAILED to invoke matching setter on " + instance.getClass().getSimpleName());
                }
            } else {
                // no matching setters, use direct-twiddle approach
                field.setAccessible(true);
                field.set(instance, value);
            }

            field.setAccessible(true);
            Object setValue = field.get(instance);
            if (!setValue.equals(value)) {
                throw new UnableToSetValueException("cannot set value!");
            }
        } catch (IllegalAccessException iae) {
            throw new RuntimeException("IllegalAccessException failed trying to twiddle field " + field.getName() + " -- Reflection tests cannot function");
        }

    }
    
    // attempt to set each of a provided list of fields on a provided object instance. The fields of the object 
    // will be set to reasonable defaults based on their type.
    // The instance argument is the object which will have specified fields populated.
    // The collection of fields is used to specify which fields on the instance should be assigned defaults.
    // The optional/nullable fieldNameToModify argument will set the field with the provided name to a different 
    // value than would normally be set. This is used in (un)equals method verification.
    // ------ ------ ------ ------ ------ ------ ------ ------ ------
    // for some types of fields, such as static and final, population will not be attempted.
    // in some cases, (such as when the setter exists but does not actually set the field) the population will be attempted
    // but will fail. In these cases, an error will only be thrown if the field value being set is 'fieldNameToModify' -
    // otherwise it is assumed that this field cannot be 'freely' set.
    // 
    // throws an exception in the case of any problems
    // returns TRUE or FALSE, depending on if a field was set to a non-default value (only ever true if fieldNameToModify is supplied)
    private <T> boolean populateObjectFields(T instance, 
                                             Field[] fields, 
                                             String fieldNameToModify) throws NoDefaultValueForTypeException, UnableToSetValueException {
//        System.out.println("populateObjectFields called on instance: " + instance + " of class " + instance.getClass());
        if (instance.getClass().getName().contains("$")) {
            System.out.println("WARNING: Inner class! I think. " + instance);
        }

            boolean fieldTweaked = false;
            for (Field field : fields) {
                if (Modifier.isFinal(field.getModifiers()) || Modifier.isStatic(field.getModifiers())) {
                    // ignore static/final
                    continue;
                }
                
                // Since we're testing field value inequality by setting the values on two different
                // instances of an object to two different values, it will be a problem if the setter
                // doesn't actually set. How to detect this? 
                // Well, we can try setting the setter using a certain value and then accessing the field.
                // If the value in the field is the value we set the field to, well, the setter works.
                // But wait! What if the field _always_ returns that value, and we just happened to call
                // the setter with (and check the resulting field value equality against) the object
                // (e.g. enum type) that the field is ALWAYS going to return anyway. So the setter doesn't 
                // 'truly' work, but if the test attempts to set it to the value that it's always set to,
                // the above test fails. SO we have two values that must be different, and we will try to 
                // set the field to BOTH of them, and then test that the resulting read of the field
                // reads each value correctly. This ensures the field does not always return one value.
                Object intermediateValue;
//                Object ultimateValue;
                
                // TODO Jordan: assert that intermediateValue and ultimateValue are not the same,
                // or else we can't even test the damn setter
                
                Object value;
                if (field.getName().equals(fieldNameToModify)) {
                    intermediateValue = getSensibleValueForField(field);
                    value = getAnotherValueForField(field);
                    if (value != null) {
                        fieldTweaked = true;
                    }
                } else {
                    intermediateValue = getAnotherValueForField(field);
                    value = getSensibleValueForField(field);
                }

                if (value == null) {
                    System.out.println("Adding class... " + field.toString());
                    fieldsThatNeedDefaults.add(field.toString());
                    numberOfFailedDefaultFieldAttempts++;
                    // if can't get default for any fields, assume the test is screwed for this object (skip it, but keep testing others)
//                    return null;
                    throw new NoDefaultValueForTypeException();
                }

                try {
                    assignValueToField(instance, field, intermediateValue);
                    assignValueToField(instance, field, value);
                } catch (UnableToSetValueException e) {
                    // raise a warning if the field we're trying to tweak to a non-standard value doesn't actually get 'set'
                    // otherwise, just ignore it
                    if (field.getName().equals(fieldNameToModify)) {
//                        System.out.println("FAILURE to tweak field to different value on object " + instance.getClass().getName() + ", unequal testing will probably fail.");
//                        e.printStackTrace(); 
//                        return null;
                        throw e;
                    }
                }
            }
            // This method can be called without specifying a fieldNameToModify, but if it is specified and the field isn't found, warn the caller!
            if (!fieldTweaked && fieldNameToModify != null) {
                // today, this only happens if trying to tweak a field that belongs to a superclass. should be supported.
//                logDebug("WARNING - object " + instance + " being returned without tweaking field " + fieldNameToModify);
//                System.out.println("OBSOLETE INNER WARNING - object " + instance + " being returned without tweaking field " + fieldNameToModify);
                // TODO Jordan: this could probably be an exception, but should wait until after superclass fields are checked
                // to enforce this
//                System.out.println("WARNING - this will soon be an exception!");
            }
            return fieldTweaked;
    }

    private Object getSensibleValueForField(Field field) {
        Object obj = getValueForField(field, false);
        return obj;
    }

    private Object getAnotherValueForField(Field field) {
        return getValueForField(field, true);
    }
    
    // This method is tasked with figuring out a "good default" value for the passed-in type. It does this by specifically handling collections and objects with generic types (and both), 
    // then call
    private Object getValueForField(Field field, boolean alt) {
        Object returnedFromGenericType = getValueForGenericType(field.getGenericType(), alt);
        
        if (returnedFromGenericType == null) {
            // try this backup approach
            Object returnedFromType = getValueForType(field.getType(), alt);
//            System.out.println("Fell back to non-generic getValueForType, and got back: " + returnedFromType);
            return returnedFromType;
        }
        
        return returnedFromGenericType;
    }

    // This method is tasked with figuring out a "good default" value for the passed-in type. It does this by specifically handling collections and objects with generic types (and both), 
    // then call
    private Object getValueForGenericType(Type genericType, boolean alt) {

        Class<?> genericClass = genericType.getClass();
        Class<?> type = null;

        ////////////////
        try {
            String[] splitClassNameFrags = genericType.getTypeName().split("<");
//            if (splitClassNameFrags.length > 1) {
//                System.out.println("Generic Type detected -- " + genericType);
//            }
            // only really need to do this next part if there's more than one frag...?
            String splitClassName = splitClassNameFrags[0];
            Class<?> derivedObjectClass = Class.forName(splitClassName);
            type = derivedObjectClass;
        } catch (ClassNotFoundException e) {
            logDebug("Could not find class!");
            return null;
        }

        // TODO: must handle primitive types separately (Type for a primitive will not be autoboxed and getTypeName() 
        // will return "int", etc while for object-based classes this method return FQPNs) 

        // first let's figure out if it's a collection type, regardless of any generic stuff
        // if it is, we'll set this to something other than null.
        // (This isn't the collection itself, but a builder with
        // a common interface)
        StructureToPopulate stp = null;

        // If the type you want to add isn't here, 
        // create a new instance of the <Structure>ToPopulate classes
        // at the end of this file, following the example of the others
        if (type.isAssignableFrom(HashMap.class)) {
            stp = new HashMapToPopulate();
        } else if (type.isAssignableFrom(HashSet.class)) {
            stp = new HashSetToPopulate();
        } else if (type.isAssignableFrom(MutablePair.class)) {
            stp = new MutablePairToPopulate();
        } else if (type.isAssignableFrom(ArrayList.class)) {
            stp = new ArrayListToPopulate();
        } else if (type.isAssignableFrom(List.class)) {
            stp = new ArrayListToPopulate();
        }

        if (stp != null) {
            // okay, we have a match on a type of collection defined above.
            // see if there are any generic parameters to give clues about what kind of 
            // objects are supposed to go in this collection
            if (genericClass != null && ParameterizedType.class.isAssignableFrom(genericClass)) {
                ParameterizedType parameterizedInnerType = (ParameterizedType) genericType;
                Type[] actualInnerTypeArgs = parameterizedInnerType.getActualTypeArguments();
                Object[] typedValuesToPopulate = new Object[actualInnerTypeArgs.length];
                int index = 0;
                for (Type actualInnerTypeArg : actualInnerTypeArgs) {
                    // for each of the parameter types (e.g. ArrayList<Integer, String> -- first would be integer, then string)
                    // dive deeper and create an instance of the expected type...
                    Object value = getValueForGenericType(actualInnerTypeArg, alt);
                    if (value == null) {
//                        System.out.println("Got null value trying to get type "  + actualInnerTypeArg);
//                        Class<?> genericClass2 = actualInnerTypeArg.getClass();
//                        System.out.println("ActualInnerTypeArg.getClass(): " + genericClass2);
                        value = new Object();
                    }
                    typedValuesToPopulate[index++] = value;
                }
                Object populatedCollection = null;
                try {
                    // okay, now build whatever kind of collection the object is expecting
                    populatedCollection = stp.validateAndPopulate(typedValuesToPopulate);
                } catch (NoDefaultValueException re) {
                    System.out.println("NoDefaultValueException...");
                    for (int i = 0; i < actualInnerTypeArgs.length; i++) {
                        if (typedValuesToPopulate[i] == null) {
//                            System.out.println("Adding class(2)... " + actualInnerTypeArgs[i].toString());
                            fieldsThatNeedDefaults.add(actualInnerTypeArgs[i].toString());
                            numberOfFailedDefaultFieldAttempts++;
                        }
                    }
                }
                return populatedCollection;
            } else {
                // TODO: at this point, we have a collection without any generic specification, 
                // which we are not handling (we don't use this anywhere in the model classes, apparently?)
//                System.out.println("!! -- Here, we are apparently not handling generic collections.");
//                System.out.println("!! -- Generic class: " + genericClass);
//                System.out.println("!! -- STP: " + stp);
//                System.out.println("!! -- Type: " + type);
//                System.out.println("ParameterizedType.class.isAssignableFrom(genericClass): " + ParameterizedType.class.isAssignableFrom(genericClass));
//                System.out.println();

                Object[] typedValuesToPopulate = new Object[stp.numberOfArguments()];
                for (int i = 0; i < stp.numberOfArguments(); i++) {
                    typedValuesToPopulate[i] = new Object();
                }
                Object populatedCollection = stp.validateAndPopulate(typedValuesToPopulate);
                return populatedCollection;
            }
        }


        return getValueForType(type, alt);
    }
    
    // the reflection tests above will test equality methods of the objects under test -- but this requires being able to get
    // two distinct values for each object type. The below method is responsible for generating two data values
    // (which of the two values is returned  depends on the 'alt' flag) for the type passed in.
    private <T> Object getValueForType(Class<T> type, boolean alt) {
        if (type.isAssignableFrom(Double.class)) { return alt ? 777D : 76D; }
        if (type.isAssignableFrom(Long.class)) { return alt ? 22L : 2L; }
        if (type.isAssignableFrom(String.class)) { return alt ? "anotherStringValue" : "stringValue"; }
        if (type.isAssignableFrom(Boolean.class)) { return !alt; }
        if (type.isAssignableFrom(boolean.class)) { return !alt; } // I assumed boolean.class isAssignableFrom Boolean.class, but no. weird.
        if (type.isAssignableFrom(Integer.class)) { return alt ? 33: 3; }
        
        long epochSecondsFirstDate = 1322462400000L;
        long epochSecondsAltDate = 1442462400000L;
        if (type.isAssignableFrom(Date.class)) { return alt ? new Date(epochSecondsAltDate) : new Date(epochSecondsFirstDate); }
        
        if (type.isAssignableFrom(LocalDate.class) || type.isAssignableFrom(LocalDateTime.class)) {
            LocalDateTime ldt = LocalDateTime.ofEpochSecond((alt ? epochSecondsAltDate : epochSecondsFirstDate), 0, ZoneOffset.UTC);
            if (type.isAssignableFrom(LocalDate.class)) { return ldt.toLocalDate(); }
            if (type.isAssignableFrom(LocalDateTime.class)) { return ldt; }
        }
        
        if (type.isAssignableFrom(IOperand.class)) { return alt ? new DimensionOperand() : new Expression(); }

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
        if (type.isAssignableFrom(DimensionField.class)) { return buildPopulatedObject(DimensionField.class, "field", alt); }
        if (type.isAssignableFrom(AggregateMeasure.class)) { return buildPopulatedObject(AggregateMeasure.class, "measure", alt); } 
        if (type.isAssignableFrom(IOperator.class)) { return alt ? ComparisonOperator.EQUAL : ComparisonOperator.NOT_EQUAL; }
        if (type.isAssignableFrom(OperandType.class)) { return alt ? OperandType.DIMENSION : OperandType.EXPRESSION; }
        if (type.isAssignableFrom(ScoreAsOfWeek.class)) { return buildPopulatedObject(ScoreAsOfWeek.class, "score", alt); }
        if (type.isAssignableFrom(JsonAttributes.class)) { return buildPopulatedObject(JsonAttributes.class, "jsonString", alt); }
        if (type.isAssignableFrom(JsonNode.class)) { return new TextNode(alt ? "string1" : "string2"); }
        if (type.isAssignableFrom(Score.class)) { return buildPopulatedObject(Score.class, "comment", alt); }
        if (type.isAssignableFrom(GoalComponent.class)) { return buildPopulatedObject(BehaviorComponent.class, "startDate", alt); }
        if (type.isAssignableFrom(Gpa.class)) { return alt ? new SimpleGpa() : new WeightedGpa();  }

        if (type.isAssignableFrom(SurveyQuestion.class)) { return alt ?
                buildPopulatedObject(SurveyBooleanQuestion.class, "showAsCheckbox", alt) :
                buildPopulatedObject(SurveyMultipleChoiceQuestion.class, "choices", alt);
        }
        if (type.isAssignableFrom(QuestionAnswer.class)) {
            return alt ?
                    buildPopulatedObject(BooleanAnswer.class, "type", alt) : 
                    buildPopulatedObject(MultipleChoiceAnswer.class, "type", alt);  
        }
        if (type.isAssignableFrom(MessageTopic.class)) {
            return alt ?
                    buildPopulatedObject(GpaTopic.class, "id", alt) :
                    buildPopulatedObject(BehaviorTopic.class, "id", alt);
        }
        if (type.isAssignableFrom(SurveyQuestionAggregate.class)) { return buildPopulatedObject(SurveyQuestionAggregate.class, "respondents", alt); }
        if (type.isAssignableFrom(Notification.class)) { return buildPopulatedObject(Notification.class, "name", alt); }
        if (type.isAssignableFrom(TriggeredNotification.class)) { return buildPopulatedObject(TriggeredNotification.class, "id", alt); }
        if (type.isAssignableFrom(NotificationGroup.class)) { 
            return alt ? 
                    buildPopulatedObject(SingleStudent.class, "student", alt) :
                    buildPopulatedObject(SingleTeacher.class, "teacherId", alt);
        }
        
        // can the value in type be cast to a 'user' variable?
        if (type.isAssignableFrom(User.class)) { return alt ? new Staff() : new Student(); }

        if (type.isAssignableFrom(Staff.class)) {
            Staff t = new Staff();
            t.setIsTeacher(true);
            t.setId(alt ? 2L : 3L);
            t.setName(alt ? "teacherName2" : "teacherName1");
            return t;
        }

        if (type.isAssignableFrom(SectionGradeWithProgression.class)) { return buildPopulatedObject(SectionGradeWithProgression.class, "currentOverallGrade", alt); } 
        if (type.isAssignableFrom(Record.class)) { 
            List list = new ArrayList<>();
            list.add(new Object());
            return new Record(list);
        }

        // if it's in our model package, do our best guess to automatically find a field that can be tweaked to test (in)equality
        if (type.getPackage() != null &&
                type.getPackage().toString().toLowerCase().contains(packageToScan.toLowerCase())) {
            if (Modifier.isAbstract(type.getModifiers())) {
                // can't best-guess implementations for interfaces without more work, skip for now
            } else if (type.isEnum()) {
                // if it's an enum type we're looking to populate, supply one of the first two enum values
                boolean firstOne = true;
                for (T enumConstant : type.getEnumConstants()) {
                    if (alt && firstOne) {  firstOne = false; }
                    else { return enumConstant; }
                }
            } else {
                // here we have a non-abstract, non-enum class, so try to look at its fields and pick one
                // that we can fiddle with before testing the resulting object
                Field field = getTweakableFieldForType(type);
                if (field != null && field.getName() != null) {
                    return buildPopulatedObject(type, field.getName(), alt);
                } else {
                    // failed to get tweakable field for whatever reason -- no fields on object, etc
                }
            }
        }
        
        return null;
    }
    
    private Field getTweakableFieldForType(Class<?> type) { 
//        System.out.println("trying to get tweakable field for type " + type.getSimpleName() + "...");
        if (type.isEnum()) {
            throw new RuntimeException("Enum type: should not be trying to tweak field!");
        }
        
        for (Field field : type.getDeclaredFields()) {
            Class<?> fieldClass = field.getType();
            // try to return a primative or wrapper first, as it will be less complicated
            if (fieldClass.isPrimitive() || isWrapperType(fieldClass)) {
                return field;
            }
        }

        for (Field field : type.getDeclaredFields()) {
            // otherwise, just return the first until we have a better approach.
            return field;
        }
        return null;
    }

    private static final Set<Class> WRAPPER_TYPES = new HashSet<>(Arrays.asList(
            Boolean.class, Character.class, Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class, Void.class));
    public static boolean isWrapperType(Class clazz) {
        return WRAPPER_TYPES.contains(clazz);
    }
    
    private void logDebug(String msg) { 
        if (loggingEnabled) {
            // TODO: figure out why logging isn't showing up on common tests, only System.out
            // logger.debug(msg);
            System.out.println(msg);
        }
    }

    private interface StructureToPopulate {
        Object validateAndPopulate(Object[] arguments);
        int numberOfArguments();
    }

    private static abstract class BaseStructureToPopulate implements StructureToPopulate {

        @Override
        public Object validateAndPopulate(Object[] arguments) {
            validate(arguments);
            return populate(arguments);
        }

        private void validate(Object[] arguments) {
            if (arguments == null || arguments.length != numberOfArguments()) {
                if (arguments == null) {
                    throw new RuntimeException("Arguments are null on me: " + this.toString());
                } else if (arguments.length != numberOfArguments()) {
                    new RuntimeException("Arguments don't match the expected number (" + numberOfArguments() + "): " + this.toString());
                }
            }

            for (Object object : arguments) {
                if (object == null) {
                    throw new NoDefaultValueException("Arguments failed to validate on me: " + this.toString());
                }
            }
        }

        public abstract Object populate(Object[] arguments);
        public abstract int numberOfArguments();
    }

    private static class ArrayListToPopulate extends BaseStructureToPopulate {

        @Override
        public Object populate(Object[] arguments) {
            ArrayList arrayList = new ArrayList();
            arrayList.add(arguments[0]);
            return arrayList;
        }

        @Override
        public int numberOfArguments() {
            return 1;
        }
    }
    
    private static class HashMapToPopulate extends BaseStructureToPopulate {

        @Override
        public Object populate(Object[] arguments) {
            HashMap hashMap = new HashMap();
            hashMap.put(arguments[0], arguments[1]);
            return hashMap;
        }

        @Override
        public int numberOfArguments() {
            return 2;
        }
    }

    private static class HashSetToPopulate extends BaseStructureToPopulate {

        @Override
        public Object populate(Object[] arguments) {
            HashSet hashSet = new HashSet();
            hashSet.add(arguments[0]);
            return hashSet;
        }

        @Override
        public int numberOfArguments() {
            return 1;
        }
    }
    
    private static class MutablePairToPopulate extends BaseStructureToPopulate {


        @Override
        public Object populate(Object[] arguments) {
            return MutablePair.of(arguments[0], arguments[1]);
        }

        @Override
        public int numberOfArguments() {
            return 2;
        }
    }
    
    private static class NoDefaultValueException extends RuntimeException { 
        
        public NoDefaultValueException() {
            super();
        }
        
        public NoDefaultValueException(String msg) { 
            super(msg);
        }
    }
}