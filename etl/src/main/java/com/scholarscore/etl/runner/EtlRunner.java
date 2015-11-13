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
    public void migrateDistrict() {
        System.out.println("Migration running...");
        SyncResult psResult = etlEngine.syncDistrict();
        System.out.println("Done! PS Migration result: " + psResult);
        
        dlEtlEngine.syncDistrict();
        SyncResult dlResult = dlEtlEngine.syncDistrict();
        System.out.println("Done! Migration result: " + dlResult);    
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
