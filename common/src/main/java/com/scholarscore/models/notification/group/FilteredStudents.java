package com.scholarscore.models.notification.group;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.scholarscore.models.Gender;
import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.user.Student;
import com.scholarscore.util.EdPanelObjectMapper;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Transient;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * Created by markroper on 1/9/16.
 */
@Entity
@DiscriminatorValue(value = "FILTERED_STUDENTS")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class FilteredStudents extends NotificationGroup<Student> {
    private Gender gender;
    private List<String> federalRaces;
    private List<String> federalEthnicities;
    private List<Long> projectedGraduationYears;
    private Boolean englishLanguageLearner;
    private Boolean specialEducationStudent;
    private List<Long> districtEntryYears;
    private List<Long> birthYears;

    @JsonIgnore
    @Column(name = HibernateConsts.NOTIFICATION_GROUP_FILTER, columnDefinition="blob")
    public String getFilterJson() {
        try {
            return EdPanelObjectMapper.MAPPER.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @JsonIgnore
    public void setFilterJson(String json) {
        try {
            FilteredStudents fs = EdPanelObjectMapper.MAPPER.readValue(json, FilteredStudents.class);
            this.gender = fs.gender;
            this.federalRaces = fs.federalRaces;
            this.federalEthnicities = fs.federalEthnicities;
            this.projectedGraduationYears = fs.projectedGraduationYears;
            this.englishLanguageLearner = fs.englishLanguageLearner;
            this.specialEducationStudent = fs.specialEducationStudent;
            this.districtEntryYears = fs.districtEntryYears;
            this.birthYears = fs.birthYears;
        } catch (IOException e) {
            //no op
        }
    }


    @Transient
    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    @Transient
    public List<String> getFederalRaces() {
        return federalRaces;
    }

    public void setFederalRaces(List<String> federalRaces) {
        this.federalRaces = federalRaces;
    }

    @Transient
    public List<String> getFederalEthnicities() {
        return federalEthnicities;
    }

    public void setFederalEthnicities(List<String> federalEthnicities) {
        this.federalEthnicities = federalEthnicities;
    }

    @Transient
    public List<Long> getProjectedGraduationYears() {
        return projectedGraduationYears;
    }

    public void setProjectedGraduationYears(List<Long> projectedGraduationYears) {
        this.projectedGraduationYears = projectedGraduationYears;
    }

    @Transient
    public Boolean getEnglishLanguageLearner() {
        return englishLanguageLearner;
    }

    public void setEnglishLanguageLearner(Boolean englishLanguageLearner) {
        this.englishLanguageLearner = englishLanguageLearner;
    }

    @Transient
    public Boolean getSpecialEducationStudent() {
        return specialEducationStudent;
    }

    public void setSpecialEducationStudent(Boolean specialEducationStudent) {
        this.specialEducationStudent = specialEducationStudent;
    }

    @Transient
    public List<Long> getDistrictEntryYears() {
        return districtEntryYears;
    }

    public void setDistrictEntryYears(List<Long> districtEntryYears) {
        this.districtEntryYears = districtEntryYears;
    }

    @Transient
    public List<Long> getBirthYears() {
        return birthYears;
    }

    public void setBirthYears(List<Long> birthYears) {
        this.birthYears = birthYears;
    }

    @Override
    public NotificationGroupType getType() {
        return NotificationGroupType.FILTERED_STUDENTS;
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
