package com.scholarscore.etl.runner;

import java.io.File;

/**
 * Created by mattg on 11/24/15.
 */
public class EtlSettings {
    private File gpaImportFile;

    public File getGpaImportFile() {
        return gpaImportFile;
    }

    public void setGpaImportFile(File gpaImportFile) {
        this.gpaImportFile = gpaImportFile;
    }
}
