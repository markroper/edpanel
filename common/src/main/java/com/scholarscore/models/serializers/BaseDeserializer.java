package com.scholarscore.models.serializers;

/**
 * User: jordan
 * Date: 11/30/14
 * Time: 10:44 PM
 */
public abstract class BaseDeserializer<T> {

    private T object;

    protected T getObject() {
        if (object == null) {
            object = newInstance();
        }
        return object;
    }

    public abstract T newInstance();
}
