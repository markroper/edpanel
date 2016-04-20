package com.scholarscore.etl.runner;

import com.scholarscore.etl.DlEtlEngine;
import com.scholarscore.etl.PsEtlEngine;
import com.scholarscore.etl.SyncResult;
import com.scholarscore.etl.kickboard.KickboardEtl;
import com.scholarscore.etl.schoolbrains.SchoolBrainsEngine;

/**
 * User: jordan
 * Date: 11/12/15
 * Time: 5:56 PM
 */
public class EtlRunner {
    // SIS system - either/or 
    private PsEtlEngine psEtlEngine;
    private SchoolBrainsEngine sbEtlEngine;
    
    // behavior system - either/or
    private DlEtlEngine dlEtlEngine;
    private KickboardEtl kickboardEtlEngine;

    // Test migrates everything
    public void migrateDistrict(EtlSettings settings) {
        System.out.println("Migration running...");
        
        // TODO: add interface, standardize, iterate -- we are doing essentially the same high-level thing
        // to each of [whatever sync engines are currently active]
        
        SyncResult psResult = psEtlEngine.syncDistrict(settings);
        System.out.println("Done! PS Migration result: " + psResult);

        SyncResult sbResult = sbEtlEngine.syncDistrict(settings);
        System.out.println("Done! SB Migration result: " + sbResult);

        SyncResult dlResult = dlEtlEngine.syncDistrict(settings);
        System.out.println("Done! Migration result: " + dlResult);
        
        if(null != kickboardEtlEngine.getEnabled() && kickboardEtlEngine.getEnabled()) {
            kickboardEtlEngine.setStudentAssociator(psEtlEngine.getStudentAssociator());
            kickboardEtlEngine.setStaffAssociator(psEtlEngine.getStaffAssociator());
            SyncResult kickboardResult = kickboardEtlEngine.syncDistrict(settings);
            System.out.println("Done migrating from KickBoard! Migration result: " + kickboardResult);
        }
        
        // TODO: give all active engines a generic hook if they want to do post-sync actions
        psEtlEngine.triggerNotificationEvaluation();
        System.out.println("Notification evaluation complete!");
    }

    public void setPsEtlEngine(PsEtlEngine psEtlEngine) {
        this.psEtlEngine = psEtlEngine;
    }

    public void setDlEtlEngine(DlEtlEngine dlEtlEngine) {
        this.dlEtlEngine = dlEtlEngine;
    }

    public void setKickboardEtlEngine(KickboardEtl kickboardEtlEngine) {
        this.kickboardEtlEngine = kickboardEtlEngine;
    }

    public void setSbEtlEngine(SchoolBrainsEngine sbEtlEngine) {
        this.sbEtlEngine = sbEtlEngine;
    }
}
