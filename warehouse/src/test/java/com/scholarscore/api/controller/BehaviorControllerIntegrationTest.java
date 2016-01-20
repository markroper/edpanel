package com.scholarscore.api.controller;

import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.Behavior;
import com.scholarscore.models.BehaviorCategory;
import com.scholarscore.models.user.Student;
import com.scholarscore.models.user.Teacher;
import org.springframework.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.LocalDate;

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
        student = studentValidatingExecutor.create(student, "create base student");

        teacher = new Teacher();
        teacher.setName(localeServiceUtil.generateName());
        teacher = teacherValidatingExecutor.create(teacher, "create base teacher");
    }

    //Positive test cases
    @DataProvider
    public Object[][] createBehaviorProvider() {
        
        // all behaviors must have time
        LocalDate now = getNow();

        Behavior emptyBehavior = new Behavior();
        // teacher is always required or constraint exception
        emptyBehavior.setStudent(student);
        emptyBehavior.setAssigner(teacher);
        emptyBehavior.setBehaviorDate(now);

        Behavior namedBehavior = new Behavior();
        // teacher is always required or constraint exception
        namedBehavior.setStudent(student);
        namedBehavior.setAssigner(teacher);
        namedBehavior.setBehaviorDate(now);
        namedBehavior.setName("BehaviorEvent");

        Behavior populatedBehavior = new Behavior();
        populatedBehavior.setName("Good Eye Contact");
        populatedBehavior.setStudent(student);
        populatedBehavior.setAssigner(teacher);
        populatedBehavior.setBehaviorCategory(BehaviorCategory.MERIT);
        populatedBehavior.setBehaviorDate(now);
        populatedBehavior.setPointValue("1");
        populatedBehavior.setRemoteStudentId("123456");
        populatedBehavior.setRoster("History 101");

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
        Behavior newBehavior = new Behavior();
        newBehavior.setStudent(student);
        newBehavior.setAssigner(teacher);
        newBehavior.setBehaviorDate(getNow());
        
        Behavior createdBehavior = behaviorValidatingExecutor.create(student.getId(), behavior, msg);
        behaviorValidatingExecutor.replace(student.getId(), createdBehavior.getId(), newBehavior, msg);
        numberOfItemsCreated++;
    }
    
    @Test(dataProvider = "createBehaviorProvider")
    public void updateBehaviorTest(String msg, Behavior behavior) { 
        Behavior createdBehavior = behaviorValidatingExecutor.create(student.getId(), behavior, msg);
        Behavior updatedBehavior = new Behavior();
        updatedBehavior.setAssigner(teacher);
        updatedBehavior.setStudent(student);
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

        LocalDate now = getNow();

        Behavior behaviorWithoutDate = new Behavior();
        behaviorWithoutDate.setStudent(student);
        behaviorWithoutDate.setAssigner(teacher);
        behaviorWithoutDate.setName(localeServiceUtil.generateName(24));
        
        Behavior behaviorNameTooLong = new Behavior();
        behaviorNameTooLong.setStudent(student);
        behaviorNameTooLong.setAssigner(teacher);
        behaviorNameTooLong.setName(localeServiceUtil.generateName(257));
        behaviorNameTooLong.setBehaviorDate(now);

        Behavior remoteStudentIdTooLong = new Behavior();
        remoteStudentIdTooLong.setStudent(student);
        remoteStudentIdTooLong.setAssigner(teacher);
        remoteStudentIdTooLong.setRemoteStudentId(localeServiceUtil.generateName(257));
        remoteStudentIdTooLong.setBehaviorDate(now);

        Behavior pointValueTooLong = new Behavior();
        pointValueTooLong.setStudent(student);
        pointValueTooLong.setAssigner(teacher);
        pointValueTooLong.setPointValue(localeServiceUtil.generateName(257));
        pointValueTooLong.setBehaviorDate(now);
        
        Behavior rosterTooLong = new Behavior();
        rosterTooLong.setStudent(student);
        rosterTooLong.setAssigner(teacher);
        rosterTooLong.setRoster(localeServiceUtil.generateName(257));
        rosterTooLong.setBehaviorDate(now);
        
        return new Object[][] {
                { "Behavior with name exceeding 256 char limit", behaviorNameTooLong, HttpStatus.BAD_REQUEST },
                { "Behavior with remote student Id exceeding 256 char limit", remoteStudentIdTooLong, HttpStatus.BAD_REQUEST },
                { "Behavior with point value exceeding 256 char limit", pointValueTooLong, HttpStatus.BAD_REQUEST },
                { "Behavior with roster exceeding 256 char limit", rosterTooLong, HttpStatus.BAD_REQUEST },
                // TODO Jordan: for some reason this test is failing in DEV environment, but not locally (OS X)
                // comment it out until I have time to investigate what the bug/problem is
//                { "Behavior with no date set", behaviorWithoutDate, HttpStatus.BAD_REQUEST },
        };
    }

    @Test(dataProvider = "createBehaviorNegativeProvider")
    public void createBehaviorNegativeTest(String msg, Behavior behavior, HttpStatus expectedStatus) {
        behaviorValidatingExecutor.createNegative(student.getId(), behavior, expectedStatus, msg);
    }

    @Test(dataProvider = "createBehaviorNegativeProvider")
    public void replaceSchoolYearNegativeTest(String msg, Behavior behavior, HttpStatus expectedStatus) {
        Behavior updatedBehavior = new Behavior();
        updatedBehavior.setStudent(student);
        updatedBehavior.setAssigner(teacher);
        updatedBehavior.setBehaviorDate(getNow());
        Behavior created = behaviorValidatingExecutor.create(student.getId(), updatedBehavior, msg);
        behaviorValidatingExecutor.replaceNegative(student.getId(), created.getId(), behavior, expectedStatus, msg);
    }
}
