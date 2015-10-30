package com.scholarscore.etl.powerschool.api.response;

import java.util.List;

/**
 * Created by markroper on 10/30/15.
 */
public class PsResponse<T> {
    public String name;
    public List<PsResponseInner<T>> record;
}
