package com.scholarscore.api.controller;

import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.Behavior;
import com.scholarscore.models.Student;
import com.scholarscore.models.Teacher;
import org.springframework.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Calendar;
import java.util.Date;

/**
 * User: jordan
 * Date: 8/28/15
 * Time: 11:20 PM
 */
@Test(groups = { "integration" })
public class BehaviorControllerIntegrationTest extends IntegrationBase {
    private int numberOfItemsCreated = 0;
    private Student student;
    private Teacher teacher;
    
    @BeforeClass
    public void init() {
        authenticate();

        student = new Student();
        student.setName(localeServiceUtil.generateName());
//        student.setCurrentSchoolId(school.getId());
        student = studentValidatingExecutor.create(student, "create base student");

        teacher = new Teacher();
        teacher.setName(localeServiceUtil.generateName());
        teacher = teacherValidatingExecutor.create(teacher, "create base teacher");
    }

    //Positive test cases
    @DataProvider
    public Object[][] createBehaviorProvider() {
        Behavior emptyBehavior = new Behavior();
        emptyBehavior.setId(1L);

        Behavior namedBehavior = new Behavior();
        namedBehavior.setName("BehaviorEvent");
        namedBehavior.setId(2L);
        
        Behavior populatedBehavior = new Behavior();
        populatedBehavior.setName("Good Eye Contact");
        populatedBehavior.setStudent(student);
        populatedBehavior.setTeacher(teacher);
        populatedBehavior.setBehaviorCategory("MERITS");
        populatedBehavior.setBehaviorDate(Calendar.getInstance().getTime());
        populatedBehavior.setPointValue("1");
        populatedBehavior.setRemoteStudentId("123456");
        populatedBehavior.setRoster("History 101");
        populatedBehavior.setId(3L);
        
        return new Object[][]{
                {"Empty behavior", emptyBehavior},
                {"Named behavior", namedBehavior},
                {"Fully populated behavior", populatedBehavior}
        };
    }

    @Test(dataProvider = "createBehaviorProvider")
    public void createBehaviorTest(String msg, Behavior behavior) { 
        behaviorValidatingExecutor.create(student.getId(), behavior, msg);
        numberOfItemsCreated++;
    }

    @Test(dataProvider = "createBehaviorProvider")
    public void deleteBehaviorTest(String msg, Behavior behavior) {
        Behavior createdBehavior = behaviorValidatingExecutor.create(student.getId(), behavior, msg);
        behaviorValidatingExecutor.delete(student.getId(), createdBehavior.getId(), msg);
    }

    @Test(dataProvider = "createBehaviorProvider")
    public void replaceBehaviorTest(String msg, Behavior behavior) {
        Behavior createdBehavior = behaviorValidatingExecutor.create(student.getId(), behavior, msg);
        behaviorValidatingExecutor.replace(student.getId(), createdBehavior.getId(), new Behavior(), msg);
        numberOfItemsCreated++;
    }
    
    @Test(dataProvider = "createBehaviorProvider")
    public void updateBehaviorTest(String msg, Behavior behavior) { 
        Behavior createdBehavior = behaviorValidatingExecutor.create(student.getId(), behavior, msg);
        Behavior updatedBehavior = new Behavior();
        updatedBehavior.setName(localeServiceUtil.generateName());
        //PATCH the existing record with a new name.
        behaviorValidatingExecutor.update(student.getId(), createdBehavior.getId(), updatedBehavior, msg);
        numberOfItemsCreated++;
    }
    
    @Test
    public void getAllItems() { 
        behaviorValidatingExecutor.getAll(student.getId(), "Get all records created so far", numberOfItemsCreated);
    }

    //Negative test cases
    @DataProvider
    public Object[][] createBehaviorNegativeProvider() {
        Behavior behaviorNameTooLong = new Behavior();
        behaviorNameTooLong.setName(localeServiceUtil.generateName(257));

        Behavior remoteStudentIdTooLong = new Behavior();
        remoteStudentIdTooLong.setRemoteStudentId(localeServiceUtil.generateName(257));
        
        Behavior categoryTooLong = new Behavior();
        categoryTooLong.setBehaviorCategory(localeServiceUtil.generateName(257));
        
        Behavior pointValueTooLong = new Behavior();
        pointValueTooLong.setPointValue(localeServiceUtil.generateName(257));
        
        Behavior rosterTooLong = new Behavior();
        rosterTooLong.setRoster(localeServiceUtil.generateName(257));
        
        return new Object[][] {
                { "Behavior with name exceeding 256 char limit", behaviorNameTooLong, HttpStatus.BAD_REQUEST },
                { "Behavior with remote student Id exceeding 256 char limit", remoteStudentIdTooLong, HttpStatus.BAD_REQUEST },
                { "Behavior with category exceeding 256 char limit", categoryTooLong, HttpStatus.BAD_REQUEST },
                { "Behavior with point value exceeding 256 char limit", pointValueTooLong, HttpStatus.BAD_REQUEST },
                { "Behavior with roster exceeding 256 char limit", rosterTooLong, HttpStatus.BAD_REQUEST },
        };
    }

    @Test(dataProvider = "createSchoolYearNegativeProvider")
    public void createBehaviorNegativeTest(String msg, Behavior behavior, HttpStatus expectedStatus) {
        behaviorValidatingExecutor.createNegative(student.getId(), behavior, expectedStatus, msg);
    }

    @Test(dataProvider = "createSchoolYearNegativeProvider")
    public void replaceSchoolYearNegativeTest(String msg, Behavior behavior, HttpStatus expectedStatus) {
        Behavior created = behaviorValidatingExecutor.create(student.getId(), new Behavior(), msg);
        behaviorValidatingExecutor.replaceNegative(student.getId(), created.getId(), behavior, expectedStatus, msg);
    }
}
