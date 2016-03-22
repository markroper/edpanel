package com.scholarscore.etl.powerschool.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.scholarscore.client.BaseHttpClient;
import com.scholarscore.client.HttpClientException;
import com.scholarscore.etl.powerschool.api.response.PsResponse;
import org.apache.http.client.methods.HttpGet;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by markroper on 11/1/15.
 */
public abstract class PowerSchoolHttpClient extends BaseHttpClient {
    public PowerSchoolHttpClient(URI uri) {
        super(uri);
    }

    @SuppressWarnings("unchecked")
    protected <T> T get(TypeReference<T> typeRef,  String path, Integer pageSize, String ...params) throws HttpClientException {
        boolean makeRequest = true;
        T returnVal = null;
        Integer currentPage = 1;
        
        //No matter the powerschool query, we add a param that is page= to all queries to handle pagination.
        if(null == params) {
            params = new String[]{ currentPage.toString() };
        } else {
            // make a copy of the array
            String[] newParams = Arrays.copyOf(params, params.length + 1);
            // append the additional param - pageNum - to the params at the end
            newParams[newParams.length - 1] = currentPage.toString();
            params = newParams;
        }
        final int pageParamIndex = params.length - 1;

        // if this is the first param, append with '?', else append with '&'
        String conjunction = (!path.contains("?")) ? "?" : "&";
        path = path + conjunction + PowerSchoolPaths.PAGE_NUM_PARAM_NAME + "={" + pageParamIndex + "}";

        String unadulteratedPath = path;
        while(makeRequest) {
            makeRequest = false;
            //No matter the powerschool query, we add a param that is page= to all queries to handle pagination.
            path = getPath(unadulteratedPath, params);
            try {
                HttpGet get = new HttpGet();
                setupCommonHeaders(get);
                get.setURI(uri.resolve(path));
                String json = getJSON(get);
                T tempVal = MAPPER.readValue(json,typeRef);
                
                if(null == returnVal) {
                    returnVal = tempVal;
                }
                
                List returnList = null;
                List tempList = null;
                //If we're dealing with a list, handle pagination...
                if (tempVal instanceof PsResponse || tempVal instanceof ArrayList) {
                    if (tempVal instanceof PsResponse) {
                        tempList = ((PsResponse) tempVal).record;
                        returnList = ((PsResponse) returnVal).record;
                    } else if (tempVal instanceof ArrayList) {
                        tempList = (List) tempVal;
                        returnList = (List) returnVal;
                    }

                    if (!currentPage.equals(1)) { returnList.addAll(tempList); }
                    
                    if (pageSize.equals(tempList.size())) {
                        makeRequest = true;
                        currentPage++;
                        params[pageParamIndex] = currentPage.toString();
                    }
                }
                
            } catch (IOException e) {
                throw new HttpClientException(e);
            }
        }
        return returnVal;
    }

}
