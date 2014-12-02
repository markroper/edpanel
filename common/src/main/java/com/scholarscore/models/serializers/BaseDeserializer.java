package com.scholarscore.models.serializers;

/**
 * User: jordan
 * Date: 11/30/14
 * Time: 10:44 PM
 *
 *
 * This base class holds an instance of an object of type T which can be returned
 * to subclasses. Subclass deserializers should specify the most specific type for T
 * that they are concerned with populating, and this object will be accessible to the subclass
 * via a call to getObject(). This is to allow subclasses to guarantee the object received
 * is of the type being populated with no instanceof checking required.
 *
 * Any classes that extend this class must supply a new instance of the object that they
 * are concerned with deserializing into via the newInstance() call.
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
