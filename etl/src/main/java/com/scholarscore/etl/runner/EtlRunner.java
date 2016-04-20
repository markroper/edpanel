package com.scholarscore.etl.runner;

import com.scholarscore.etl.DlEtlEngine;
import com.scholarscore.etl.PsEtlEngine;
import com.scholarscore.etl.SyncResult;
import com.scholarscore.etl.kickboard.KickboardEtl;

/**
 * User: jordan
 * Date: 11/12/15
 * Time: 5:56 PM
 */
public class EtlRunner {
    // SIS system - either/or 
    private PsEtlEngine psEtlEngine;
    
    // behavior system - either/or
    private DlEtlEngine dlEtlEngine;
    private KickboardEtl kickboardEtlEngine;

    // Test migrates everything
    public void migrateDistrict(EtlSettings settings) {
        System.out.println("Migration running...");
        SyncResult psResult = psEtlEngine.syncDistrict(settings);
        if (psResult != null) {
            System.out.println("Done! PS Migration result: " + psResult);
        } else {
            
        }
        SyncResult dlResult = dlEtlEngine.syncDistrict(settings);
        System.out.println("Done! Migration result: " + dlResult);
        if(null != kickboardEtlEngine.getEnabled() && kickboardEtlEngine.getEnabled()) {
            kickboardEtlEngine.setStudentAssociator(psEtlEngine.getStudentAssociator());
            kickboardEtlEngine.setStaffAssociator(psEtlEngine.getStaffAssociator());
            SyncResult kickboardResult = kickboardEtlEngine.syncDistrict(settings);
            System.out.println("Done migrating from KickBoard! Migration result: " + kickboardResult);
        }
        psEtlEngine.triggerNotificationEvaluation();
        System.out.println("Notification evaluation complete!");
    }

    public PsEtlEngine getPsEtlEngine() {
        return psEtlEngine;
    }

    public void setPsEtlEngine(PsEtlEngine psEtlEngine) {
        this.psEtlEngine = psEtlEngine;
    }

    public DlEtlEngine getDlEtlEngine() {
        return dlEtlEngine;
    }

    public void setDlEtlEngine(DlEtlEngine dlEtlEngine) {
        this.dlEtlEngine = dlEtlEngine;
    }

    public KickboardEtl getKickboardEtlEngine() {
        return kickboardEtlEngine;
    }

    public void setKickboardEtlEngine(KickboardEtl kickboardEtlEngine) {
        this.kickboardEtlEngine = kickboardEtlEngine;
    }
}
