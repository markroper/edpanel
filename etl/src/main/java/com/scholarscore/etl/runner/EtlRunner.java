package com.scholarscore.etl.runner;

import com.scholarscore.etl.DlEtlEngine;
import com.scholarscore.etl.EtlEngine;
import com.scholarscore.etl.SyncResult;

/**
 * User: jordan
 * Date: 11/12/15
 * Time: 5:56 PM
 */
public class EtlRunner {

    private EtlEngine etlEngine;

    private DlEtlEngine dlEtlEngine;

    // Test migrates everything
    public void migrateDistrict(EtlSettings settings) {
        System.out.println("Migration running...");
//        SyncResult psResult = etlEngine.syncDistrict(settings);
//        System.out.println("Done! PS Migration result: " + psResult);
        SyncResult dlResult = dlEtlEngine.syncDistrict(settings);
        System.out.println("Done! Migration result: " + dlResult);
//        etlEngine.triggerNotificationEvaluation();
//        System.out.println("Notification evaluation complete!");
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
}
