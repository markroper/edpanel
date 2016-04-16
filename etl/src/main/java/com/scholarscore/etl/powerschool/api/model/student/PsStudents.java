package com.scholarscore.etl.powerschool.api.model.student;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.scholarscore.etl.powerschool.api.deserializers.StudentsDeserializer;
import com.scholarscore.etl.ITranslateCollection;
import com.scholarscore.models.Address;
import com.scholarscore.models.Gender;
import com.scholarscore.models.user.Student;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

/**
 * Created by mattg on 7/3/15.
 */
@JsonDeserialize(using = StudentsDeserializer.class)
public class PsStudents extends ArrayList<PsStudent> implements ITranslateCollection<Student> {
    private static final String YES = "Yes";

    private static String SPED_FLAG;
    private static String ELL_FLAG;

    public String setSPED_FLAG(String sf) {
        return SPED_FLAG = sf;
    }

    public String setELL_FLAG(String ef) {
        return ELL_FLAG = ef;
    }

    @Override
    public Collection<Student> toInternalModel() {

        Collection<Student> response = new ArrayList<>();

        this.forEach(student -> {
            Student model = new Student();
            if (null != student.name) {
                model.setName(student.name.toString());
            }
            if(null != SPED_FLAG &&
                    null != ELL_FLAG &&
                    null != student._extension_data &&
                    null != student._extension_data._table_extension &&
                    null != student._extension_data._table_extension._field) {
                PsExtensionField spedData = student._extension_data._table_extension._field.get(SPED_FLAG);
                if(null != spedData) {
                    model.setSped(YES.equals(spedData.value));
                }
                PsExtensionField ellData = student._extension_data._table_extension._field.get(ELL_FLAG);
                if(null != ellData) {
                    model.setEll(YES.equals(ellData.value));
                }
            }
            model.setSourceSystemId(student.id.toString());
            model.setSourceSystemUserId(student.local_id.toString());
            if (null != student.school_enrollment) {
                model.setCurrentSchoolId(student.school_enrollment.school_id);
                if (null != student.school_enrollment.exit_date) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(student.school_enrollment.exit_date);
                    Long projectedGraduationYear = (long)cal.get(Calendar.YEAR);
                    model.setProjectedGraduationYear(projectedGraduationYear);
                }
            }

            if (null != student.addresses) {
                if (null != student.addresses.physical) {
                    model.setHomeAddress(new Address());
                    model.getHomeAddress().setStreet(student.addresses.physical.street);
                    model.getHomeAddress().setCity(student.addresses.physical.city);
                    model.getHomeAddress().setState(student.addresses.physical.state_province);
                    model.getHomeAddress().setPostalCode(student.addresses.physical.postal_code);
                }
                if (null != student.addresses.mailing) {
                    model.setMailingAddress(new Address());
                    model.getMailingAddress().setStreet(student.addresses.mailing.street);
                    model.getMailingAddress().setCity(student.addresses.mailing.city);
                    model.getMailingAddress().setState(student.addresses.mailing.state_province);
                    model.getMailingAddress().setPostalCode(student.addresses.mailing.postal_code);
                }
            }
            if (null != student.ethnicity_race) {
                model.setFederalEthnicity(student.ethnicity_race.federal_ethnicity);
                if(null != student.ethnicity_race.races) {
                    model.setFederalRace(student.ethnicity_race.races.district_race_code);
                }
            }
            if (null != student.demographics) {
                if (null != student.demographics.gender) {
                    switch (student.demographics.gender) {
                        case "M":
                            model.setGender(Gender.MALE);
                            break;
                        case "F":
                            model.setGender(Gender.FEMALE);
                            break;
                        case "O":
                        default:
                            model.setGender(Gender.OTHER);
                            break;
                    }
                }
                model.setBirthDate(student.demographics.birth_date);
                model.setDistrictEntryDate(student.demographics.district_entry_date);
            }
            model.setEnabled(true);
            response.add(model);
        });

        return response;
    }
}
