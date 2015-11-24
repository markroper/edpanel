package com.scholarscore.etl.runner;

import com.scholarscore.etl.DlEtlEngine;
import com.scholarscore.etl.EtlEngine;
import com.scholarscore.etl.SyncResult;
import org.apache.commons.cli.*;

import java.io.File;

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
        SyncResult psResult = etlEngine.syncDistrict();
        System.out.println("Done! PS Migration result: " + psResult);

        File gpaFile = settings.getGpaImportFile();
        if (null != gpaFile && gpaFile.canRead() && gpaFile.isFile()) {
            // parse the gpa file from disk assuming the file type is CSV and of a specific format
        }
        
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
