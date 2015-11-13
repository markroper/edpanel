package com.scholarscore.etl.runner;

import com.scholarscore.etl.DLETLEngine;
import com.scholarscore.etl.ETLEngine;
import com.scholarscore.etl.IETLEngine;
import com.scholarscore.etl.SyncResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * User: jordan
 * Date: 11/12/15
 * Time: 5:56 PM
 */
public class EtlRunner {

    private ETLEngine etlEngine;

    private DLETLEngine dlEtlEngine;

    // Test migrates everything
    public void migrateDistrict() {
        System.out.println("Migration running...");
        SyncResult psResult = etlEngine.syncDistrict();
        System.out.println("Done! PS Migration result: " + psResult);
        
        dlEtlEngine.syncDistrict();
        SyncResult dlResult = dlEtlEngine.syncDistrict();
        System.out.println("Done! Migration result: " + dlResult);    
    }

    public ETLEngine getEtlEngine() {
        return etlEngine;
    }

    public void setEtlEngine(ETLEngine etlEngine) {
        this.etlEngine = etlEngine;
    }

    public DLETLEngine getDlEtlEngine() {
        return dlEtlEngine;
    }

    public void setDlEtlEngine(DLETLEngine dlEtlEngine) {
        this.dlEtlEngine = dlEtlEngine;
    }
}
