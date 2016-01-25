package com.scholarscore.utils;

import com.scholarscore.models.Address;
import com.scholarscore.models.BehaviorCategory;
import com.scholarscore.models.Course;
import com.scholarscore.models.Gender;
import com.scholarscore.models.School;
import com.scholarscore.models.SchoolYear;
import com.scholarscore.models.Section;
import com.scholarscore.models.StudentSectionGrade;
import com.scholarscore.models.Term;
import com.scholarscore.models.assignment.Assignment;
import com.scholarscore.models.assignment.AssignmentType;
import com.scholarscore.models.assignment.AttendanceAssignment;
import com.scholarscore.models.assignment.GradedAssignment;
import com.scholarscore.models.goal.GoalType;
import com.scholarscore.models.gradeformula.GradeFormula;
import com.scholarscore.models.user.Staff;
import com.scholarscore.models.user.Student;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import java.time.LocalDate;
import java.util.Date;

/**
 * This holds a bunch of test generation methods to help easily generate test cases
 * Created by cschneider on 10/14/15.
 */
public class CommonTestUtils {

    //TODO: make this a real address
    public static Address generateAddress(){
        return generateAddress(
                RandomStringUtils.randomAlphabetic(10),
                RandomStringUtils.randomAlphabetic(10),
                RandomStringUtils.randomNumeric(4) + " " + RandomStringUtils.randomAlphabetic(10),
                RandomStringUtils.randomNumeric(5) + "-" + RandomStringUtils.randomNumeric(4),
                RandomUtils.nextLong(0L, Long.MAX_VALUE));
    }

    public static Address generateAddress(String state){
        return generateAddress(state,
                RandomStringUtils.randomAlphabetic(10),
                RandomStringUtils.randomNumeric(4) + " " + RandomStringUtils.randomAlphabetic(10),
                RandomStringUtils.randomNumeric(5) + "-" + RandomStringUtils.randomNumeric(4),
                RandomUtils.nextLong(0L, Long.MAX_VALUE));
    }

    public static Address generateAddress(String state, String city){
        return generateAddress(state, city,
                RandomStringUtils.randomNumeric(4) + " " + RandomStringUtils.randomAlphabetic(10),
                RandomStringUtils.randomNumeric(5) + "-" + RandomStringUtils.randomNumeric(4),
                RandomUtils.nextLong(0L, Long.MAX_VALUE));
    }

    public static Address generateAddress(String state, String city, String street){
        return generateAddress(state, city, street,
                RandomStringUtils.randomNumeric(5) + "-" + RandomStringUtils.randomNumeric(4),
                RandomUtils.nextLong(0L, Long.MAX_VALUE));
    }

    public static Address generateAddress(String state, String city, String street, String postalCode){
        return generateAddress(state, city, street, postalCode, RandomUtils.nextLong(0L, Long.MAX_VALUE));
    }

    public static Address generateAddress(String state, String city, String street, String postalCode, Long id){
        return new Address.AddressBuilder().
                withCity(city).
                withState(state).
                withStreet(street).
                withPostalCode(postalCode).
                withId(id).
                build();
    }

    public static String generatePhoneNumber(){
        return generatePhoneNumber(RandomStringUtils.randomNumeric(3), RandomStringUtils.randomNumeric(3), RandomStringUtils.randomNumeric(4));
    }

    public static String generatePhoneNumber(String areaCode){
        return generatePhoneNumber(areaCode, RandomStringUtils.randomNumeric(3), RandomStringUtils.randomNumeric(4));
    }

    public static String generatePhoneNumber(String areaCode, String exchange){
        return generatePhoneNumber(areaCode, exchange, RandomStringUtils.randomNumeric(4));
    }

    public static String generatePhoneNumber(String areaCode, String exchange, String subscriber){
        return "("+areaCode+")"+exchange+"-"+subscriber;
    }

    public static String generateEmail(){
        return generateEmail(RandomStringUtils.randomAlphanumeric(8), RandomStringUtils.randomAlphabetic(8), RandomStringUtils.randomAlphabetic(3));
    }

    public static String generateEmail(String username){
        return generateEmail(username, RandomStringUtils.randomAlphabetic(8), RandomStringUtils.randomAlphabetic(3));
    }

    public static String generateEmail(String username, String domain){
        return generateEmail(username, domain, RandomStringUtils.randomAlphabetic(3));
    }

    public static String generateEmail(String username, String domain, String extension){
        return username + "@" + domain + "." + extension;
    }

    public static String generateName(){
        return RandomStringUtils.randomAlphabetic(6) + " " + RandomStringUtils.randomAlphabetic(6);
    }

    public static LocalDate generateBirthDate(){
        return generateBirthDate(9, 18);
    }

    public static LocalDate generateBirthDate(final int minAge, final int maxAge){
        LocalDate now = LocalDate.now();
        // .nextInt max value is exclusive, so add 1 to ensure passed-in maxAge as a possible selection
        now = now.plusYears(-1l * RandomUtils.nextInt(minAge, maxAge + 1));
        now = now.plusMonths(-1l * RandomUtils.nextInt(0, 12));
        return now;
    }

    public static Date getRandomDate(){
        return new Date(RandomUtils.nextLong(0L, Long.MAX_VALUE));
    }

    public static LocalDate getRandomLocalDate(){
        return LocalDate.ofEpochDay(RandomUtils.nextLong(0L, Long.MAX_VALUE));
    }

    public static String generateSocialSecurityNumber(){
        return RandomStringUtils.randomNumeric(3) + "-" + RandomStringUtils.randomNumeric(2) + "-" + RandomStringUtils.randomNumeric(4);
    }

    public static Gender getRandomGender(){
        int genderIndex = RandomUtils.nextInt(0, Gender.values().length);
        return Gender.values()[genderIndex];
    }

    public static GoalType getRandomGoalType(){
        int goalIndex = RandomUtils.nextInt(0, GoalType.values().length);
        return GoalType.values()[goalIndex];
    }

    public static AssignmentType getRandomAssignmentType(){
        int assignmentIndex = RandomUtils.nextInt(0, AssignmentType.values().length);
        return AssignmentType.values()[assignmentIndex];
    }

    public static BehaviorCategory getRandomBehaviorCategory(){
        int behaviorIndex = RandomUtils.nextInt(0, BehaviorCategory.values().length);
        return BehaviorCategory.values()[behaviorIndex];
    }

    public static boolean getRandomBoolean(){
        int coinFlip = RandomUtils.nextInt(0, 1);
        return coinFlip == 1;
    }

    /**
     * Generates a random School year with random values. This method will not fill in child values dependent on it - mainly
     * school years
     * @return a new populated school instance
     */
    public static School generateSchool(){
        return new School.SchoolBuilder()
                .withAddress(generateAddress())
                .withMainPhone(generatePhoneNumber())
                .withSourceSystemId(RandomStringUtils.randomAlphanumeric(10))
                .withPrincipalEmail(generateEmail())
                .withPrincipalName(RandomStringUtils.randomAlphabetic(10) + "," + RandomStringUtils.randomAlphabetic(10))
                .withName(RandomStringUtils.randomAlphabetic(15))
                .withId(RandomUtils.nextLong(0L, Long.MAX_VALUE))
                .build();
    }

    public static SchoolYear generateSchoolYear(final School parentSchool){
        LocalDate startDate = LocalDate.now();
        SchoolYear schoolYear = generateSchoolYearWithoutTerms(parentSchool);
        schoolYear.addTerm(generateTerm(startDate.plusMonths(3l), startDate.plusMonths(3l), schoolYear));
        schoolYear.addTerm(generateTerm(startDate.plusMonths(6l), startDate.plusMonths(6l), schoolYear));
        schoolYear.addTerm(generateTerm(startDate.plusMonths(6l),startDate.plusMonths(9l), schoolYear));
        return schoolYear;
    }

    public static SchoolYear generateSchoolYearWithoutTerms(final School parentSchool){
        LocalDate startDate = LocalDate.now();
        return new SchoolYear.SchoolYearBuilder().withSchool(parentSchool).withStartDate(startDate).withEndDate(startDate.plusMonths(9l)).build();
    }

    public static Term generateTerm(LocalDate startDate, LocalDate endDate, SchoolYear schoolYear){
        Term term = generateTermWithoutSchoolYear(startDate, endDate);
        term.setSchoolYear(schoolYear);

        return term;
    }

    public static Term generateTermWithoutSchoolYear(LocalDate startDate, LocalDate endDate){
        return new Term.TermBuilder()
                .withStartDate(startDate)
                .withEndDate(endDate)
                .withId(RandomUtils.nextLong(0L, Long.MAX_VALUE))
                .build();
    }

    public static Student generateStudent(){
        Long id = RandomUtils.nextLong(0L, Long.MAX_VALUE);
        String sourceSystemId = RandomStringUtils.randomAlphanumeric(10);
        String homePhone = CommonTestUtils.generatePhoneNumber();
        String username = RandomStringUtils.randomAlphanumeric(8);
        String password =  RandomStringUtils.randomAlphanumeric(14);
        //Source system identifier. E.g. powerschool ID
        //Addresses
        Address mailingAddress = CommonTestUtils.generateAddress();
        Address homeAddress = CommonTestUtils.generateAddress();
        //Demographics
        Gender gender = getRandomGender();
        LocalDate birthDate = generateBirthDate();
        LocalDate districtEntryDate= getRandomLocalDate();
        Long projectedGraduationYear = getRandomDate().getTime();
        String socialSecurityNumber = CommonTestUtils.generateSocialSecurityNumber();
        //EthnicityRace
        String federalRace = RandomStringUtils.randomAlphabetic(5);
        String federalEthnicity = RandomStringUtils.randomAlphabetic(5);
        Long currentSchoolId = RandomUtils.nextLong(0L, Long.MAX_VALUE);

        return new Student.StudentBuilder().
                withId(id).
                withSourceSystemId(sourceSystemId).
                withMailingAddress(mailingAddress).
                withHomeAddress(homeAddress).
                withGender(gender).
                withBirthDate(birthDate).
                withDistrictEntryDate(districtEntryDate).
                withProjectedGraduationYear(projectedGraduationYear).
                withSocialSecurityNumber(socialSecurityNumber).
                withFederalEthnicity(federalEthnicity).
                withFederalRace(federalRace).
                withCurrentSchoolId(currentSchoolId).
                withHomePhone(homePhone).
                withUsername(username).
                withPassword(password).
                build();
    }

    public static Staff generateTeacher(){
        Long id = RandomUtils.nextLong(0L, Long.MAX_VALUE);
        Address address = CommonTestUtils.generateAddress();
        String sourceSystemId = RandomStringUtils.randomAlphanumeric(10);
        String homePhone = CommonTestUtils.generatePhoneNumber();
        String username = RandomStringUtils.randomAlphanumeric(8);
        String password =  RandomStringUtils.randomAlphanumeric(14);

        return new Staff.StaffBuilder().
                withId(id).
                withHomeAddress(address).
                withHomePhone(homePhone).
                withSourceSystemid(sourceSystemId).
                withUsername(username).
                withPassword(password).
                withEnabled(false).
                withTeacher(true).
                withAdmin(false).
                build();
    }

    public static Section generateSection(){
        LocalDate startDate = getRandomLocalDate();
        LocalDate endDate = startDate.plusWeeks(Long.valueOf(RandomUtils.nextInt(1, 5)));
        String room = RandomStringUtils.randomAlphanumeric(3);
        GradeFormula gradeFormula = generateGradeFormula();
        String gradeFormulaString = RandomStringUtils.randomAlphanumeric(10);
        Term term = generateTerm(LocalDate.now(), LocalDate.now().plusMonths(3), generateSchoolYear(generateSchool()));
        Course course = generateCourse();

        Section section = new Section.SectionBuilder().
                withId(RandomUtils.nextLong(0L, Long.MAX_VALUE)).
                withName(generateName()).
                withStartDate(startDate).
                withEndDate(endDate).
                withRoom(room).
                withGradeFormula(gradeFormula).
                withTerm(term).
                withCourse(course).build();

        for(int i = 0; i < RandomUtils.nextInt(1, 5); i++){
            section.addPerson(generateTeacher());
        }

        for(int i = 0; i< RandomUtils.nextInt(10, 20); i++) {
            section.addAssignment(generateAssignment(getRandomAssignmentType(), section));
        }

        for(int i= 0; i< RandomUtils.nextInt(5, 30); i++){
            Student student = generateStudent();
            section.addEnrolledStudent(student);
        }

        return section;
    }

    public static Course generateCourse(){
        return new Course.CourseBuilder().
                withName(generateName()).
                withSourceSystemId(RandomStringUtils.randomNumeric(10)).
                withNumber(RandomStringUtils.randomNumeric(7)).
                withSchool(generateSchool()).
                build();
    }

    public static Assignment generateAssignment(AssignmentType type, Section section){
        Assignment.AssignmentBuilder<? extends Assignment.AssignmentBuilder, ? extends Assignment> builder;
        switch (type){
            case ATTENDANCE:
                builder = new AttendanceAssignment.AttendanceAssignmentBuilder();
                break;
            default:
                builder = new GradedAssignment.GradedAssignmentBuilder().withAssignedDate(getRandomLocalDate());

        }
        return (Assignment)builder.
                withName(generateName()).
                withAvailablePoints(RandomUtils.nextLong(0L, Long.MAX_VALUE)).
                withSection(section).
                withDueDate(getRandomLocalDate()).
                withName(generateName()).
                build();
    }

    public static Assignment generateRandomAssignmentWithoutSection(){
        AssignmentType type = getRandomAssignmentType();
        return generateAssignmentWithoutSection(type);
    }

    public static Assignment generateAssignmentWithoutSection(AssignmentType type){
        Assignment.AssignmentBuilder<? extends Assignment.AssignmentBuilder, ? extends Assignment> builder;
        switch (type){
            case ATTENDANCE:
                builder = new AttendanceAssignment.AttendanceAssignmentBuilder();
                break;
            default:
                builder = new GradedAssignment.GradedAssignmentBuilder().withAssignedDate(getRandomLocalDate());

        }
        return (Assignment)builder.
                withName(generateName()).
                withAvailablePoints(RandomUtils.nextLong(0L, Long.MAX_VALUE)).
                withDueDate(getRandomLocalDate()).
                withName(generateName()).
                build();
    }

    public static StudentSectionGrade generateSectionGrade(Section section, Student student){
        return new StudentSectionGrade.StudentSectionGradeBuilder().
                withGrade(RandomUtils.nextDouble(0d, 100d)).
                withComplete(getRandomBoolean()).
                withSection(section).
                withStudent(student).
                withId(RandomUtils.nextLong(0L, Long.MAX_VALUE)).
                withName(generateName()).
                build();
    }

    public static StudentSectionGrade generateSectionGradeWithoutSection(Student student){
        return new StudentSectionGrade.StudentSectionGradeBuilder().
                withGrade(RandomUtils.nextDouble(0d, 100d)).
                withComplete(getRandomBoolean()).
                withStudent(student).
                withId(RandomUtils.nextLong(0L, Long.MAX_VALUE)).
                withName(generateName()).
                build();
    }

    public static GradeFormula generateGradeFormula(){
        GradeFormula formula = new GradeFormula();
        for(AssignmentType type : AssignmentType.values()){
            formula.getAssignmentTypeWeights().put(type.name(), new Double(RandomUtils.nextInt(0, Integer.MAX_VALUE)));
        }
        return formula;
    }
}
