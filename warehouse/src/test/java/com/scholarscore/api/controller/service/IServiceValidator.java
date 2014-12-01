package com.scholarscore.api.controller.service;

import java.io.Serializable;

import org.springframework.http.HttpStatus;

import com.scholarscore.api.controller.base.IntegrationBase;

/**
 * Every resource for which there is a REST API endpoint should have a class capable of
 * calling the endpoints for that resource and validating what is returned in positive 
 * and in negative cases.  This interface defines the API actions that should be validated
 * by each implementation of this interface.
 * 
 * @author markroper
 *
 * @param <T> The class that represents the resource whose API the implementer 
 *      calls and verifies responses from
 */
public interface IServiceValidator<T extends Serializable> {
    
    /**
     * Should return the IntegrationBase instance that the service uses to make
     * REST API calls.
     * 
     * @return An IntegrationBase instance
     */
    public IntegrationBase getServiceBase();
    
    /**
     * Implementations should attempt to retrieve the entity with ID equal 
     * to the id paramter by making an HTTP GET call for the resource.
     * 
     * @param id
     * @param msg
     * @return
     */
    public T get(Long id, String msg);
    
    /**
     * Implementations should attempt to retrieve the resouce with ID 
     * equal to the id parameter and should assert that the retrieval fails
     * with the HTTP status returned by the API equal to the expectedCode
     * parameter
     * 
     * @param id
     * @param expectedCode
     * @param msg
     * @return
     */
    public void getNegative(Long id, HttpStatus expectedCode, String msg);
    
    /**
     * Implementations should attempt to create an instance of T by making an HTTP 
     * POST request and assert that the entity is created as expected by making 
     * an HTTP GET request for the resource subsequent to the POST. Returns the 
     * instance retrieved via GET.
     * 
     * @param entity The entity to create
     * @param msg A descriptive message of the test case, for use in debugging
     * @return The created instance, retrieved from the API via GET
     */
    public T create(T entity, String msg);
    
    /**
     * Implementations should attempt to create an instance of T by making an HTTP post
     * request and assert that the the entity is not created by validating that the 
     * HttpStatus returned is equal to the expectedCode provided as a parameter.
     * 
     * @param assignment The entity to create
     * @param expectedCode The HTTP code expected to be returned by the API
     * @param msg A descriptive message of the test case, for use in debugging
     */
    public void createNegative(T entity, HttpStatus expectedCode, String msg);
    
    /**
     * Implementations should replace the entity with ID equal to the id parameter with 
     * the value of the entity parameter by making an HTTP PUT. Assert that the replace 
     * succeeded by retrieving the entity via an HTTP GET. Return the instance retrieved 
     * via GET.
     * 
     * @param id The id of the entity to replace
     * @param entity The replacement entity
     * @param msg A descriptive message of the test case, for use in debugging
     * @return The created instance, retrieved from the API via GET
     */
    public T replace(Long id, T entity, String msg);
    
    /**
     * Implementations should fail to replace the entity with ID equal to the id 
     * parameter with the value of the entity parameter by making an HTTP PUT call 
     * and asserting that the call returns an HttpStatus equal to the parameter
     * expectedCode.
     * 
     * @param id The id of the entity to attempt to replace
     * @param entity The replacement entity
     * @param expectedCode The HTTP code expected to be returned by the API
     * @param msg A descriptive message of the test case for use in debugging
     */
    public void replaceNegative(Long id, T entity, HttpStatus expectedCode, String msg);
    
    /**
     * Implementations should to update the entity with ID equal to the id parameter 
     * using the value of the entity parameter by making an HTTP PATCH call.  PATCH 
     * should update the persisted entity, setting any non-null attributes from the 
     * update entity on the persisted entity, and leaving the other attributes unchanged. 
     * Implementations should assert that the PATCH was successful by executing an HTTP 
     * GET and interrogating the result.
     * 
     * @param id The id of the entity to update
     * @param entity The update entity
     * @param msg A descriptive message of the test case for use in debugging
     * @return The updated entity, retrieved via an HTTP GET
     */
    public T update(Long id, T entity, String msg);
    
    /**
     * Implementations should fail to update the entity with ID equal to the id 
     * parameter using the value of the entity parameter by making an HTTP PATCH 
     * and asserting that the API returns the HTTP status code equal to the
     * expectedCode parameter.
     *  
     * @param id The id of the entity to attempt to replace
     * @param entity The replacement entity
     * @param expectedCode The HTTP code expected to be returned by the API
     * @param msg A descriptive message of the test case for use in debugging
     */
    public void updateNegative(Long id, T entity, HttpStatus expectedCode, String msg);
    
    /**
     * Implementations should delete the entity with ID equal to the id parameter
     * and assert that the delete was successful by making an HTTP GET call
     * subsequent to the DELETE and receiving a 404 HTTP status code in the response.
     * 
     * @param id The ID of the entity to delete
     * @param msg A descriptive message of the test case for use in debugging
     */
    public void delete(Long id, String msg);
    
    /**
     * Implementations should fail to delete an entity with ID equal to the id
     * parameter and assert that the DELETE fails by comparing the HttpStatus returned
     * by the API call and the expected code parameter.
     * 
     * @param id The ID of the entity to delete
     * @param expectedCode The expected API status HTTP return code
     * @param msg A descriptive message of the test case for use in debugging
     */
    public void deleteNegative(Long id, HttpStatus expectedCode, String msg);
}
