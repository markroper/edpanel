package com.scholarscore.models.query.bucket;

import java.time.LocalDate;

/**
 * Created by markroper on 2/9/16.
 */
public class DateBucket extends AggregationBucket<LocalDate> {
    public static final String BUCKET_TYPE = "DATE";
}
