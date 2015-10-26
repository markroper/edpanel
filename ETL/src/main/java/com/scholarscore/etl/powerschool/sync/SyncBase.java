package com.scholarscore.etl.powerschool.sync;

import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.powerschool.client.IPowerSchoolClient;
import com.scholarscore.models.ApiModel;

/**
 * Created by markroper on 10/26/15.
 */
public abstract class SyncBase<T extends ApiModel> implements ISync<T> {
    protected IAPIClient edPanel;
    protected IPowerSchoolClient powerSchool;
    public SyncBase(IAPIClient edPanel, IPowerSchoolClient powerSchool) {
        this.edPanel = edPanel;
        this.powerSchool = powerSchool;
    }
}
