package com.scholarscore.etl.deanslist.client;

import com.scholarscore.client.BaseHttpClient;

import java.net.URI;

/**
 * Created by jwinch on 7/22/15.
 */
public class DeansListClient extends BaseHttpClient implements IDeansListClient {

    public static final String PATH_GET_STUDENTS = "/api/beta/export/get-students.php";
    public static final String PATH_GET_BEHAVIOR_DATA = "/api/beta/export/get-behavior-data.php";
    
    /*
    public static final String PATH_RESOURCE_SECTION = "/ws/v1/school/{0}/section";
    */
    
    public DeansListClient(URI uri) {
        super(uri);
    }

    @Override
    protected void authenticate() {
        
    }

    @Override
    protected Boolean isAuthenticated() {
        return null;
    }
}
