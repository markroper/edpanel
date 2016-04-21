package com.scholarscore.etl.powerschool.sync;

import com.scholarscore.client.HttpClientException;
import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.ISync;
import com.scholarscore.etl.PowerSchoolSyncResult;
import com.scholarscore.etl.powerschool.api.model.PsCourses;
import com.scholarscore.etl.powerschool.client.IPowerSchoolClient;
import com.scholarscore.models.Course;
import com.scholarscore.models.School;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by markroper on 10/27/15.
 * 
 * Course sync object
 */
public class CourseSync extends SyncBase<Course> implements ISync<Course> {
    private final static Logger LOGGER = LoggerFactory.getLogger(CourseSync.class);
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
    protected ConcurrentHashMap<Long, Course> resolveAllFromSourceSystem() throws HttpClientException {
        ConcurrentHashMap<Long, Course> result = new ConcurrentHashMap<>();
        PsCourses response = powerSchool.getCoursesBySchool(Long.valueOf(school.getSourceSystemId()));
        Collection<Course> apiListOfCourses = response.toInternalModel();
        for(Course c: apiListOfCourses) {
            result.put(Long.valueOf(c.getSourceSystemId()), c);
        }
        return result;
    }

    @Override
    protected void handleSourceGetFailure(PowerSchoolSyncResult results) {
        LOGGER.error("Unable to extract courses from PowerSchool for school: " + school.getName() +
                " with EdPanel ID: " + school.getId());
        results.courseSourceGetFailed(Long.valueOf(school.getSourceSystemId()), school.getId());
    }

    @Override
    protected ConcurrentHashMap<Long, Course> resolveFromEdPanel() throws HttpClientException {
        Collection<Course> courses = edPanel.getCourses(school.getId());
        ConcurrentHashMap<Long, Course> courseMap = new ConcurrentHashMap<>();
        for(Course c: courses) {
            String ssid = c.getSourceSystemId();
            if(null != ssid) {
                courseMap.put(Long.valueOf(ssid), c);
            }
        }
        return courseMap;
    }

    @Override
    protected void handleEdPanelGetFailure(PowerSchoolSyncResult results) {
        LOGGER.error("Unable to extract courses from EdPanel for school: " + school.getName() +
                " with EdPanel ID: " + school.getId());
        results.courseEdPanelGetFailed(Long.valueOf(school.getSourceSystemId()), school.getId());
    }

    @Override
    protected void createEdPanelRecord(Course entityToSave, PowerSchoolSyncResult results) {
        Long ssid = Long.parseLong(entityToSave.getSourceSystemId());
        try {
            Course created = edPanel.createCourse(school.getId(), entityToSave);
            entityToSave.setId(created.getId());
            results.courseCreated(ssid, created.getId());
        } catch (HttpClientException e) {
            results.courseCreateFailed(ssid);
        }
    }

    @Override
    protected void updateEdPanelRecord(Course sourceSystemEntity, Course edPanelEntity, PowerSchoolSyncResult results) {
        Long ssid = Long.parseLong(sourceSystemEntity.getSourceSystemId());
        sourceSystemEntity.setId(edPanelEntity.getId());
        sourceSystemEntity.setSchool(edPanelEntity.getSchool());
        if (!edPanelEntity.equals(sourceSystemEntity)) {
            try {
                Course created = edPanel.replaceCourse(school.getId(), sourceSystemEntity);
                results.courseUpdated(ssid, created.getId());
            } catch (HttpClientException e) {
                results.courseUpdateFailed(ssid, sourceSystemEntity.getId());
            }
        } else {
            results.courseUntouched(ssid, sourceSystemEntity.getId());
        }
    }

    @Override
    protected void deleteEdPanelRecord(Course entityToDelete, PowerSchoolSyncResult results) {
        Long ssid = Long.parseLong(entityToDelete.getSourceSystemId());
        try {
            edPanel.deleteCourse(school.getId(), entityToDelete);
            results.courseDeleted(ssid, entityToDelete.getId());
        } catch (HttpClientException e) {
            results.courseDeleteFailed(ssid, entityToDelete.getId());
        }
    }
}
