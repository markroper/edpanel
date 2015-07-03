package com.scholarscore.etl.powerschool.api.model;

import java.util.List;

/**
 * Created by mattg on 6/28/15.
 */
public class District {
    public String uuid;
    public String name;
    public String district_number;

    public Addresses addresses;

    public Phones phones;
//    private List<DistrictRaceCode> raceCodes;
//
//    private List<DistrictOfResidence> residences;
//
//    private List<EntryCode> entryCodes;
//
//    private EthnicityRaceDeclineToSpecify ethnicityRaceDeclineToSpecify;
//
//    private Boolean raceAllowToDecline;
//    private String raceAllowToDeclineLabel;
//
//    private List<ExitCode> exitCodes;
//
//    private List<FederalRaceCategory> federalRaceCategories;
//
//    private List<FeesPaymentMethod> feesPaymentMethods;
//
//
//    private List<Phones> phones;
//
//    private List<SchedulingReportingEthnicity> schedulingReportingEthnicities;

    @Override
    public String toString() {
        return "District{" +
                "uuid='" + uuid + '\'' +
                ", name='" + name + '\'' +
                ", district_number='" + district_number + '\'' +
                '}';
    }
}
