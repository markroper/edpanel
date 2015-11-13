package com.scholarscore.etl.powerschool.api.model;

/**
 * Created by mattg on 6/28/15.
 */
public class PsDistrict {
    public String uuid;
    public String name;
    public String district_number;

    public PsAddresses addresses;

    public PsPhones phones;
//    private List<PsDistrictRaceCode> raceCodes;
//
//    private List<DistrictOfResidence> residences;
//
//    private List<PsEntryCode> entryCodes;
//
//    private EthnicityRaceDeclineToSpecify ethnicityRaceDeclineToSpecify;
//
//    private Boolean raceAllowToDecline;
//    private String raceAllowToDeclineLabel;
//
//    private List<ExitCode> exitCodes;
//
//    private List<PsFederalRaceCategory> federalRaceCategories;
//
//    private List<FeesPaymentMethod> feesPaymentMethods;
//
//
//    private List<PsPhones> phones;
//
//    private List<SchedulingReportingEthnicity> schedulingReportingEthnicities;

    @Override
    public String toString() {
        return "PsDistrict{" +
                "uuid='" + uuid + '\'' +
                ", name='" + name + '\'' +
                ", district_number='" + district_number + '\'' +
                '}';
    }
}
