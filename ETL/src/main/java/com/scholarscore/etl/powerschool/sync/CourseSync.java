package com.scholarscore.etl.powerschool.sync;

import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.powerschool.api.model.PsCourses;
import com.scholarscore.etl.powerschool.client.IPowerSchoolClient;
import com.scholarscore.models.Course;
import com.scholarscore.models.School;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by markroper on 10/27/15.
 */
public class CourseSync implements ISync<Course> {
    protected IAPIClient edPanel;
    protected IPowerSchoolClient powerSchool;
    protected School school;

    public CourseSync(IAPIClient edPanel,
                     IPowerSchoolClient powerSchool,
                     School s) {
        this.edPanel = edPanel;
        this.powerSchool = powerSchool;
        this.school = s;
    }

    @Override
    public ConcurrentHashMap<Long, Course> synchCreateUpdateDelete() {
        ConcurrentHashMap<Long, Course> source = resolveAllFromSourceSystem();
        ConcurrentHashMap<Long, Course> edpanel = resolveFromEdPanel();
        Iterator<Map.Entry<Long, Course>> sourceIterator = source.entrySet().iterator();
        //Find & perform the inserts and updates, if any
        while(sourceIterator.hasNext()) {
            Map.Entry<Long, Course> entry = sourceIterator.next();
            Course sourceCourse = entry.getValue();
            Course edPanelCourse = edpanel.get(entry.getKey());
            if(null == edPanelCourse){
                Course created = edPanel.createCourse(school.getId(), sourceCourse);
                sourceCourse.setId(created.getId());
            } else {
                sourceCourse.setId(edPanelCourse.getId());
                sourceCourse.setSchool(edPanelCourse.getSchool());
                if(!edPanelCourse.equals(sourceCourse)) {
                    edPanel.replaceCourse(school.getId(), sourceCourse);
                }
            }
        }
        //Delete anything IN EdPanel that is NOT in source system
        Iterator<Map.Entry<Long, Course>> edpanelIterator = edpanel.entrySet().iterator();
        while(edpanelIterator.hasNext()) {
            Map.Entry<Long, Course> entry = edpanelIterator.next();
            if(!source.containsKey(entry.getKey())) {
                edPanel.deleteCourse(school.getId(), entry.getValue());
            }
        }
        return source;
    }


    protected ConcurrentHashMap<Long, Course> resolveAllFromSourceSystem() {
        ConcurrentHashMap<Long, Course> result = new ConcurrentHashMap<>();
        PsCourses response = powerSchool.getCoursesBySchool(Long.valueOf(school.getSourceSystemId()));
        Collection<Course> apiListOfCourses = response.toInternalModel();
        for(Course c: apiListOfCourses) {
            result.put(Long.valueOf(c.getSourceSystemId()), c);
        }
        return result;
    }

    protected ConcurrentHashMap<Long, Course> resolveFromEdPanel() {
        Course[] courses = edPanel.getCourses(school.getId());
        ConcurrentHashMap<Long, Course> courseMap = new ConcurrentHashMap<>();
        for(Course c: courses) {
            Long id = null;
            String ssid = c.getSourceSystemId();
            if(null != ssid) {
                id = Long.valueOf(ssid);
                courseMap.put(id, c);
            }
        }
        return courseMap;
    }
}
