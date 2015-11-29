package com.scholarscore.etl.deanslist.api.response;

import com.scholarscore.etl.deanslist.api.model.DlBehavior;
import com.scholarscore.etl.powerschool.api.response.ITranslateCollection;
import com.scholarscore.models.BehaviorCategory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * Created by jwinch on 7/23/15.
 */
public class BehaviorResponse implements Serializable, ITranslateCollection<com.scholarscore.models.Behavior> {

    private final static Logger logger = LoggerFactory.getLogger(BehaviorResponse.class);
    
    protected static final String DEANSLIST_SOURCE = "deanslist";
    private static DateTimeFormatter sdf =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public Integer rowcount;
    public HashSet<DlBehavior> data;

    private static final String IN = "in";
    private static final String SCHOOL = "school";
    private static final String SUSPENSION = "suspension";
    private static final String DETENTION = "detention";
    private static final String DEMERIT = "demerit";
    private static final String MERIT = "merit";

    @Override
    public Collection<com.scholarscore.models.Behavior> toInternalModel() {
        ArrayList<com.scholarscore.models.Behavior> toReturn = new ArrayList<>();

        for (DlBehavior dlBehavior : data) {
            com.scholarscore.models.Behavior out = new com.scholarscore.models.Behavior();
            out.setRemoteSystem(DEANSLIST_SOURCE);
            out.setRemoteStudentId(dlBehavior.DLStudentID);
            out.setRemoteBehaviorId(dlBehavior.DLSAID);
            
            // we parse the category name down to a known enum but don't keep the raw
            // category name in the category field, so appending it to name so that no
            // data is lost.
            String behaviorName;
            if (StringUtils.isEmpty(dlBehavior.Behavior)) {
                behaviorName = "";
            } else {
                behaviorName = dlBehavior.Behavior;
            }
            if (!StringUtils.isEmpty(dlBehavior.BehaviorCategory)) {
                behaviorName = dlBehavior.BehaviorCategory + " " + behaviorName;
            }
            out.setName(behaviorName);
            
            BehaviorCategory parsedCategory = determineBehaviorCategory(dlBehavior.BehaviorCategory);
            if (parsedCategory == null) {
                logger.warn("WARNING Could not parse category. Skipping...");
            }
            out.setBehaviorCategory(parsedCategory);
            out.setBehaviorDate(LocalDate.parse(dlBehavior.BehaviorDate, sdf));

            // mostly-empty student with just student name (it's all we have)
            com.scholarscore.models.user.Student student = new com.scholarscore.models.user.Student();
            student.setName(getStudentName(dlBehavior));
            out.setStudent(student);
            // mostly-empty teacher with just teacher name (it's all we have)
            com.scholarscore.models.user.Teacher teacher = new com.scholarscore.models.user.Teacher();
            teacher.setName(getStaffName(dlBehavior));
            out.setTeacher(teacher);

            out.setPointValue(dlBehavior.PointValue);
            
            out.setRoster(dlBehavior.Roster);
            toReturn.add(out);
        }
        return toReturn;
    }
    
    // the conversion to the BehaviorCategory enum from 'whatever data has been jammed into deanslist' 
    // is best effort - it'll work in the 'default' Deanslist configuration but we need to do some
    // best-effort guessing for the cases where they've changed the names
    private BehaviorCategory determineBehaviorCategory(String behaviorCategoryString) {
        if (behaviorCategoryString == null) {
            return null;
        }
        String lowercased = behaviorCategoryString.toLowerCase();
        if (lowercased.contains(SUSPENSION) &&
                lowercased.contains(IN) &&
                lowercased.contains(SCHOOL)) {
            return BehaviorCategory.IN_SCHOOL_SUSPENSION;
        } else if (lowercased.contains(SUSPENSION)) {
            return BehaviorCategory.OUT_OF_SCHOOL_SUSPENSION;
        } else if (lowercased.contains(DETENTION)) {
            return BehaviorCategory.DETENTION;
        } else if (lowercased.contains(DEMERIT)) {
            return BehaviorCategory.DEMERIT;
        } else if (lowercased.contains(MERIT)) {
            return BehaviorCategory.MERIT;
        }
        return BehaviorCategory.OTHER;
    }
  
    // TODO Jordan: revisit middle names (may be a clue in more advanced matching)
    private String getStudentName(DlBehavior dlBehavior) { 
        return (StringUtils.isEmpty(dlBehavior.StudentFirstName) ? "" : dlBehavior.StudentFirstName.trim() + " ")
//                + (StringUtils.isEmpty(dlBehavior.StudentMiddleName) ? "" : dlBehavior.StudentMiddleName + " ")
                + (StringUtils.isEmpty(dlBehavior.StudentLastName) ? "" : dlBehavior.StudentLastName).trim();
    }
    
    private String getStaffName(DlBehavior dlBehavior) { 
        return
                (StringUtils.isEmpty(dlBehavior.StaffFirstName) ? "" : dlBehavior.StaffFirstName.trim() + " ")
//                + (StringUtils.isEmpty(dlBehavior.StaffMiddleName) ? "" : dlBehavior.StaffMiddleName + " ")
                + (StringUtils.isEmpty(dlBehavior.StaffLastName) ? "" : dlBehavior.StaffLastName).trim();
        
    }

}
