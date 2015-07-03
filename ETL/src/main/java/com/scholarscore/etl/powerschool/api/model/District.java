package com.scholarscore.etl.powerschool.api.model;

import java.util.List;

/**
 * Created by mattg on 6/28/15.
 */
public class District {
    private String uuid;
    private String name;
    private String district_number;
//    private List<Address> addresses;
//
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
//    private List<Phone> phones;
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
