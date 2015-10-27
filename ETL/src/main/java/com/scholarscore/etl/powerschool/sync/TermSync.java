package com.scholarscore.etl.powerschool.sync;

import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.powerschool.api.model.PsTerm;
import com.scholarscore.etl.powerschool.api.response.TermResponse;
import com.scholarscore.etl.powerschool.client.IPowerSchoolClient;
import com.scholarscore.models.School;
import com.scholarscore.models.SchoolYear;
import com.scholarscore.models.Term;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by markroper on 10/26/15.
 */
public class TermSync implements ISync<Term> {
    protected IAPIClient edPanel;
    protected IPowerSchoolClient powerSchool;
    protected School school;
    //Since powerschool doesnt have a first class 'year', the key here is the 4-digit starting year
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
    public ConcurrentHashMap<Long, Term> synchCreateUpdateDelete() {
        ConcurrentHashMap<Long, Term> source = resolveAllFromSourceSystem();
        ConcurrentHashMap<Long, Term> edpanel = resolveFromEdPanel();
        Iterator<Map.Entry<Long, Term>> sourceIterator = source.entrySet().iterator();
        //Find & perform the inserts and updates, if any
        while(sourceIterator.hasNext()) {
            Map.Entry<Long, Term> entry = sourceIterator.next();
            Term sourceTerm = entry.getValue();
            Term edPanelTerm = edpanel.get(entry.getKey());
            if(null == edPanelTerm){
                if(!this.edpanelSchoolYears.containsKey(sourceTerm.getSchoolYear().getName())) {
                    //create school year
                    SchoolYear createdYear =
                            edPanel.createSchoolYear(school.getId(), sourceTerm.getSchoolYear());
                    sourceTerm.getSchoolYear().setId(createdYear.getId());
                }
                Term created = edPanel.createTerm(school.getId(), sourceTerm.getSchoolYear().getId(), sourceTerm);
                sourceTerm.setId(created.getId());
            } else {
                sourceTerm.setId(edPanelTerm.getId());
                sourceTerm.getSchoolYear().setId(edPanelTerm.getSchoolYear().getId());
                //Don't compare terms, which won't be set on the source school year
                edPanelTerm.getSchoolYear().setTerms(new ArrayList<>());
                if(!edPanelTerm.equals(sourceTerm)) {
                    //Create/update school year if needed
                    //TODO: startDate and endDate seem to be repeatedly off by one day.
                    if(!edPanelTerm.getSchoolYear().equals(sourceTerm.getSchoolYear())) {
                        if(!this.edpanelSchoolYears.containsKey(sourceTerm.getSchoolYear().getName())) {
                            //create school year
                            SchoolYear createdYear =
                                    edPanel.createSchoolYear(school.getId(), sourceTerm.getSchoolYear());
                            sourceTerm.getSchoolYear().setId(createdYear.getId());
                        } else {
                            sourceTerm.setSchoolYear(
                                    this.edpanelSchoolYears.get(
                                            sourceTerm.getSchoolYear().getName()));
                        }
                    }
                    edPanel.updateTerm(school.getId(), sourceTerm.getSchoolYear().getId(), sourceTerm);
                }
            }
        }
        //Delete anything IN EdPanel that is NOT in source system
        Iterator<Map.Entry<Long, Term>> edpanelIterator = edpanel.entrySet().iterator();
        while(edpanelIterator.hasNext()) {
            Map.Entry<Long, Term> entry = edpanelIterator.next();
            if(!source.containsKey(entry.getKey())) {
                edPanel.deleteTerm(
                        school.getId(),
                        entry.getValue().getSchoolYear().getId(),
                        entry.getValue());
            }
        }
        return source;
    }

    protected ConcurrentHashMap<Long, Term> resolveAllFromSourceSystem() {
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
                edpanelTerm.setSourceSystemId(t.getId().toString());
                if(null == yearToTerms.get(t.getStart_year())) {
                    yearToTerms.put(t.getStart_year(), Collections.synchronizedList(new ArrayList<>()));
                }
                yearToTerms.get(t.getStart_year()).add(edpanelTerm);
            }
            //Then we create the needed school years in EdPanel given the terms from PowerSchool
            Iterator<Map.Entry<Long, List<Term>>> it =
                    yearToTerms.entrySet().iterator();
            while(it.hasNext()) {
                Map.Entry<Long, List<Term>> entry = it.next();
                SchoolYear schoolYear = new SchoolYear();
                schoolYear.setSchool(school);
                schoolYear.setName(entry.getKey().toString());
                //For each school year, we need to set the start & end dates as the smallest
                //of the terms' start dates and the largest of the terms' end dates
                for(Term t: entry.getValue()) {
                    if(null == schoolYear.getStartDate() ||
                            schoolYear.getStartDate().compareTo(t.getStartDate()) > 0) {
                        schoolYear.setStartDate(t.getStartDate());
                    }
                    if(null == schoolYear.getEndDate() ||
                            schoolYear.getEndDate().compareTo(t.getEndDate()) < 0) {
                        schoolYear.setEndDate(t.getEndDate());
                    }
                }
                this.sourceSchoolYears.put(new Long(schoolYear.getName()), schoolYear);
            }
            //Finally, having created the EdPanel SchoolYears, we can create the terms in EdPanel
            it = yearToTerms.entrySet().iterator();
            while(it.hasNext()) {
                Map.Entry<Long, List<Term>> entry = it.next();
                for(Term t: entry.getValue()) {
                    //Now that the school Year has been created, cache it on the
                    SchoolYear y = sourceSchoolYears.get(entry.getKey());
                    t.setSchoolYear(y);
                    sourceTerms.put(new Long(t.getSourceSystemId()), t);
                }
            }
        }
        return sourceTerms;
    }

    protected ConcurrentHashMap<Long, Term> resolveFromEdPanel() {
        SchoolYear[] years = edPanel.getSchoolYears(school.getId());
        ConcurrentHashMap<Long, Term> termMap = new ConcurrentHashMap<>();
        for(SchoolYear year: years) {
            Long fourDigitYear = null;
            try {
                fourDigitYear = Long.valueOf(year.getName());
                this.edpanelSchoolYears.put(fourDigitYear, year);
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
}
