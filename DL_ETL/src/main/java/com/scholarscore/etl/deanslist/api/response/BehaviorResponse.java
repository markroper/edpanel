package com.scholarscore.etl.deanslist.api.response;

import com.scholarscore.etl.deanslist.api.model.Behavior;
import com.scholarscore.etl.powerschool.api.response.ITranslateCollection;
import com.scholarscore.models.BehaviorCategory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by jwinch on 7/23/15.
 */
public class BehaviorResponse implements ITranslateCollection<com.scholarscore.models.Behavior> {

    private final static Logger logger = LoggerFactory.getLogger(BehaviorResponse.class);
    
    protected static final String DEANSLIST_SOURCE = "deanslist";
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    Integer rowcount;
    Set<Behavior> data;

    private static final String IN = "in";
    private static final String SCHOOL = "school";
    private static final String SUSPENSION = "suspension";
    private static final String DETENTION = "detention";
    private static final String DEMERIT = "demerit";
    private static final String MERIT = "merit";

    @Override
    public Collection<com.scholarscore.models.Behavior> toInternalModel() {
        ArrayList<com.scholarscore.models.Behavior> toReturn = new ArrayList<>();

        for (Behavior behavior : data) {
            com.scholarscore.models.Behavior out = new com.scholarscore.models.Behavior();
            out.setRemoteSystem(DEANSLIST_SOURCE);
            out.setRemoteStudentId(behavior.DLStudentID);
            out.setRemoteBehaviorId(behavior.DLSAID);
            
            // we parse the category name down to a known enum but don't keep the raw
            // category name in the category field, so appending it to name so that no
            // data is lost.
            String behaviorName;
            if (StringUtils.isEmpty(behavior.Behavior)) {
                behaviorName = "";
            } else {
                behaviorName = behavior.Behavior;
            }
            if (!StringUtils.isEmpty(behavior.BehaviorCategory)) {
                behaviorName = behavior.BehaviorCategory + " " + behaviorName;
            }
            out.setName(behaviorName);
            
            BehaviorCategory parsedCategory = determineBehaviorCategory(behavior.BehaviorCategory);
            if (parsedCategory == null) {
                logger.warn("WARNING Could not parse category. Skipping...");
            }
            out.setBehaviorCategory(parsedCategory);
            try {
                out.setBehaviorDate(sdf.parse(behavior.BehaviorDate));
            } catch (ParseException pe) {
                logger.warn("WARNING Could not parse date. Skipping...");
            }

            // mostly-empty student with just student name (it's all we have)
            com.scholarscore.models.user.Student student = new com.scholarscore.models.user.Student();
            student.setName(getStudentName(behavior));
            out.setStudent(student);
            // mostly-empty teacher with just teacher name (it's all we have)
            com.scholarscore.models.user.Teacher teacher = new com.scholarscore.models.user.Teacher();
            teacher.setName(getStaffName(behavior));
            out.setTeacher(teacher);

            out.setRoster(behavior.Roster);
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
    
    private String getStudentName(Behavior behavior) { 
        return (StringUtils.isEmpty(behavior.StudentFirstName) ? "" : behavior.StudentFirstName + " ")
                + (StringUtils.isEmpty(behavior.StudentMiddleName) ? "" : behavior.StudentMiddleName + " ")
                + (StringUtils.isEmpty(behavior.StudentLastName) ? "" : behavior.StudentLastName).trim();
    }
    
    private String getStaffName(Behavior behavior) { 
        return
                (StringUtils.isEmpty(behavior.StaffFirstName) ? "" : behavior.StaffFirstName + " ")
                + (StringUtils.isEmpty(behavior.StaffMiddleName) ? "" : behavior.StaffMiddleName + " ")
                + (StringUtils.isEmpty(behavior.StaffLastName) ? "" : behavior.StaffLastName).trim();
        
    }
}
