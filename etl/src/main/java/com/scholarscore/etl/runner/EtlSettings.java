package com.scholarscore.etl.runner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mattg on 11/24/15.
 */
public class EtlSettings {
    private List<File> gpaImportFiles = new ArrayList<>();

    public List<File> getGpaImportFiles() {
        return gpaImportFiles;
    }

    public void setGpaImportFiles(List<File> gpaImportFiles) {
        this.gpaImportFiles = gpaImportFiles;
    }
}
