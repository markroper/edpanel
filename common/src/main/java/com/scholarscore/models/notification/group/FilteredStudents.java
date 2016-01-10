package com.scholarscore.models.notification.group;

import com.scholarscore.models.Gender;
import com.scholarscore.models.user.Student;

import java.util.List;
import java.util.Objects;

/**
 * Created by markroper on 1/9/16.
 */
public class FilteredStudents extends NotificationGroup<Student> {
    private Gender gender;
    private List<String> federalRaces;
    private List<String> federalEthnicities;
    private List<Long> projectedGraduationYears;
    private Boolean englishLanguageLearner;
    private Boolean specialEducationStudent;
    private List<Long> districtEntryYears;
    private List<Long> birthYears;

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public List<String> getFederalRaces() {
        return federalRaces;
    }

    public void setFederalRaces(List<String> federalRaces) {
        this.federalRaces = federalRaces;
    }

    public List<String> getFederalEthnicities() {
        return federalEthnicities;
    }

    public void setFederalEthnicities(List<String> federalEthnicities) {
        this.federalEthnicities = federalEthnicities;
    }

    public List<Long> getProjectedGraduationYears() {
        return projectedGraduationYears;
    }

    public void setProjectedGraduationYears(List<Long> projectedGraduationYears) {
        this.projectedGraduationYears = projectedGraduationYears;
    }

    public Boolean getEnglishLanguageLearner() {
        return englishLanguageLearner;
    }

    public void setEnglishLanguageLearner(Boolean englishLanguageLearner) {
        this.englishLanguageLearner = englishLanguageLearner;
    }

    public Boolean getSpecialEducationStudent() {
        return specialEducationStudent;
    }

    public void setSpecialEducationStudent(Boolean specialEducationStudent) {
        this.specialEducationStudent = specialEducationStudent;
    }

    public List<Long> getDistrictEntryYears() {
        return districtEntryYears;
    }

    public void setDistrictEntryYears(List<Long> districtEntryYears) {
        this.districtEntryYears = districtEntryYears;
    }

    public List<Long> getBirthYears() {
        return birthYears;
    }

    public void setBirthYears(List<Long> birthYears) {
        this.birthYears = birthYears;
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(gender, federalRaces, federalEthnicities,
                projectedGraduationYears, englishLanguageLearner, specialEducationStudent,
                districtEntryYears, birthYears);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        final FilteredStudents other = (FilteredStudents) obj;
        return Objects.equals(this.gender, other.gender)
                && Objects.equals(this.federalRaces, other.federalRaces)
                && Objects.equals(this.federalEthnicities, other.federalEthnicities)
                && Objects.equals(this.projectedGraduationYears, other.projectedGraduationYears)
                && Objects.equals(this.englishLanguageLearner, other.englishLanguageLearner)
                && Objects.equals(this.specialEducationStudent, other.specialEducationStudent)
                && Objects.equals(this.districtEntryYears, other.districtEntryYears)
                && Objects.equals(this.birthYears, other.birthYears);
    }
}
