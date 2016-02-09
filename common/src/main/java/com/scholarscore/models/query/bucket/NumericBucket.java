package com.scholarscore.models.query.bucket;

/**
 * Created by markroper on 2/9/16.
 */
public class NumericBucket extends AggregationBucket<Double> {
    public static final String BUCKET_TYPE = "NUMERIC";

    public NumericBucket() {
        super();
    }

    public NumericBucket(Double start, Double end, String label) {
        super(start, end, label);
    }
}
