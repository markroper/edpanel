package com.scholarscore.etl.deanslist.api.response;

import com.scholarscore.etl.deanslist.api.model.Behavior;
import com.scholarscore.models.BehaviorCategory;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Collection;
import java.util.HashSet;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * User: jordan
 * Date: 9/17/15
 * Time: 10:56 AM
 */
@Test(groups = { "unit" })
public class BehaviorResponseUnitTest {
    
    @Test
    public void testBehaviorResponseToInternalMode() {
        Behavior behavior = buildDefaultBehavior();

        HashSet<Behavior> behaviors = new HashSet<>();
        behaviors.add(behavior);

        BehaviorResponse response = new BehaviorResponse();
        response.data = behaviors;

        Collection<com.scholarscore.models.Behavior> parsedBehaviors = response.toInternalModel();
        assertTrue(parsedBehaviors != null, "BehaviorResponse returned null behaviors after attempted parsing");
        assertTrue(parsedBehaviors.size() == 1, "BehaviorResponse returned multiple behaviors after attempting to parse only one");
        com.scholarscore.models.Behavior parsedBehavior = parsedBehaviors.toArray(new com.scholarscore.models.Behavior[1])[0];
        assertTrue(parsedBehavior != null, "BehaviorResponse returned null behavior within non-null behaviors collection");

        assertEquals(parsedBehavior.getBehaviorCategory(), (BehaviorCategory.DEMERIT), "parsed behavior category doesn't show demerit");

        // (name is category name + " " + behavior description
        assertEquals(parsedBehavior.getName(), "Demerit Not bowing low enough", "Demerit name doesn't match");

        assertTrue(parsedBehavior.getStudent() != null, "parsed behavior student is null");
        assertEquals(parsedBehavior.getStudent().getName(),"James Tiberius Kirk", "parsed behavior student name doesn't match");

        assertTrue(parsedBehavior.getBehaviorDate() != null, "parsed behavior date is null");
        assertEquals((Long)parsedBehavior.getBehaviorDate().getTime(),
                (Long)1442462400000L, "parsed date is incorrect");

        assertEquals(parsedBehavior.getRemoteSystem(), BehaviorResponse.DEANSLIST_SOURCE, "parsed remote system isn't deanslist");
        assertEquals(parsedBehavior.getRemoteBehaviorId(), "123456", "parsed behavior remote behavior id doesn't match");
        assertEquals(parsedBehavior.getRemoteStudentId(), "A12345", "parsed behavior remote student id doesn't match");
        assertEquals(parsedBehavior.getRoster(), "History", "parsed behavior roster doesn't match");
   }
    
    @Test(dataProvider = "behaviorCategoryProvider")
    public void testBehaviorCategoryParsingInBehaviorResponseToInternalMode(String behaviorCategory, BehaviorCategory shouldParseAs) {
        Behavior behavior = buildDefaultBehavior();
        behavior.BehaviorCategory = behaviorCategory;

        HashSet<Behavior> behaviors = new HashSet<>();
        behaviors.add(behavior);

        BehaviorResponse response = new BehaviorResponse();
        response.data = behaviors;
        
        Collection<com.scholarscore.models.Behavior> parsedBehaviors = response.toInternalModel();
        assertTrue(parsedBehaviors != null, "BehaviorResponse returned null behaviors after attempted parsing");
        assertTrue(parsedBehaviors.size() == 1, "BehaviorResponse returned multiple behaviors after attempting to parse only one");
        com.scholarscore.models.Behavior parsedBehavior = parsedBehaviors.toArray(new com.scholarscore.models.Behavior[1])[0];
        assertTrue(parsedBehavior != null, "BehaviorResponse returned null behavior within non-null behaviors collection");
        assertEquals(parsedBehavior.getBehaviorCategory(), (shouldParseAs),
                "Behavior Response did not correctly parse " + behaviorCategory + ".\n"
                        + "...was looking for: " + shouldParseAs + "\n"
                        + "...but found      : " + parsedBehavior.getBehaviorCategory()
        );
    }
    
    @DataProvider(name = "behaviorCategoryProvider")
    public Object[][] behaviorCategoryProvider() {
        return new Object[][] {

                // basic merit cases
                { "Merit", BehaviorCategory.MERIT },
                { "Merits", BehaviorCategory.MERIT },
                { "merits", BehaviorCategory.MERIT },
                { "Something Merits", BehaviorCategory.MERIT },
                { "Something Merits Something", BehaviorCategory.MERIT },

                // basic demerit cases
                { "Demerit", BehaviorCategory.DEMERIT },
                { "Demerits", BehaviorCategory.DEMERIT },
                { "demerits", BehaviorCategory.DEMERIT },
                { "Something Demerits", BehaviorCategory.DEMERIT },
                { "Something Demerits Something", BehaviorCategory.DEMERIT },

                // basic detention cases
                { "Detention", BehaviorCategory.DETENTION },
                { "Detentions", BehaviorCategory.DETENTION },
                { "detentions", BehaviorCategory.DETENTION },
                { "Something Detentions", BehaviorCategory.DETENTION },
                { "Something Detentions Something", BehaviorCategory.DETENTION },
                
                // basic suspension
                { "Suspension", BehaviorCategory.OUT_OF_SCHOOL_SUSPENSION },
                { "Suspensions", BehaviorCategory.OUT_OF_SCHOOL_SUSPENSION },
                { "suspensions", BehaviorCategory.OUT_OF_SCHOOL_SUSPENSION },
                { "Something Suspensions", BehaviorCategory.OUT_OF_SCHOOL_SUSPENSION },
                { "Something suspension Something", BehaviorCategory.OUT_OF_SCHOOL_SUSPENSION },

                // basic in school suspension
                { "In School Suspension", BehaviorCategory.IN_SCHOOL_SUSPENSION },
                { "InSchool Suspension", BehaviorCategory.IN_SCHOOL_SUSPENSION },
                { "In-School suspension", BehaviorCategory.IN_SCHOOL_SUSPENSION },
                { "In - School suspension", BehaviorCategory.IN_SCHOOL_SUSPENSION },
                { "in-school suspension", BehaviorCategory.IN_SCHOOL_SUSPENSION },
                { "inschoolsuspension", BehaviorCategory.IN_SCHOOL_SUSPENSION },
                { "something in school suspension", BehaviorCategory.IN_SCHOOL_SUSPENSION },
                { "something in something school something suspension something", BehaviorCategory.IN_SCHOOL_SUSPENSION },

                // basic other (including empty)
                { "", BehaviorCategory.OTHER },
                { " ", BehaviorCategory.OTHER },
                { "garbage", BehaviorCategory.OTHER },
                { "other", BehaviorCategory.OTHER },
                { "mer it de mer it", BehaviorCategory.OTHER },
                { "weird hippie behavioral event", BehaviorCategory.OTHER },
                { "pretty much anything not already used", BehaviorCategory.OTHER },


                // demerits beats merits 
                { "merit demerit", BehaviorCategory.DEMERIT },
                { "somethingdemerit merit", BehaviorCategory.DEMERIT },
                
                // detention beats demerits 
                { "detention demerit", BehaviorCategory.DETENTION },
                { "demerits detentions", BehaviorCategory.DETENTION },

                // (out-of-school) suspension beats detention
                { "detention suspension", BehaviorCategory.OUT_OF_SCHOOL_SUSPENSION },
                { "suspensions detentions", BehaviorCategory.OUT_OF_SCHOOL_SUSPENSION },

                // in school suspension beats (out-of-school) suspension
                { "suspension - in school suspension", BehaviorCategory.IN_SCHOOL_SUSPENSION },

                // thus, in school suspension beats everything
                { "in school suspension suspension detention demerit merit other", BehaviorCategory.IN_SCHOOL_SUSPENSION },

                // null returns null
                { null, null },
        };
    }
    
    private Behavior buildDefaultBehavior() {
        Behavior behavior = new Behavior();
        behavior.StudentFirstName = "James";
        behavior.StudentMiddleName = "Tiberius";
        behavior.StudentLastName = "Kirk";
        behavior.Behavior = "Not bowing low enough";
        behavior.BehaviorCategory = "Demerit";
        behavior.BehaviorDate = "2015-09-17";   // .getTime from this parsedDate is 1442462400000
        behavior.DLSAID = "123456";
        behavior.DLStudentID = "A12345";
        behavior.Roster = "History";
        return behavior;
    }

}
