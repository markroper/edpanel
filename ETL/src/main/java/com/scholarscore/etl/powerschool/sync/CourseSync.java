package com.scholarscore.etl.powerschool.sync;

import com.scholarscore.client.HttpClientException;
import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.SyncResult;
import com.scholarscore.etl.powerschool.api.model.PsCourses;
import com.scholarscore.etl.powerschool.client.IPowerSchoolClient;
import com.scholarscore.models.Course;
import com.scholarscore.models.School;

import java.io.IOException;
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
    public ConcurrentHashMap<Long, Course> syncCreateUpdateDelete(SyncResult results) {
        ConcurrentHashMap<Long, Course> source = null;
        try {
            source = resolveAllFromSourceSystem();
        } catch (HttpClientException e) {
            results.courseSourceGetFailed(Long.valueOf(school.getSourceSystemId()), school.getId());
            return new ConcurrentHashMap<>();
        }
        ConcurrentHashMap<Long, Course> edpanel = null;
        try {
            edpanel = resolveFromEdPanel();
        } catch (HttpClientException e) {
            results.courseEdPanelGetFailed(Long.valueOf(school.getSourceSystemId()), school.getId());
            return new ConcurrentHashMap<>();
        }
        Iterator<Map.Entry<Long, Course>> sourceIterator = source.entrySet().iterator();
        //Find & perform the inserts and updates, if any
        while(sourceIterator.hasNext()) {
            Map.Entry<Long, Course> entry = sourceIterator.next();
            Course sourceCourse = entry.getValue();
            Course edPanelCourse = edpanel.get(entry.getKey());
            if(null == edPanelCourse){
                Course created = null;
                try {
                    created = edPanel.createCourse(school.getId(), sourceCourse);
                } catch (HttpClientException e) {
                    results.courseCreateFailed(entry.getKey());
                    continue;
                }
                sourceCourse.setId(created.getId());
                results.courseCreated(entry.getKey(), sourceCourse.getId());
            } else {
                sourceCourse.setId(edPanelCourse.getId());
                sourceCourse.setSchool(edPanelCourse.getSchool());
                if(!edPanelCourse.equals(sourceCourse)) {
                    try {
                        edPanel.replaceCourse(school.getId(), sourceCourse);
                    } catch (IOException e) {
                        results.courseUpdateFailed(entry.getKey(), sourceCourse.getId());
                        continue;
                    }
                    results.courseUpdated(entry.getKey(), sourceCourse.getId());
                }
            }
        }
        //Delete anything IN EdPanel that is NOT in source system
        Iterator<Map.Entry<Long, Course>> edpanelIterator = edpanel.entrySet().iterator();
        while(edpanelIterator.hasNext()) {
            Map.Entry<Long, Course> entry = edpanelIterator.next();
            if(!source.containsKey(entry.getKey())) {
                try {
                    edPanel.deleteCourse(school.getId(), entry.getValue());
                } catch (HttpClientException e) {
                    results.courseDeleteFailed(entry.getKey(), entry.getValue().getId());
                    continue;
                }
                results.courseDeleted(entry.getKey(), entry.getValue().getId());
            }
        }
        return source;
    }


    protected ConcurrentHashMap<Long, Course> resolveAllFromSourceSystem() throws HttpClientException {
        ConcurrentHashMap<Long, Course> result = new ConcurrentHashMap<>();
        PsCourses response = powerSchool.getCoursesBySchool(Long.valueOf(school.getSourceSystemId()));
        Collection<Course> apiListOfCourses = response.toInternalModel();
        for(Course c: apiListOfCourses) {
            result.put(Long.valueOf(c.getSourceSystemId()), c);
        }
        return result;
    }

    protected ConcurrentHashMap<Long, Course> resolveFromEdPanel() throws HttpClientException {
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
