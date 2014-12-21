package com.scholarscore.models;

/**
 * Defines the expectations that all API model objects must implement.
 * @author markroper
 *
 */
public interface IApiModel<T> {

    /**
     * The PATCH API endpoint requires that only those attributes the caller
     * wishes to update should be set. Null properties on the submitted instance
     * are set to the values on the previously persisted entity and this method 
     * is the mechanism by which that setting takes place.
     * 
     * @param mergeFrom The entity to merge properties from
     */
    public void mergePropertiesIfNull(T mergeFrom);
}
