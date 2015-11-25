package com.scholarscore.models.gpa;

import java.util.ArrayList;

/**
 * Created by markroper on 11/24/15.
 */
public class GpaList extends ArrayList<Gpa> {
    public GpaList() {
        super();
    }

    public GpaList(ArrayList<Gpa> gpas) {
        super(gpas);
    }

    public GpaList(GpaList gpas) {
        super(gpas);
    }
}
