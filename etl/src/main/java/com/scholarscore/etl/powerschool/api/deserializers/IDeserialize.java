package com.scholarscore.etl.powerschool.api.deserializers;

import java.util.List;

/**
 * A Deserializer that takes a json object structure returned from running a named query and emits a list of type T
 * which is the ApiModel object used for posting a message over to the edpanel API
 *
 * Created by mattg on 10/9/15.
 */
public interface IDeserialize<T> {
    <T> List<T> deserialize(Class<T> clazz, Object jsonStruct);
}
