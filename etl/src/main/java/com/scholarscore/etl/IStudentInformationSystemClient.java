package com.scholarscore.etl;

import com.scholarscore.client.HttpClientException;
import com.scholarscore.models.School;

/**
 * User: jordan
 * Date: 4/15/16
 * Time: 7:09 PM
 */
public interface IStudentInformationSystemClient {

    ITranslateCollection<School> getSchools() throws HttpClientException;
}
