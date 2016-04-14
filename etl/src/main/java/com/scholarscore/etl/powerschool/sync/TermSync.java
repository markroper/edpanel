package com.scholarscore.etl.powerschool.sync;

import com.scholarscore.client.HttpClientException;
import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.ISync;
import com.scholarscore.etl.PowerSchoolSyncResult;
import com.scholarscore.etl.powerschool.api.model.term.PsTerm;
import com.scholarscore.etl.powerschool.api.response.TermResponse;
import com.scholarscore.etl.powerschool.client.IPowerSchoolClient;
import com.scholarscore.models.School;
import com.scholarscore.models.SchoolYear;
import com.scholarscore.models.Term;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by markroper on 10/26/15.
 */
public class TermSync extends SyncBase<Term> implements ISync<Term> {
    private final static Logger LOGGER = LoggerFactory.getLogger(TermSync.class);
    protected IAPIClient edPanel;
    protected IPowerSchoolClient powerSchool;
    protected School school;
    //Since powerschool doesn't have a first class 'year', the key here is the 4-digit starting year
    protected Map<Long, SchoolYear> sourceSchoolYears = new ConcurrentHashMap<>();
    protected Map<Long, SchoolYear> edpanelSchoolYears = new ConcurrentHashMap<>();

    public TermSync(IAPIClient edPanel,
                    IPowerSchoolClient powerSchool,
                    School s) {
        this.edPanel = edPanel;
        this.powerSchool = powerSchool;
        this.school = s;
    }

    @Override
    protected ConcurrentHashMap<Long, Term> resolveAllFromSourceSystem() throws HttpClientException {
        //Get all the terms from PowerSchool for the current School
        String sourceSystemIdString = school.getSourceSystemId();
        Long sourceSystemSchoolId = new Long(sourceSystemIdString);
        TermResponse tr = powerSchool.getTermsBySchoolId(sourceSystemSchoolId);
        ConcurrentHashMap<Long, Term> sourceTerms =
                new ConcurrentHashMap<>();
        if(null != tr && null != tr.terms && null != tr.terms.term) {
            Map<Long, List<Term>> yearToTerms = new HashMap<>();
            List<PsTerm> terms = tr.terms.term;
            //First we build up the term and deduce from this, how many school years there are...
            for(PsTerm t : terms) {
                Term edpanelTerm = new Term();
                edpanelTerm.setStartDate(t.getStart_date());
                edpanelTerm.setEndDate(t.getEnd_date());
                edpanelTerm.setName(t.getName());
                edpanelTerm.setPortion(t.getPortion());
                edpanelTerm.setSourceSystemId(t.getId().toString());
                if(null == yearToTerms.get(t.getStart_year())) {
                    yearToTerms.put(t.getStart_year(), Collections.synchronizedList(new ArrayList<>()));
                }
                yearToTerms.get(t.getStart_year()).add(edpanelTerm);
            }
            //Then we create the needed school years in EdPanel given the terms from PowerSchool
            for (Map.Entry<Long, List<Term>> entry : yearToTerms.entrySet()) {
                SchoolYear schoolYear = new SchoolYear();
                schoolYear.setSchool(school);
                schoolYear.setName(entry.getKey().toString());
                //For each school year, we need to set the start & end dates as the smallest
                //of the terms' start dates and the largest of the terms' end dates
                for (Term t : entry.getValue()) {
                    if (null == schoolYear.getStartDate() ||
                            schoolYear.getStartDate().compareTo(t.getStartDate()) > 0) {
                        schoolYear.setStartDate(t.getStartDate());
                    }
                    if (null == schoolYear.getEndDate() ||
                            schoolYear.getEndDate().compareTo(t.getEndDate()) < 0) {
                        schoolYear.setEndDate(t.getEndDate());
                    }
                }
                this.sourceSchoolYears.put(new Long(schoolYear.getName()), schoolYear);
            }
            //Finally, having created the EdPanel SchoolYears, we can create the terms in EdPanel
            for (Map.Entry<Long, List<Term>> entry : yearToTerms.entrySet()) {
                for (Term t : entry.getValue()) {
                    //Now that the school Year has been created, cache it on the
                    SchoolYear y = sourceSchoolYears.get(entry.getKey());
                    t.setSchoolYear(y);
                    sourceTerms.put(new Long(t.getSourceSystemId()), t);
                }
            }
        }
        return sourceTerms;
    }

    @Override
    protected void handleSourceGetFailure(PowerSchoolSyncResult results) {
        LOGGER.error("Unable to resolve terms from PowerSchool for school: " + school.getName() +
                " with EdPanel ID " + school.getId());
        results.termSourceGetFailed(Long.valueOf(school.getSourceSystemId()), school.getId());
    }

    @Override
    protected ConcurrentHashMap<Long, Term> resolveFromEdPanel() throws HttpClientException {
        SchoolYear[] years = edPanel.getSchoolYears(school.getId());
        ConcurrentHashMap<Long, Term> termMap = new ConcurrentHashMap<>();
        for(SchoolYear year: years) {
            try {
                this.edpanelSchoolYears.put(Long.valueOf(year.getName()), year);
            } catch(NumberFormatException | NullPointerException e) {
                //noop
            }
            Term[] terms = edPanel.getTerms(school.getId(), year.getId());
            for(Term t: terms) {
                t.setSchoolYear(year);
                termMap.put(Long.valueOf(t.getSourceSystemId()), t);
            }
        }
        return termMap;
    }

    @Override
    protected void handleEdPanelGetFailure(PowerSchoolSyncResult results) {
        LOGGER.error("Unable to resolve terms from EdPanel for school: " + school.getName() +
                " with EdPanel ID " + school.getId());
        results.termEdPanelGetFailed(Long.valueOf(school.getSourceSystemId()), school.getId());
    }

    @Override
    protected void createEdPanelRecord(Term entityToSave, PowerSchoolSyncResult results) {
        Long ssid = Long.parseLong(entityToSave.getSourceSystemId());
        if (!this.edpanelSchoolYears.containsKey(new Long(entityToSave.getSchoolYear().getName()))) {
            SchoolYear createdYear = null;
            try {
                createdYear = edPanel.createSchoolYear(school.getId(), entityToSave.getSchoolYear());
                // school years don't have SSIDs so use term SSID
                results.yearCreated(ssid, createdYear.getId());  
            } catch (HttpClientException e) {
                results.yearCreateFailed(ssid);
                return;
            }
            entityToSave.getSchoolYear().setId(createdYear.getId());
        }
        Term created;
        try {
            created = edPanel.createTerm(school.getId(), entityToSave.getSchoolYear().getId(), entityToSave);
            entityToSave.setId(created.getId());
            results.termCreated(ssid, created.getId());
        } catch (HttpClientException e) {
            results.termCreateFailed(ssid);
        }
    }

    @Override
    protected void updateEdPanelRecord(Term sourceSystemEntity, Term edPanelEntity, PowerSchoolSyncResult results) {
        Long ssid = Long.parseLong(sourceSystemEntity.getSourceSystemId());
        sourceSystemEntity.setId(edPanelEntity.getId());
        sourceSystemEntity.getSchoolYear().setId(edPanelEntity.getSchoolYear().getId());
        //Don't compare terms, which won't be set on the source school year
        edPanelEntity.getSchoolYear().setTerms(new ArrayList<>());
        if (!edPanelEntity.equals(sourceSystemEntity)) {
            //Create/update school year if needed
            if (!edPanelEntity.getSchoolYear().equals(sourceSystemEntity.getSchoolYear())) {
                if (!this.edpanelSchoolYears.containsKey(new Long(sourceSystemEntity.getSchoolYear().getName()))) {
                    //create school year
                    SchoolYear createdYear = null;
                    try {
                        createdYear = edPanel.createSchoolYear(school.getId(), sourceSystemEntity.getSchoolYear());
                    } catch (HttpClientException e) {
                        results.termUpdateFailed(ssid, sourceSystemEntity.getId());
                        return;
                    }
                    sourceSystemEntity.getSchoolYear().setId(createdYear.getId());
                } else {
                    sourceSystemEntity.setSchoolYear(
                            this.edpanelSchoolYears.get(
                                    new Long(sourceSystemEntity.getSchoolYear().getName())));
                }
            }
            try {
                edPanel.updateTerm(school.getId(), sourceSystemEntity.getSchoolYear().getId(), sourceSystemEntity);
                results.termUpdated(ssid, sourceSystemEntity.getId());
            } catch (HttpClientException e) {
                results.termUpdateFailed(ssid, sourceSystemEntity.getId());
            }
        }
    }

    @Override
    protected void deleteEdPanelRecord(Term entityToDelete, PowerSchoolSyncResult results) {
        Long ssid = Long.parseLong(entityToDelete.getSourceSystemId());
        try {
            edPanel.deleteTerm(
                    school.getId(),
                    entityToDelete.getSchoolYear().getId(),
                    entityToDelete);
            results.termDeleted(ssid, entityToDelete.getId());
        } catch (HttpClientException e) {
            results.termDeleteFailed(ssid, entityToDelete.getId());
        }
    }
}
