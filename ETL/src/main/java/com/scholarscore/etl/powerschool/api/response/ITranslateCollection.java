package com.scholarscore.etl.powerschool.api.response;

import com.scholarscore.models.IApiModel;
import com.scholarscore.models.Student;

import java.util.Collection;
import java.util.List;

/**
 * Created by mattg on 7/3/15.
 */
public interface ITranslateCollection<T extends IApiModel<?>> {

    Collection<Student> toInternalModel();
}
