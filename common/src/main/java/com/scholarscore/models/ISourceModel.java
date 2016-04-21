package com.scholarscore.models;

/**
 * User: jordan
 * Date: 4/20/16
 * Time: 4:23 PM
 * 
 * Define expectations that all 'source' entities (i.e. entities with data partially or fully originating from outside systems)
 * must adhere to.
 * 
 * 'T' defines the Type for the unique identifier that every source system entity should possess a unique value for.
 */
public interface ISourceModel<T> {
    T getSourceSystemId();
}
