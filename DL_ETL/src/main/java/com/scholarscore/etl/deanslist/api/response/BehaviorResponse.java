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

/**
 * Created by jwinch on 7/23/15.
 */
public class BehaviorResponse implements ITranslateCollection<com.scholarscore.models.Behavior> {

    private final static Logger logger = LoggerFactory.getLogger(BehaviorResponse.class);
    
    protected static final String DEANSLIST_SOURCE = "deanslist";
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    Integer rowcount;
    List<Behavior> data = new ArrayList<>();

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
            String behaviorName = StringUtils.isEmpty(behavior.Behavior) ? "" : behavior.Behavior;
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
            com.scholarscore.models.Student student = new com.scholarscore.models.Student();
            student.setName(getStudentName(behavior));
            out.setStudent(student);
            // mostly-empty teacher with just teacher name (it's all we have)
            com.scholarscore.models.Teacher teacher = new com.scholarscore.models.Teacher();
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
        if (lowercased.contains("suspension") &&
                lowercased.contains("in") &&
                lowercased.contains("school")) {
            return BehaviorCategory.IN_SCHOOL_SUSPENSION;
        } else if (lowercased.contains("suspension")) {
            return BehaviorCategory.OUT_OF_SCHOOL_SUSPENSION;
        } else if (lowercased.contains("detention")) { 
            return BehaviorCategory.DETENTION;
        } else if (lowercased.contains("demerit")) {
            return BehaviorCategory.DEMERIT;
        } else if (lowercased.contains("merit")) {
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
