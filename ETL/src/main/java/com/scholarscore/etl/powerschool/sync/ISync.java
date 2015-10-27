package com.scholarscore.etl.powerschool.sync;

import com.scholarscore.models.ApiModel;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by markroper on 10/26/15.
 */
public interface ISync<T extends ApiModel> {

    ConcurrentHashMap<Long, T> synchCreateUpdateDelete();

}
