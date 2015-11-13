package com.scholarscore.etl.powerschool.api.model.term;

import com.google.common.collect.BiMap;

import java.util.HashMap;

/**
 * Another day, another seedy underbelly.  PowerSchool (the mainline SIS) and PowerTeacher
 * (Grade-related vassal system) share a physical database, but have different database tables for the
 * same entities. For a few reasons, we need to pull related data out of each of the two systems.
 * Therefore we need an ID associator for rows to map the following interrelated physical Power* database
 * tables:
 * <p/>
 * PowerSchool: Term -> TermBins
 * PowerTeacher: PSM_Term -> PSM_ReportingTerms
 * PowerTeacher: SYNC_termsmap
 * PowerTeacher: SYNC_ReportingTermsMap (associated PSM_ReportingTerms & TermBins)
 * <p/>
 * The Sync tables associate the IDs of a table from PowerSchool and a table from PowerTeacher.
 * Querying all 6 of these tables, we build a set of BiMaps so that we can freely associate between these
 * entities, which we need to do to migrate StudentSectionGrades and GradeFormulas.
 *
 * Created by markroper on 11/12/15.
 */
public class TermAssociator {
    protected BiMap<Long, Long> ptTermIdToPsTermId;
    protected BiMap<Long, Long> ptReportingTermIdToPsTermBinId;
    protected HashMap<Long, Long> psTermBinIdToPsTermId;

    private TermAssociator() {
    }

    /*
        Resolve the PowerTeacher termId from reportingTermId, powerSchoolTermId, or termBinId
     */
    public Long getPtTermIdFromPsTermId(Long psTermId) {
        return ptTermIdToPsTermId.inverse().get(psTermId);
    }

    public Long getPtTermIdFromTermBinId(Long termBinId) {
        Long powerSchoolTermId = psTermBinIdToPsTermId.get(termBinId);
        return getPtTermIdFromPsTermId(powerSchoolTermId);
    }

    public Long getPtTermIdFromReportingTermId(Long reportingTermId) {
        Long powerSchoolTermBinId = ptReportingTermIdToPsTermBinId.get(reportingTermId);
        return getPtTermIdFromTermBinId(powerSchoolTermBinId);
    }

    /*
        Resolve the PowerSchool termId from termBinId, reportingTermId or powerTeacherTermId
     */
    public Long getPsTermIdFromPtTermId(Long ptTermId) {
        return ptTermIdToPsTermId.get(ptTermId);
    }

    public Long getPsTermIdFromTermBinId(Long termBinId) {
        return psTermBinIdToPsTermId.get(termBinId);
    }

    public Long getPsTermIdFromReportingTermId(Long reportingTermId) {
        Long termBinId = ptReportingTermIdToPsTermBinId.get(reportingTermId);
        if(null == termBinId) {
            return null;
        }
        return getPsTermIdFromTermBinId(termBinId);
    }
    public static class TermAssociatorBuilder {
        protected BiMap<Long, Long> ptTermIdToPsTermId;
        protected BiMap<Long, Long> ptReportingTermIdToPsTermBinId;
        protected HashMap<Long, Long> psTermBinIdToPsTermId;

        public TermAssociatorBuilder(){
        }

        public TermAssociatorBuilder withPtTermIdToPsTermId(final BiMap<Long,Long> ptpsid){
            this.ptTermIdToPsTermId = ptpsid;
            return this;
        }

        public TermAssociatorBuilder withPtReportingTermIdToPsTermBinId(
                final BiMap<Long,Long> ptReportingTermIdToPsTermBinId){
            this.ptReportingTermIdToPsTermBinId = ptReportingTermIdToPsTermBinId;
            return this;
        }

        public TermAssociatorBuilder withPsTermBinIdToPsTermId(final HashMap<Long, Long> psTermBinIdToPsTermId){
            this.psTermBinIdToPsTermId = psTermBinIdToPsTermId;
            return this;
        }

        public TermAssociator build(){
            TermAssociator s = new TermAssociator();
            s.ptReportingTermIdToPsTermBinId = ptReportingTermIdToPsTermBinId;
            s.ptTermIdToPsTermId = ptTermIdToPsTermId;
            s.psTermBinIdToPsTermId = psTermBinIdToPsTermId;
            return s;
        }
    }

}
