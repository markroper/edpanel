package com.scholarscore.etl.schoolbrains;

import java.io.File;
import java.util.List;

/**
 * Created by markroper on 4/14/16.
 */
public abstract class BaseParser<T> {
    protected File input;
    public BaseParser(File file) {
        this.input = file;
    }

    public abstract List<T> parse();
}
