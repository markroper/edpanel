package com.scholarscore.utils;

import com.scholarscore.models.Address;
import com.scholarscore.models.School;
import com.scholarscore.models.SchoolYear;
import com.scholarscore.models.Term;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.time.DateUtils;

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

    public static String generateSocialSecurityNumber(){
        return RandomStringUtils.randomNumeric(3) + "-" + RandomStringUtils.randomNumeric(2) + "-" + RandomStringUtils.randomNumeric(4);
    }

    public static School generateSchool(){
        School school = new School.SchoolBuilder()
                .withAddress(generateAddress())
                .withMainPhone(generatePhoneNumber())
                .withSourceSystemId(RandomStringUtils.randomAlphanumeric(10))
                .withPrincipalEmail(generateEmail())
                .withPrincipalName(RandomStringUtils.randomAlphabetic(10) + "," + RandomStringUtils.randomAlphabetic(10))
                .withName(RandomStringUtils.randomAlphabetic(15))
                .withId(RandomUtils.nextLong(0L, Long.MAX_VALUE))
                .build();
        generateSchoolYear(school);
        return school;
    }

    public static SchoolYear generateSchoolYear(final School parentSchool){
        Date startDate = new Date();
        SchoolYear schoolYear = new SchoolYear.SchoolYearBuilder().withSchool(parentSchool).withStartDate(startDate).withEndDate(DateUtils.addMonths(startDate, 9)).build();
        schoolYear.addTerm(generateTerm(startDate, DateUtils.addMonths(startDate, 3), schoolYear));
        schoolYear.addTerm(generateTerm(DateUtils.addMonths(startDate, 3), DateUtils.addMonths(startDate, 6), schoolYear));
        schoolYear.addTerm(generateTerm(DateUtils.addMonths(startDate, 6), DateUtils.addMonths(startDate, 9), schoolYear));
        return schoolYear;
    }

    public static Term generateTerm(Date startDate, Date endDate, SchoolYear schoolYear){
        return new Term.TermBuilder()
                .withStartDate(startDate)
                .withEndDate(endDate)
                .withSchoolYear(schoolYear)
                .withId(RandomUtils.nextLong(0L, Long.MAX_VALUE))
                .build();
    }
}
