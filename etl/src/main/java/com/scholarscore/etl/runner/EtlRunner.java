package com.scholarscore.etl.runner;

import com.scholarscore.etl.DlEtlEngine;
import com.scholarscore.etl.EtlEngine;
import com.scholarscore.etl.SyncResult;
import com.scholarscore.etl.kickboard.KickboardEtl;

/**
 * User: jordan
 * Date: 11/12/15
 * Time: 5:56 PM
 */
public class EtlRunner {
    private EtlEngine etlEngine;
    private DlEtlEngine dlEtlEngine;
    private KickboardEtl kickboardEtlEngine;

    // Test migrates everything
    public void migrateDistrict(EtlSettings settings) {
        System.out.println("Migration running...");
        SyncResult psResult = etlEngine.syncDistrict(settings);
        System.out.println("Done! PS Migration result: " + psResult);
        SyncResult dlResult = dlEtlEngine.syncDistrict(settings);
        System.out.println("Done! Migration result: " + dlResult);
        if(null != kickboardEtlEngine.getEnabled() && kickboardEtlEngine.getEnabled()) {
            kickboardEtlEngine.setStudentAssociator(etlEngine.getStudentAssociator());
            kickboardEtlEngine.setStaffAssociator(etlEngine.getStaffAssociator());
            SyncResult kickboardResult = kickboardEtlEngine.syncDistrict(settings);
            System.out.println("Done migrating from KickBoard! Migration result: " + kickboardResult);
        }
        etlEngine.triggerNotificationEvaluation();
        System.out.println("Notification evaluation complete!");
    }

    public EtlEngine getEtlEngine() {
        return etlEngine;
    }

    public void setEtlEngine(EtlEngine etlEngine) {
        this.etlEngine = etlEngine;
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
