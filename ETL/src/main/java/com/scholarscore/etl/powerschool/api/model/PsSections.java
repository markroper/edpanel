package com.scholarscore.etl.powerschool.api.model;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by mattg on 7/3/15.
 */
@XmlRootElement(name = "sections")
public class PsSections {
    public List<PsSection> section;
}
