package com.scholarscore.models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.scholarscore.models.assignment.Assignment;
import com.scholarscore.models.assignment.AssignmentType;
import com.scholarscore.models.assignment.AttendanceAssignment;
import com.scholarscore.models.assignment.GradedAssignment;
import com.scholarscore.models.attendance.AttendanceStatus;
import com.scholarscore.models.attendance.AttendanceTypes;
import com.scholarscore.models.attendance.SchoolDay;
import com.scholarscore.models.goal.BehaviorComponent;
import com.scholarscore.models.goal.GoalComponent;
import com.scholarscore.models.gpa.Gpa;
import com.scholarscore.models.gpa.SimpleGpa;
import com.scholarscore.models.gpa.WeightedGpa;
import com.scholarscore.models.gradeformula.GradeFormula;
import com.scholarscore.models.query.AggregateFunction;
import com.scholarscore.models.query.AggregateMeasure;
import com.scholarscore.models.query.Dimension;
import com.scholarscore.models.query.DimensionField;
import com.scholarscore.models.query.Measure;
import com.scholarscore.models.query.MeasureField;
import com.scholarscore.models.query.Record;
import com.scholarscore.models.query.expressions.Expression;
import com.scholarscore.models.query.expressions.operands.DimensionOperand;
import com.scholarscore.models.query.expressions.operands.IOperand;
import com.scholarscore.models.query.expressions.operands.OperandType;
import com.scholarscore.models.query.expressions.operators.ComparisonOperator;
import com.scholarscore.models.query.expressions.operators.IOperator;
import com.scholarscore.models.ui.ScoreAsOfWeek;
import com.scholarscore.models.ui.SectionGradeWithProgression;
import com.scholarscore.models.user.Administrator;
import com.scholarscore.models.user.ContactType;
import com.scholarscore.models.user.Student;
import com.scholarscore.models.user.Teacher;
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
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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
            String className = clazz.getName();
            // for purposes of exclusion, identify inner classes as the same as their outer class file
            // (so if the class e.g. ModelReflectionTests.java is excluded, so are the inner classes in this file) 
            className = className.split("\\$")[0];
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
        if (numberOfFailedDefaultFieldAttempts <= 0) {
            logDebug("\nDONE. No problems.");
        } else {
            StringBuilder debugStringBuilder = new StringBuilder();
            debugStringBuilder.append("\nDONE. \n"
                    + "One or more test(s) (" + numberOfFailedDefaultFieldAttempts + ", " + fieldsThatNeedDefaults.size() + " unique) were skipped because \n"
                    + "of an inability to construct objects with sensible default values."
                    + "\n(To fix this, please see the " + this.getClass().getSimpleName() + ".getValueForField method "
                    + "and follow the example there.)\n\n"
                    + "The following fields need sensible defaults defined in getValueForField...\n");
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
                // skip static and final fields. our concern is equals() and hashcode() which don't consider these
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
            try {
                field.setAccessible(true);
                Object unmodifiedValue = field.get(unmodifiedInstance);
                Object modifiedValue = field.get(instanceWithTweakedField);
                equalsMsg += "\n(Unmodified value: " + unmodifiedValue + ", Modified value: " + modifiedValue + ")";
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
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
    
    /* 
     * Create a reasonable test object. 
     * If fieldNameToModify is set, this field will be varied from the normal default value typical to this field type.
     */
    private Object buildPopulatedObject(Class clazz, String fieldNameToModify) {
        try {
            Object instance;
            try {
                instance = clazz.newInstance();
            } catch (InstantiationException ie) {
                // this is expected to happen if we can't build the object with a no-arg constructor. just move on and pretend nothing happened.
                return null;
            }
                
            Field[] fields = clazz.getDeclaredFields();
            boolean fieldTweaked = false;
            for (Field field : fields) {
                if (Modifier.isFinal(field.getModifiers()) || Modifier.isStatic(field.getModifiers())) {
                    // ignore static/final
                    continue;
                }

                Object value;
                if (field.getName().equals(fieldNameToModify)) {
                    value = getAnotherValueForField(field);
                } else {
                    value = getSensibleValueForField(field);
                }

                if (value == null) {
                    fieldsThatNeedDefaults.add(field.toString());
                    numberOfFailedDefaultFieldAttempts++;
                    // if can't get default for any fields, assume the test is screwed for this instance
                    return null;
                }
                field.setAccessible(true);
                field.set(instance, value);
                fieldTweaked = true;
            }
            // This method can be called without specifying a fieldNameToModify, but if it is specified and the field isn't found, warn the caller!
            if (!fieldTweaked && fieldNameToModify != null) {
                logDebug("WARNING - object " + instance + " being returned without tweaking field " + fieldNameToModify);
            }
            return instance;
        } catch (IllegalAccessException e) {
            logDebug("IllegalAccessException " + e);
            return null;
        }
    }

    private Object getSensibleValueForField(Field field) {
        return getValueForField(field.getGenericType(), false);
    }

    private Object getAnotherValueForField(Field field) {
        return getValueForField(field.getGenericType(), true);
    }
    
    // This method is tasked with figuring out a "good default" value for the passed-in type. It does this by specifically handling collections and objects with generic types (and both), 
    // then call
    private Object getValueForField(Type genericType, boolean alt) {

        Class<?> genericClass = genericType.getClass();
        Class<?> type = null;

        ////////////////
        try {
            String splitClassName = genericType.getTypeName().split("<")[0];
            Class<?> derivedObjectClass = Class.forName(splitClassName);
            type = derivedObjectClass;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            logDebug("Could not find class!");
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
                    typedValuesToPopulate[index++] = getValueForField(actualInnerTypeArg, alt);
                }
                Object populatedCollection = null;
                try {
                    // okay, now build whatever kind of collection the object is expecting
                    populatedCollection = stp.validateAndPopulate(typedValuesToPopulate);
                } catch (NoDefaultValueException re) {
                    for (int i = 0; i < actualInnerTypeArgs.length; i++) {
                        if (typedValuesToPopulate[i] == null) {
                            fieldsThatNeedDefaults.add(actualInnerTypeArgs[i].toString());
                            numberOfFailedDefaultFieldAttempts++;
                        }
                    }
                }
                return populatedCollection;
            } else {
                // TODO: at this point, we have a collection without any generic specification, 
                // which we are not handling (we don't use this anywhere in the model classes, apparently?)
            }
        }
        
        return getValueForType(type, alt);
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
        if (type.isAssignableFrom(AggregateFunction.class)) { return alt ? AggregateFunction.AVG : AggregateFunction.COUNT; }
        if (type.isAssignableFrom(AggregateMeasure.class)) { return buildPopulatedObject(AggregateMeasure.class, "measure", alt); } 
        if (type.isAssignableFrom(IOperator.class)) { return alt ? ComparisonOperator.EQUAL : ComparisonOperator.NOT_EQUAL; }
        if (type.isAssignableFrom(BehaviorCategory.class)) { return alt ? BehaviorCategory.DEMERIT : BehaviorCategory.MERIT; }
        if (type.isAssignableFrom(OperandType.class)) { return alt ? OperandType.DIMENSION : OperandType.EXPRESSION; }
        if (type.isAssignableFrom(Dimension.class)) { return alt ? Dimension.STUDENT : Dimension.TEACHER; }
        if (type.isAssignableFrom(Gender.class)) { return alt ? Gender.FEMALE : Gender.MALE; }
        if (type.isAssignableFrom(Measure.class)) { return alt ? Measure.DEMERIT : Measure.MERIT; }
        if (type.isAssignableFrom(AttendanceStatus.class)) { return alt ? AttendanceStatus.PRESENT : AttendanceStatus.ABSENT; }
        if (type.isAssignableFrom(ContactType.class)) { return alt ? ContactType.EMAIL : ContactType.PHONE; }
        if (type.isAssignableFrom(ScoreAsOfWeek.class)) { return buildPopulatedObject(ScoreAsOfWeek.class, "score", alt); }
        if (type.isAssignableFrom(AttendanceTypes.class)) { return alt ? AttendanceTypes.DAILY : AttendanceTypes.SECTION; }
        if (type.isAssignableFrom(JsonAttributes.class)) { return buildPopulatedObject(JsonAttributes.class, "jsonString", alt); }
        if (type.isAssignableFrom(JsonNode.class)) { return new TextNode(alt ? "string1" : "string2"); }
        if (type.isAssignableFrom(Score.class)) { return buildPopulatedObject(Score.class, "comment", alt); }
        if (type.isAssignableFrom(GoalComponent.class)) { return buildPopulatedObject(BehaviorComponent.class, "startDate", alt); }
        if (type.isAssignableFrom(Gpa.class)) { return alt ? new SimpleGpa() : new WeightedGpa();  }
        
        // can the value in type be cast to a 'user' variable?
        if (type.isAssignableFrom(User.class)) { return alt ? new Administrator() : new Student(); }

        if (type.isAssignableFrom(Teacher.class)) {
            Teacher t = new Teacher();
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

        return null;
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