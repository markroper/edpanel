package com.scholarscore.etl;

import com.scholarscore.models.ApiModel;

/**
 * Created by mattg on 7/3/15.
 */
public interface ITranslate<T extends ApiModel> {
    T toEdpanelModel();
}
