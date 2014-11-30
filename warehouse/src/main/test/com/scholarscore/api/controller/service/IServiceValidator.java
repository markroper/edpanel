package com.scholarscore.api.controller.service;

import java.io.Serializable;
import org.springframework.http.HttpStatus;

public interface IServiceValidator<T extends Serializable> {
    /**
     * Create an instance of T by HTTP POST, assert that the entity is created as
     * expected by calling HTTP GET subsequent to the POST. Returns the instance 
     * retrieved via GET.
     * 
     * @param entity The entity to create
     * @param msg A descriptive message of the test case, for use in debugging
     * @return The created instance, retrieved from the API via GET
     */
    public T create(T entity, String msg);
    
    /**
     * Attempt to create an instance of T by HTTP post.  Asserts that the entity is 
     * not created and that the HttpStatus returned matches the expectedCode passed in
     * as a parameter.
     * 
     * @param assignment The entity to create
     * @param expectedCode The HTTP code expected to be returned by the API
     * @param msg A descriptive message of the test case, for use in debugging
     */
    public void createNegative(T entity, HttpStatus expectedCode, String msg);
    
    /**
     * Replace the entity with ID equal to the id parameter with the value 
     * of the entity parameter by calling HTTP PUT. Assert that the replace took place 
     * by calling GET to retrieve the instance after the PUT has taken place. Return
     * the instance retrieved via GET.
     * 
     * @param id The id of the entity to replace
     * @param entity The replacement entity
     * @param msg A descriptive message of the test case, for use in debugging
     * @return The created instance, retrieved from the API via GET
     */
    public T replace(Long id, T entity, String msg);
    
    /**
     * Attempt to replace the entity with ID equal to the id parameter with the value 
     * of the entity parameter by calling HTTP PUT. Assert that the replace does not
     * take place and that the PUT call returns the HttpStatus equal to the parameter
     * expectedCode.
     * 
     * @param id The id of the entity to attempt to replace
     * @param entity The replacement entity
     * @param expectedCode The HTTP code expected to be returned by the API
     * @param msg A descriptive message of the test case for use in debugging
     */
    public void replaceNegative(Long id, T entity, HttpStatus expectedCode, String msg);
    
    /**
     * Attempt to update the entity with ID equal to the id parameter using the value
     * of the entity parameter by making an HTTP PATCH call.  Patch should update the 
     * persisted entity, setting any non-null attributes from the update entity onto 
     * the persisted entity, and leaving the other attributes unchanged. Implementations
     * should assert that the PATCH was successful by executing an HTTP GET and 
     * interrogating the result.
     * 
     * @param id The id of the entity to update
     * @param entity The update entity
     * @param msg A descriptive message of the test case for use in debugging
     * @return The updated entity, retrieved via an HTTP GET
     */
    public T update(Long id, T entity, String msg);
    
    /**
     * Attempt to update the entity with ID equal to the id parameter using the value
     * of the entity parameter by making an HTTP PATCH call.  Asserts that the PATCH 
     * is unsuccessful and that the API returns the HTTP status code equal to the
     * expectedCode parameter.
     *  
     * @param id The id of the entity to attempt to replace
     * @param entity The replacement entity
     * @param expectedCode The HTTP code expected to be returned by the API
     * @param msg A descriptive message of the test case for use in debugging
     */
    public void updateNegative(Long id, T entity, HttpStatus expectedCode, String msg);
    
    /**
     * 
     * @param id
     * @param msg
     */
    public void delete(Long id, String msg);
    
    public void deleteNegative(Long id, HttpStatus expectedCode, String msg);
}
