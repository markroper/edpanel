package com.scholarscore.etl.powerschool.api.model.student;

/**
 * At the moment, functionally identical tos PsTableStudent. 
 * Only separated for quickness, readability and in case one class changes in the future.
 * 
 * User: jordan
 * Date: 3/27/16
 * Time: 11:19 AM
 */
public class PsTableSection {
    public long id;        // this ID is *only* available from the low-level (i.e. 'table') API call for students
    public long dcid;      // this (dc)ID is what other views call simply "id", which is incredibly confusing
}
