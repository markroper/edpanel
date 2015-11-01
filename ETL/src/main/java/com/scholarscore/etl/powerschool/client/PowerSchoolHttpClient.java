package com.scholarscore.etl.powerschool.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.scholarscore.client.BaseHttpClient;
import com.scholarscore.client.HttpClientException;
import com.scholarscore.etl.powerschool.api.response.PsResponse;
import org.apache.http.client.methods.HttpGet;

import java.io.IOException;
import java.net.URI;
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
        String unadulteredPath = path;
        while(makeRequest) {
            makeRequest = false;
            //No matter the powerschool query, we add a param that is page= to all queries to handle pagination.
            if(null == params) {
                params = new String[]{ currentPage.toString() };
            } else {
                String[] newParams = new String[params.length + 1];
                newParams[0] = currentPage.toString();
                for(int i = 0; i < params.length; i++) {
                    newParams[i + 1] = params[i];
                }
                params = newParams;
            }
            path = getPath(unadulteredPath, params);
            try {
                HttpGet get = new HttpGet();
                setupCommonHeaders(get);
                get.setURI(uri.resolve(path));
                String json = getJSON(get);
                T tempVal = mapper.readValue(json,typeRef);
                if(null == returnVal) {
                    returnVal = tempVal;
                }
                //If we're dealing with a list, handle pagination...
                if(tempVal instanceof PsResponse) {
                    List tempList = ((PsResponse)tempVal).record;
                    //If we have exactly the page size number of results, try again to see if there is another page!
                    if(pageSize.equals(tempList.size())) {
                        makeRequest = true;
                        currentPage++;
                        ((PsResponse)returnVal).record.addAll(tempList);
                    }
                }
            } catch (IOException e) {
                throw new HttpClientException(e);
            }
        }
        return returnVal;
    }
}
