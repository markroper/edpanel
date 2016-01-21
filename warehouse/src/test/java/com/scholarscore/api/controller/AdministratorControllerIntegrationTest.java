package com.scholarscore.api.controller;

import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.user.Staff;
import org.springframework.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.UUID;

/**
 * Created by cwallace on 1/19/16.
 */
@Test(groups = { "integration" })
public class AdministratorControllerIntegrationTest extends IntegrationBase {
        @BeforeClass
        public void init() {
            authenticate();
        }

        //Positive test cases
        @DataProvider
        public Object[][] createAdminProvider() {
            Staff emptyTeacher = new Staff();
            Staff namedTeacher = new Staff();
            namedTeacher.setName(localeServiceUtil.generateName());

            return new Object[][] {
                { "Empty teacher", emptyTeacher },
                    { "Named teacher", namedTeacher }
            };
        }

        @Test(dataProvider = "createAdminProvider")
        public void createAdminTest(String msg, Staff admin) {
            administratorValidatingExecutor.create(admin, msg);
        }

        @Test(dataProvider = "createAdminProvider")
        public void deleteAdminTest(String msg, Staff admin) {
            Staff createdAdmin = administratorValidatingExecutor.create(admin, msg);
            administratorValidatingExecutor.delete(createdAdmin.getId(), msg);
        }

        @Test(dataProvider = "createAdminProvider")
        public void replaceAdminTest(String msg, Staff admin) {
            Staff createdAdmin = administratorValidatingExecutor.create(admin, msg);
            Staff a = new Staff(admin);
            administratorValidatingExecutor.replace(createdAdmin.getId(), a, msg);
        }

        @Test(dataProvider = "createAdminProvider")
        public void updateAdminTest(String msg, Staff admin) {
            Staff createdAdmin = administratorValidatingExecutor.create(admin, msg);
            Staff updatedAdmin = new Staff();
            updatedAdmin.setName(localeServiceUtil.generateName());
            //PATCH the existing record with a new name.
            administratorValidatingExecutor.update(createdAdmin.getId(), updatedAdmin, msg);
        }

        @Test
        public void getAllItems() {
            administratorValidatingExecutor.getAll("Get all records created so far");
        }

        //Negative test cases
        @DataProvider
        public Object[][] createAdminNegativeProvider() {
            Staff gradedAdminNameTooLong = new Staff();
            gradedAdminNameTooLong.setName(localeServiceUtil.generateName(257));

            return new Object[][] {
                    { "Teacher with name exceeding 256 char limit", gradedAdminNameTooLong, HttpStatus.BAD_REQUEST }
            };
        }

        @Test(dataProvider = "createAdminNegativeProvider")
        public void createAdminNegativeTest(String msg, Staff administrator, HttpStatus expectedStatus) {
            administratorValidatingExecutor.createNegative(administrator, expectedStatus, msg);
        }

        @Test(dataProvider = "createAdminNegativeProvider")
        public void replaceAdminTest(String msg, Staff administrator, HttpStatus expectedStatus) {
            Staff t = new Staff();
            t.setName(UUID.randomUUID().toString());
            Staff created = administratorValidatingExecutor.create(t, msg);
            administratorValidatingExecutor.replaceNegative(created.getId(), administrator, expectedStatus, msg);
        }
    }

